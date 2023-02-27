package de.christianbernstein.rsab.iris

import de.christianbernstein.rsab.iris.endpoints.*
import de.christianbernstein.rsab.iris.packetier.PacketSubscriberContext
import de.christianbernstein.rsab.iris.packetier.createProtocol

@Suppress("MemberVisibilityCanBePrivate")
class ServerShard(core: Iris) : Shard<ServerShard>(core, {
    core.packetier().engine.createSession(
        MAIN_SERVER_ID, createProtocol(
        "ping" to { respond(emptyMap()) },
        "test" to { println("test") },
        "set-num" to { session.setCachedProperty("num", packet.getInt("num")) },
        "get-num" to { respond(mapOf("num" to session.getCachedProperty("num")!!)) },

        AuthenticateEndpoint().pair(),
        IsAuthenticatedEndpoint().pair(),
        SelectNetworkEndpoint().pair(),
        GetAvailableNetworksEndpoint().pair(),
        GetSelectedNetworkInformationEndpoint().pair()
    ))
}) {
    companion object {
        const val MAIN_SERVER_ID: String = "main-server"
    }

    override fun shard(): ServerShard = this

    /**
     * The internal action requires the session to be authenticated.
     * If the session isn't authenticated, an error response will be sent to the client.
     */
    fun requiresAuthentication(ctx: PacketSubscriberContext, ifAuthenticated: (userLayer: UserLayerContext) -> Unit) {
        if (ctx.session.getCachedProperty("authenticated", false)!!) {
            ifAuthenticated(UserLayerContext(ctx.session.getCachedProperty("player-name")!!))
        } else {
            ctx.respond(ctx.generateEndpointResponse(
                success = false,
                code = EndpointErrorCodes.UNAUTHORIZED.code,
                additionalMessage = "This action requires authentication"
            ))
        }
    }

    /**
     * The internal action requires a network to be selected.
     * If the session isn't associated with a network, an error response will be sent to the client.
     */
    fun requiresSelectedNetwork(ctx: PacketSubscriberContext, ifNetworkSelected: (netLayer: NetworkLayerContext) -> Unit) {
        ctx.session.getCachedProperty<String>("selected-network-id").also { id ->
            if (id != null) {
                ifNetworkSelected(NetworkLayerContext(id))
            } else {
                ctx.respond(ctx.generateEndpointResponse(
                    success = false,
                    code = EndpointErrorCodes.NO_NETWORK_SELECTED.code,
                    additionalMessage = "This action requires a network to be selected"
                ))
            }
        }
    }

    /**
     * The internal action requires to be authenticated and a network needs to be selected.
     * If any of the above requirements isn't satisfied, the underlying error handling routines are applied.
     *
     * @see requiresAuthentication First requirement to be checked
     * @see requiresSelectedNetwork Second requirement to be checked
     */
    fun requiresAuthAndNet(ctx: PacketSubscriberContext, ifMeetsDemands: (userLayer: UserLayerContext, netLayer: NetworkLayerContext) -> Unit) {
        this.requiresAuthentication(ctx) { userLayer ->
            this.requiresSelectedNetwork(ctx) { netLayer ->
                ifMeetsDemands(userLayer, netLayer)
            }
        }
    }
}

