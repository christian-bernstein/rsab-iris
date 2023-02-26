package de.christianbernstein.rsab.iris.web

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession) {

    companion object {
        val lastId = AtomicInteger(0)
    }

    val id = lastId.getAndIncrement()
}
