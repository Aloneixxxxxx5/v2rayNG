package com.v2ray.ang.ui.alone.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

data class HomeUiState(
    val connection: ConnectionState = ConnectionState.Off,
    val activeServer: AloneServer = AloneServer("frankfurt-1", "Frankfurt", "Germany", "185.213.xxx.xxx", "vless"),
    /** null = no reading yet ("—" in the strip), matches spec.simulated: latencyMs is currently a fake number generator. */
    val latencyMs: Int? = null,
    val sessionSeconds: Long = 0,
)

/**
 * Owns spec.stateMachines.connection exactly. `beginHandshakeSimulation()` is
 * the ONE thing in this file that's fake — spec.simulated flags latency and
 * throughput as sim-only; the handshake sequencing here is sim-only too,
 * until this is wired to the real VpnService (see ANDROID_HANDOFF.md's plan:
 * VpnService.Builder().establish() -> hev-socks5-tunnel -> Xray-core SOCKS5
 * inbound). Swap `beginHandshakeSimulation()` for a real call there; nothing
 * in HomeScreen.kt needs to change since it only ever sees ConnectionState.
 */
class HomeViewModel : ViewModel() {
    private val _ui = MutableStateFlow(HomeUiState())
    val ui: StateFlow<HomeUiState> = _ui.asStateFlow()

    private var handshakeJob: Job? = null
    private var sessionJob: Job? = null

    fun onDialTapped() {
        when (_ui.value.connection) {
            is ConnectionState.Off, is ConnectionState.Failed -> connect()
            is ConnectionState.Connecting -> cancelConnect()
            is ConnectionState.On -> disconnect()
            is ConnectionState.Disconnecting -> Unit // no-op, matches spec transitions (no edge defined)
        }
    }

    private fun connect() {
        handshakeJob?.cancel()
        handshakeJob = viewModelScope.launch {
            _ui.value = _ui.value.copy(connection = ConnectionState.Connecting(HandshakeStep.RESOLVE))
            beginHandshakeSimulation()
        }
    }

    /** SIMULATED — see class doc. Steps through resolve -> tcp -> tls -> tunnel, ~450ms apart. */
    private suspend fun beginHandshakeSimulation() {
        for (step in HandshakeStep.entries) {
            _ui.value = _ui.value.copy(connection = ConnectionState.Connecting(step))
            delay(450)
        }
        val since = Instant.now()
        _ui.value = _ui.value.copy(
            connection = ConnectionState.On(since, _ui.value.activeServer),
            latencyMs = (28..64).random(), // spec.simulated: fake until real probe wiring
        )
        startSessionTimer(since)
    }

    private fun cancelConnect() {
        handshakeJob?.cancel()
        _ui.value = _ui.value.copy(connection = ConnectionState.Off)
    }

    private fun disconnect() {
        sessionJob?.cancel()
        viewModelScope.launch {
            _ui.value = _ui.value.copy(connection = ConnectionState.Disconnecting)
            delay(280)
            _ui.value = _ui.value.copy(connection = ConnectionState.Off, sessionSeconds = 0)
        }
    }

    private fun startSessionTimer(since: Instant) {
        sessionJob?.cancel()
        sessionJob = viewModelScope.launch {
            while (true) {
                _ui.value = _ui.value.copy(sessionSeconds = java.time.Duration.between(since, Instant.now()).seconds)
                delay(1000)
            }
        }
    }
}

/** off:0, connecting:(step+1)/4, on:1 — spec.stateMachines' exact sweep formula. */
fun ConnectionState.sweepFraction(): Float = when (this) {
    is ConnectionState.Off, is ConnectionState.Disconnecting -> 0f
    is ConnectionState.Connecting -> (step.ordinal + 1) / 4f
    is ConnectionState.On -> 1f
    is ConnectionState.Failed -> 0f
}

fun formatSession(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%d:%02d".format(m, s)
}
