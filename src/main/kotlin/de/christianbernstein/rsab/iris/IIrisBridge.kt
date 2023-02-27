package de.christianbernstein.rsab.iris

interface IIrisBridge {
    fun getStorageInformation(netID: String): StorageInformation
    fun getTypeInformation(netID: String, typeID: String): TypeInformation
    fun getAllTypeInformation(netID: String): Set<TypeInformation>
}
