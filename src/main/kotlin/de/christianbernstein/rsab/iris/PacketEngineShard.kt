package de.christianbernstein.rsab.iris

import de.christianbernstein.rsab.iris.packetier.PacketEngine

class PacketEngineShard(core: Iris) : Shard<PacketEngineShard>(core) {
    var engine: PacketEngine = PacketEngine(core.web().generatePacketierBridge())
}
