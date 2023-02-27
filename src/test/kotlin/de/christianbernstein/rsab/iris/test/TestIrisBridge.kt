package de.christianbernstein.rsab.iris.test

import de.christianbernstein.rsab.iris.IIrisBridge
import de.christianbernstein.rsab.iris.LevelChangeType
import de.christianbernstein.rsab.iris.StorageInformation
import de.christianbernstein.rsab.iris.TypeInformation
import java.time.Instant

object TestIrisBridge : IIrisBridge {
    override fun getStorageInformation(netID: String): StorageInformation = StorageInformation(
        32, 64, LevelChangeType.STABLE, Instant.now().epochSecond
    )
    override fun getTypeInformation(netID: String, typeID: String): TypeInformation = this.getAllTypeInformation(netID)
        .first { it.id == typeID }
    override fun getAllTypeInformation(netID: String): Set<TypeInformation> = setOf(
        TypeInformation(levelChangeType = LevelChangeType.STABLE, id = "minecraft:dirt", quantity = 16)
    )
}
