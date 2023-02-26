package de.christianbernstein.rsab.iris

import de.christianbernstein.rsab.iris.packet.PacketEngineShard
import de.christianbernstein.rsab.iris.server.ServerShard
import de.christianbernstein.rsab.iris.web.WebEngineShard

/**
 * RS-AB Core system's core class
 */
abstract class AbstractIris(private val bridge: IIrisBridge): ShardedCore() {
    fun web(): WebEngineShard = shard("web")
    fun packetier(): PacketEngineShard = shard("packet")
    fun server(): ServerShard = shard("server")
    fun bridge(): IIrisBridge = bridge
    abstract fun init()
}
