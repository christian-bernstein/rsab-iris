package de.christianbernstein.rsab.iris.server

import de.christianbernstein.rsab.iris.packetier.PacketSubscriberContext

@Suppress("MemberVisibilityCanBePrivate")
open class Endpoint(
    val name: String,
    val handler: PacketSubscriberContext.() -> Unit = {}
) {

    fun handle() {
        this.handler()
    }

    fun pair(): Pair<String, PacketSubscriberContext.() -> Unit> = this.name to this.handler

}
