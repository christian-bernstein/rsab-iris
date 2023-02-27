package de.christianbernstein.rsab.iris.endpoints

import de.christianbernstein.rsab.iris.iris
import de.christianbernstein.rsab.iris.Endpoint

class GetSelectedNetworkInformationEndpoint: Endpoint("get-selected-network-information", {
    iris().server().requiresAuthAndNet(this) { _, _ ->

    }
})
