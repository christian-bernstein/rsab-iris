package de.christianbernstein.rsab.iris.web

data class Packet(
    val type: String,
    val params: Map<String, String>,
)
