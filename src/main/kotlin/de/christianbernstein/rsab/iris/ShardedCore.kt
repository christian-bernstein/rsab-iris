package de.christianbernstein.rsab.iris.shard

@Suppress("UNCHECKED_CAST")
open class ShardedCore {

    private var shards: Map<String, Shard<out Any>> = hashMapOf()

    fun <T : Shard<T>> shard(id: String): T = requireNotNull(this.shards[id]) { "Cannot resolve a shard with id '$id'" }
        .run { this as T }

    fun <T : Shard<T>, V> shard(id: String, block: T.() -> V): V = shard<T>(id)
        .run(block)

    fun <T : Shard<T>> registerShard(id: String, shard: T): T = shard
        .also { shards += id to it }
        .also { it.init() }
}
