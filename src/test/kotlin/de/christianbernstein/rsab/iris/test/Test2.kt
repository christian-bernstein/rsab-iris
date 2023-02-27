package de.christianbernstein.rsab.iris.test

import de.christianbernstein.rsab.iris.Iris
import de.christianbernstein.rsab.iris.packetier.Packet
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = Iris(TestIrisBridge).run {
    IrisTestClient {

        // Check if this socket connection is already authenticated
        test {
            socket.send(Json.encodeToString(Packet(
                type = "is-authenticated"
            )))
        }

        // Login should be successful -> Layer switch imminent
        test {
            "username".also { username ->
                socket.send(Json.encodeToString(Packet(
                    type = "authenticate",
                    data = mapOf(
                        "player-name" to username,
                        "token" to this@run.generateUserToken(username)
                    )
                )))
            }
        }

        // Check if this socket connection is already authenticated
        test {
            socket.send(Json.encodeToString(Packet(
                type = "is-authenticated"
            )))
        }
    }
}
