package de.christianbernstein.rsab.iris.test

import de.christianbernstein.rsab.iris.Iris
import java.time.Duration
import java.time.Instant

fun main(args: Array<String>): Unit = Iris(TestIrisBridge).run {
    val unix = Instant.now()
    this.generateUserToken("zZChrisZz")
    println("user token generation took ${Duration.between(unix, Instant.now()).toMillis()}ms")
}
