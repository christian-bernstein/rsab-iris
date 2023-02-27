package de.christianbernstein.rsab.iris.endpoints

import de.christianbernstein.rsab.iris.iris
import de.christianbernstein.rsab.iris.Endpoint

class SelectNetworkEndpoint: Endpoint("select-network", {
    iris().server().requiresAuthentication(this) {
        val networkID = this.packet.getString("network-id")
        this.session.setCachedProperty("selected-network-id", networkID)
        finishWithEmptySuccess()
    }
})
