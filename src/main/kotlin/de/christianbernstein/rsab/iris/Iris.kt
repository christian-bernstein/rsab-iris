package de.christianbernstein.rsab.iris

import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * RS-AB Core system's core class
 */
class Iris(private val bridge: IIrisBridge): ShardedCore() {

    companion object {
        var instance: Iris? = null
    }

    fun web(): WebEngineShard = shard("web")

    fun packetier(): PacketEngineShard = shard("packet")

    fun server(): ServerShard = shard("server")

    fun bridge(): IIrisBridge = bridge

    private fun init() {
        this.registerShard("web", WebEngineShard(this))
        this.registerShard("packet", PacketEngineShard(this))
        this.registerShard("server", ServerShard(this))
    }

    /**
     * TODO: Store salt in file / db
     */
    fun generateUserToken(username: String): String = SecretKeyFactory
        .getInstance("PBKDF2WithHmacSHA1")
        .generateSecret(PBEKeySpec(username.toCharArray(), "salty salted salt".toByteArray(), 65536, 24))
        .encoded
        .let { Base64.getUrlEncoder().encodeToString(it) }

    init {
        instance = this
        this.init()
    }
}

fun iris(): Iris = Iris.instance ?: error("Iris instance is null")
