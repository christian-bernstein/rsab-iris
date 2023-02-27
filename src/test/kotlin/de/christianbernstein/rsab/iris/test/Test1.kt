package de.christianbernstein.rsab.iris.test

import de.christianbernstein.rsab.iris.Iris
import de.christianbernstein.rsab.iris.packetier.Packet
import de.christianbernstein.rsab.iris.packetier.PacketType
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

fun main(args: Array<String>): Unit = Iris(TestIrisBridge).run {
    a(); b()
}

fun a() {
    IrisTestClient("a", configurator = {
        skipTest = false
    }) {
        test(skip = true) {
            socket.send(Json.encodeToString(Packet(
                type = "set-num",
                packetType = PacketType.SINGLETON,
                conversationID = UUID.randomUUID().toString(),
                data = mapOf("num" to "42")
            )))
        }

        test {
            socket.send(Json.encodeToString(Packet(
                type = "get-num",
                packetType = PacketType.REQUEST,
                conversationID = UUID.randomUUID().toString(),
                data = emptyMap()
            )))
        }
    }
}

fun b() {
    IrisTestClient("b", configurator = {
        skipTest = false
    }) {
        test {
            socket.send(Json.encodeToString(Packet(
                type = "get-num",
                packetType = PacketType.REQUEST,
                conversationID = UUID.randomUUID().toString(),
                data = emptyMap()
            )))
        }
    }
}
