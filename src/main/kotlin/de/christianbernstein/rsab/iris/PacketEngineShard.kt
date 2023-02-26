package de.christianbernstein.rsab.iris.packet

import de.christianbernstein.rsab.iris.Iris
import de.christianbernstein.rsab.iris.shard.Shard
import de.christianbernstein.rsab.iris.packetier.PacketEngine

class PacketEngineShard(core: Iris) : Shard<PacketEngineShard>(core) {
    var engine: PacketEngine = PacketEngine(core.web().generatePacketierBridge())
}
