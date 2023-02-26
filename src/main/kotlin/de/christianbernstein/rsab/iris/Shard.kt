package de.christianbernstein.rsab.iris.shard

import de.christianbernstein.rsab.iris.Iris

abstract class Shard<Impl>(
    val core: Iris,
    controller: Shard<Impl>.() -> Unit = {}
) {
    // private var initLatch: MultiLatch = MultiLatch("main") {}

    open fun shard(): Impl = throw NotImplementedError()

    open fun init(): Unit = Unit

    open fun shutdown(): Unit = Unit
    init { this.controller() }
}
