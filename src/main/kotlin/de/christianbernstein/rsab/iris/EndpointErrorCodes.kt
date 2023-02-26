package de.christianbernstein.rsab.iris.server

enum class EndpointErrorCodes(val code: Int) {
    SUCCESS(0),
    UNAUTHORIZED(401),
    INTERNAL_SERVER_ERROR(500),
    NO_NETWORK_SELECTED(901)
}
