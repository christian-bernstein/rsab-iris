package de.christianbernstein.rsab.iris.endpoints

import de.christianbernstein.rsab.iris.iris
import de.christianbernstein.rsab.iris.Endpoint

class AuthenticateEndpoint: Endpoint("authenticate", {
    val playerName = packet.getString("player-name")
    val token = packet.getString("token")
    val generatedToken = iris().generateUserToken(playerName)
    if (token == generatedToken) {
        // User has submitted the correct token, login can be granted
        session.setCachedProperty("player-name", playerName)
        session.setCachedProperty("token", token)
        session.setCachedProperty("authenticated", true)

        finishWithEmptySuccess()
    } else {
        // User hasn't submitted the correct token

        respond(mapOf(
            "success" to false
        ))
    }
})
