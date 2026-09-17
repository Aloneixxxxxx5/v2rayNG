package com.v2ray.ang.ui.alone.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.v2ray.ang.ui.alone.icons.Icons as AloneIconSet
import com.v2ray.ang.ui.theme.AloneTheme
import com.v2ray.ang.ui.main.MainAction
import com.v2ray.ang.ui.main.MainStatus
import com.v2ray.ang.ui.main.MainViewModel

/**
 * The REAL bridge: takes v2rayNG's actual MainViewModel (real VpnService toggle, real
 * connection status, real server groups) and renders it through the ALONE VPN visual design.
 * Nothing here is simulated — onDialTap fires the real MainAction.ToggleService.
 *
 * onNavigate/onOpenDestination intentionally reuse v2rayNG's existing, already-working
 * Activities (Logcat, Settings, PerAppProxy, ...) for anything the ALONE redesign hasn't
 * replaced yet, rather than dead-ending on features that already work.
 */
@Composable
fun AloneMainScreen(
    mainViewModel: MainViewModel,
    onAction: (MainAction) -> Unit,
    onOpenLog: () -> Unit,
    onOpenHelp: () -> Unit,
    onOpenServers: () -> Unit,
    onOpenDns: () -> Unit,
) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val colors = AloneTheme.colors

    val visual = realStatusToVisual(uiState.isRunning, uiState.status)
    val sweep = if (visual == DialVisualState.ON) 1f else if (visual == DialVisualState.CONNECTING) 0.6f else 0f

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(colors.surface),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                GlassIconBtn(AloneIconSet.Alone.Terminal, onOpenLog)
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ALONE VPN", style = MaterialTheme.typography.headlineSmall, color = colors.onSurface)
                    Text(
                        "خصوصی. رمزنگاری‌شده. سریع.",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                    )
                }
                GlassIconBtn(AloneIconSet.Alone.Help, onOpenHelp)
            }
        }

        item {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                ConnectDial(
                    state = visual,
                    sweepFraction = sweep,
                    onTap = { onAction(MainAction.ToggleService) },
                )
                val (headline, headlineColor) = when (visual) {
                    DialVisualState.ON -> "ترافیکت محافظت می‌شود" to colors.ok
                    DialVisualState.CONNECTING -> "در حال اتصال…" to colors.onSurface
                    DialVisualState.FAILED -> "اتصال برقرار نشد" to colors.error
                    else -> "وصل نیستی" to colors.onSurface
                }
                Text(
                    headline,
                    style = MaterialTheme.typography.titleLarge,
                    color = headlineColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(
                    mainViewModel.formatStatus(uiState.status),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        item {
            ActiveServerCard(
                serverName = uiState.groups.firstOrNull { it.id == uiState.selectedGroupId }?.remarks
                    ?: "سروری انتخاب نشده",
                protocol = "",
                onClick = onOpenServers,
            )
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickActionTile(
                    icon = AloneIconSet.Alone.Pulse,
                    label = "تست تأخیر",
                    onClick = { onAction(MainAction.TestCurrentServer) },
                    modifier = Modifier.weight(1f),
                )
                QuickActionTile(
                    icon = AloneIconSet.Alone.Pad,
                    label = "دور زدن اپ‌ها",
                    onClick = onOpenDns,
                    modifier = Modifier.weight(1f),
                )
                QuickActionTile(
                    icon = AloneIconSet.Alone.Cast,
                    label = "افزودن کانفیگ",
                    onClick = { onAction(MainAction.ImportQRcode) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

private fun realStatusToVisual(isRunning: Boolean, status: MainStatus): DialVisualState = when {
    status is MainStatus.Testing || status is MainStatus.TestProgress -> DialVisualState.CONNECTING
    isRunning && status is MainStatus.Connected -> DialVisualState.ON
    isRunning -> DialVisualState.CONNECTING
    else -> DialVisualState.OFF
}

@Composable
private fun GlassIconBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
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
        Icon(icon, contentDescription = null, tint = colors.onSurface, modifier = Modifier.size(18.dp))
    }
}
