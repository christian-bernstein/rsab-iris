package de.christianbernstein.rsab.iris.endpoints

import de.christianbernstein.rsab.iris.iris
import de.christianbernstein.rsab.iris.Endpoint

class GetAvailableNetworksEndpoint: Endpoint("get-available-networks", {
    iris().server().requiresAuthentication(this) {

    }
})
