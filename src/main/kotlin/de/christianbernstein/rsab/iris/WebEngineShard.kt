package de.christianbernstein.rsab.iris

import de.christianbernstein.rsab.iris.packetier.Packet
import de.christianbernstein.rsab.iris.packetier.PacketierNetAdapter
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.LinkedHashSet
import kotlin.concurrent.thread

@Suppress("ExtractKtorModule")
class WebEngineShard(core: Iris) : Shard<WebEngineShard>(core) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    private lateinit var engine: NettyApplicationEngine

    override fun shard(): WebEngineShard = this

    override fun shutdown() {
        super.shutdown()
        this.engine.stop(10, 10, TimeUnit.SECONDS)
    }

    fun generatePacketierBridge(): PacketierNetAdapter = object : PacketierNetAdapter() {

        override fun pub(senderID: String, receiverID: String, packet: Packet): Unit = this@WebEngineShard
            .connections
            .first { it.id.toString() == receiverID }
            .session
            .run {
                launch {
                    this@run.send(Json.encodeToString(packet))
                }
            }

        override fun broadPub(senderID: String, packet: Packet): Unit = Json.encodeToString(packet).let { msg ->
            this@WebEngineShard.connections.forEach {
                it.session.run {
                    launch {
                        this@run.send(msg)
                    }
                }
            }
        }
    }

    override fun init() {
        super.init()
        CompletableDeferred<Unit>().run {
            thread(start = true) {
                embeddedServer(Netty, port = 8080) {
                    install(WebSockets)
                    this@WebEngineShard.initMainSocketRoute(this)
                }.also { this@WebEngineShard.engine = it }.start(wait = false).also {
                    println("Completing web init")
                    this.complete(Unit)
                }
            }
            runBlocking { this@run.await() }
        }
    }

    private fun initMainSocketRoute(application: Application): Unit {
        application.routing {
            webSocket("main") {
                val con = Connection(this)
                try {
                    this@WebEngineShard.onConnectionInit(con)
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        this@WebEngineShard.onMessage(con, frame.readText())
                    }
                } catch (e: Exception) {
                    logger.error("Error while receiving message: '${e.localizedMessage}'")
                    e.printStackTrace()
                } finally {
                    this@WebEngineShard.onConnectionClose(con)
                }
            }
        }
    }

    private fun onMessage(connection: Connection, data: String) {
        this.logger.debug("Message received id='${connection.id}' data='$data'")
        this.core.packetier().engine.handle(
            senderID = connection.id.toString(),
            receiverID = ServerShard.MAIN_SERVER_ID,
            packet = Json.decodeFromString(Packet.serializer(), data)
        )
    }

    private fun onConnectionClose(connection: Connection) {
        this.core.packetier().engine.closeSession(connection.id.toString())
        this.logger.error("Closing socket connection id='${connection.id}'")
        connections -= connection
    }

    private suspend fun onConnectionInit(connection: Connection) {
        this.core.packetier().engine.createSession(connection.id.toString())
        connections += connection

        // TODO: Send login packet
        // connection.session.send("You are connected! There are ${connections.count()} users here.")
    }
}
