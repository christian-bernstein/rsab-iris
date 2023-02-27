package de.christianbernstein.rsab.iris

data class StorageInformation(
    val stored: Int,
    val capacity: Int,
    val levelChangeType: LevelChangeType,
    val estimatedFillUpTimestamp: Long
)
