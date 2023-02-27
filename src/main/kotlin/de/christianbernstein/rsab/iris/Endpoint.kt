package de.christianbernstein.rsab.iris

import de.christianbernstein.rsab.iris.packetier.PacketSubscriberContext

@Suppress("MemberVisibilityCanBePrivate")
open class Endpoint(
    val name: String,
    val handler: PacketSubscriberContext.() -> Unit = {}
) {

    open fun handle(): PacketSubscriberContext.() -> Unit = this.handler

    fun getServer(): ServerShard = iris().server()

    fun pair(): Pair<String, PacketSubscriberContext.() -> Unit> = this.name to this.handle()

}
