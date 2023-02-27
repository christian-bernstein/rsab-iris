package de.christianbernstein.rsab.iris.endpoints

import de.christianbernstein.rsab.iris.Endpoint

class IsAuthenticatedEndpoint: Endpoint("is-authenticated", {
    respond(mapOf(
        "authenticated" to session.getCachedProperty("authenticated", false)!!
    ))
})
