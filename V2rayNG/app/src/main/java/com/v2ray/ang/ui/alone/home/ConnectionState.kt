package com.v2ray.ang.ui.alone.home

import java.time.Instant

/** One handshake step; order matches spec.stateMachines.connection.handshakeSteps exactly. */
enum class HandshakeStep(val labelResKey: String, val real: String) {
    RESOLVE("alone_hs_resolve", "DNS resolution of the server host"),
    TCP("alone_hs_tcp", "TCP/QUIC connect to host:port"),
    TLS("alone_hs_tls", "TLS / REALITY / uTLS handshake"),
    TUNNEL("alone_hs_tunnel", "tun interface up, routes installed, first packet through"),
}

/** Minimal server shape — enough for Home; the full model lands with the Servers screen. */
data class AloneServer(
    val id: String,
    val city: String,
    val country: String,
    val ip: String,
    val protocol: String,
)

sealed interface FailureReason {
    data object Timeout : FailureReason
    data object AuthRejected : FailureReason
    data object NoInternet : FailureReason
    data class Other(val message: String) : FailureReason
}

/**
 * spec.stateMachines.connection, ported 1:1:
 * sealed interface ConnectionState { Off; Connecting(step); On(since, server); Failed(reason); Disconnecting }
 *
 * Transitions (enforced by ConnectionController, not by this type):
 *  off --tapDial--> connecting --allStepsOk--> on
 *  connecting --stepFailed--> failed          connecting --tapDial--> off (cancel aborts the handshake)
 *  on --tapDial--> disconnecting --done--> off
 *  on --tunnelDropped--> failed (kill switch should block traffic here)
 *  on --serverChanged--> connecting (tear down the old tunnel first)
 *  failed --tapDial--> connecting
 */
sealed interface ConnectionState {
    data object Off : ConnectionState
    data class Connecting(val step: HandshakeStep) : ConnectionState
    data class On(val since: Instant, val server: AloneServer) : ConnectionState
    data class Failed(val reason: FailureReason) : ConnectionState
    data object Disconnecting : ConnectionState
}

/** spec: "Naming the failing step is the whole point. 'Connecting…' with a spinner tells the user nothing they can act on." */
val HandshakeStep.ordinalStep: Int get() = ordinal
