package com.v2ray.ang.ui.alone.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.v2ray.ang.R
import com.v2ray.ang.ui.alone.icons.Icons as AloneIconSet
import com.v2ray.ang.ui.theme.AloneTheme

@Composable
fun HomeScreen(
    onOpenLog: () -> Unit = {},
    onOpenHelp: () -> Unit = {},
    onOpenServers: () -> Unit = {},
    onOpenLatency: () -> Unit = {},
    onOpenDns: () -> Unit = {},
    onOpenImport: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(),
) {
    val ui by viewModel.ui.collectAsState()
    val colors = AloneTheme.colors
    val visual = ui.connection.toVisual()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(colors.surface),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HomeAppBar(onOpenLog = onOpenLog, onOpenHelp = onOpenHelp)
        }

        item {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ConnectDial(
                    state = visual,
                    sweepFraction = ui.connection.sweepFraction(),
                    onTap = viewModel::onDialTapped,
                )
                DialLabel(ui)
                if (ui.connection is ConnectionState.Connecting) {
                    HandshakeProgress(
                        current = (ui.connection as ConnectionState.Connecting).step.ordinal,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                    )
                }
            }
        }

        item {
            TelemetryStrip(
                statusLabel = statusValueText(visual),
                statusTone = when (visual) {
                    DialVisualState.ON -> Tone.OK
                    DialVisualState.FAILED -> Tone.BAD
                    else -> Tone.NEUTRAL
                },
                latencyText = ui.latencyMs?.let { "${it}ms" } ?: "—",
                latencyTone = when (ui.latencyMs) {
                    null -> Tone.NEUTRAL
                    in 0..80 -> Tone.OK
                    in 81..180 -> Tone.WARN
                    else -> Tone.BAD
                },
                exitIp = if (visual == DialVisualState.ON) ui.activeServer.ip else "—",
                isFarsi = true,
            )
        }

        item {
            ActiveServerCard(
                serverName = "${ui.activeServer.city} · ${ui.activeServer.country}",
                protocol = ui.activeServer.protocol,
                onClick = onOpenServers,
            )
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickActionTile(
                    icon = AloneIconSet.Alone.Pulse,
                    label = stringResource(R.string.alone_t_latency),
                    onClick = onOpenLatency,
                    modifier = Modifier.weight(1f),
                )
                QuickActionTile(
                    icon = AloneIconSet.Alone.Pad,
                    label = stringResource(R.string.alone_t_dns),
                    onClick = onOpenDns,
                    modifier = Modifier.weight(1f),
                )
                QuickActionTile(
                    icon = AloneIconSet.Alone.Cast,
                    label = stringResource(R.string.alone_t_import),
                    onClick = onOpenImport,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun HomeAppBar(onOpenLog: () -> Unit, onOpenHelp: () -> Unit) {
    val colors = AloneTheme.colors
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        GlassIconButton(AloneIconSet.Alone.Terminal, stringResource(R.string.alone_al_log), onOpenLog)
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("ALONE VPN", style = MaterialTheme.typography.headlineSmall, color = colors.onSurface)
            Text(
                stringResource(R.string.alone_tagline),
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant,
            )
        }
        GlassIconButton(AloneIconSet.Alone.Help, stringResource(R.string.alone_al_help), onOpenHelp)
    }
}

@Composable
private fun GlassIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String, onClick: () -> Unit) {
    val colors = AloneTheme.colors
    Row(
        Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(colors.glassFill)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = contentDescription, tint = colors.onSurface, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun DialLabel(ui: HomeUiState) {
    val colors = AloneTheme.colors
    val visual = ui.connection.toVisual()
    val headline = statusHeadlineText(visual)
    val headlineColor = when (visual) {
        DialVisualState.ON -> colors.ok
        DialVisualState.FAILED -> colors.error
        else -> colors.onSurface
    }
    val support = when (val c = ui.connection) {
        is ConnectionState.Off -> stringResource(R.string.alone_sub_tap)
        is ConnectionState.Failed -> stringResource(R.string.alone_sub_retry)
        is ConnectionState.Connecting -> stringResource(R.string.alone_sub_cancel)
        is ConnectionState.On -> stringResource(R.string.alone_sub_via, c.server.city) + " · " + formatSession(ui.sessionSeconds)
        is ConnectionState.Disconnecting -> ""
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(headline, style = MaterialTheme.typography.titleLarge, color = headlineColor, textAlign = TextAlign.Center)
        Text(support, style = MaterialTheme.typography.labelLarge, color = colors.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
private fun statusHeadlineText(v: DialVisualState): String = stringResource(
    when (v) {
        DialVisualState.OFF -> R.string.alone_st_off
        DialVisualState.CONNECTING -> R.string.alone_st_connecting
        DialVisualState.ON -> R.string.alone_st_on
        DialVisualState.FAILED -> R.string.alone_st_failed
        DialVisualState.DISCONNECTING -> R.string.alone_st_disconnecting
    },
)

@Composable
private fun statusValueText(v: DialVisualState): String = stringResource(
    when (v) {
        DialVisualState.OFF -> R.string.alone_v_off
        DialVisualState.CONNECTING -> R.string.alone_v_conn
        DialVisualState.ON -> R.string.alone_v_on
        DialVisualState.FAILED -> R.string.alone_v_fail
        DialVisualState.DISCONNECTING -> R.string.alone_v_dis
    },
)
