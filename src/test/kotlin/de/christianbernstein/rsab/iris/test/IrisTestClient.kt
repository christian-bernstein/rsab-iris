package de.christianbernstein.rsab.iris.test

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.concurrent.thread

@Suppress("MemberVisibilityCanBePrivate")
class IrisTestClient(
    var name: String = UUID.randomUUID().toString(),
    var path: String = "/main",
    val configurator: IrisTestClient.() -> Unit = {},
    val suite: suspend IrisTestClient.() -> Unit
) {

    lateinit var http: HttpClient

    lateinit var socket: DefaultClientWebSocketSession

    var thread: Thread? = null

    var async: Boolean = true

    var skipTest: Boolean = false

    init {
        this.init()
    }

    private fun init() {
        this.configurator()
        if (this.skipTest) return
        log("Starting client '${this.name}' (async: ${if (this.async) "enabled" else "disabled"})")
        if (this.async) {
            this.thread = thread(start = true) { this.start() }
        } else {
            this.start()
        }
    }

    private fun start() = runBlocking {
        HttpClient { install(WebSockets) }.also { this@IrisTestClient.http = it }.run {
            webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = this@IrisTestClient.path) {
                this@IrisTestClient.socket = this
                val readMessageRoutine = launch { readMessages() }
                val userSuitRoutine = launch { userSuit() }
                readMessageRoutine.join()
                userSuitRoutine.cancelAndJoin()
            }
        }
    }

    private suspend fun DefaultClientWebSocketSession.readMessages() {
        try {
            for (message in incoming) {
                message as? Frame.Text ?: continue
                log("Message received: '${message.readText()}'")
            }
        } catch (e: Exception) {
            log("Error while receiving: ${e.localizedMessage}")
        } finally {
            log("Closing iris server socket connection")
        }
    }

    private suspend fun userSuit() = this.suite()

    suspend fun test(skip: Boolean = false, test: suspend () -> Unit) {
        try {
            if (!skip) test()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun log(message: Any) = LoggerFactory
        .getLogger(this.javaClass)
        .debug("[${this.name}] $message")

    fun error(message: Any) = LoggerFactory
        .getLogger(this.javaClass)
        .error("[${this.name}] $message")
}
