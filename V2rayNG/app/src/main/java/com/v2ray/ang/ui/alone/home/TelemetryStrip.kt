package com.v2ray.ang.ui.alone.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.v2ray.ang.ui.theme.AloneShape
import com.v2ray.ang.ui.theme.AloneTheme
import com.v2ray.ang.ui.theme.aloneMicroTextStyle

/** spec.components TelemetryStrip: props status, latencyMs, exitIp. */
@Composable
fun TelemetryStrip(
    statusLabel: String,
    statusTone: Tone,
    latencyText: String,
    latencyTone: Tone,
    exitIp: String,
    isFarsi: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = AloneTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .clip(AloneShape.large)
            .background(colors.glassFill)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TelemetryCell(stringRes(com.v2ray.ang.R.string.alone_k_status), statusLabel, statusTone.toColor(colors), Modifier.weight(1f))
        Separator()
        TelemetryCell(stringRes(com.v2ray.ang.R.string.alone_k_latency), latencyText, latencyTone.toColor(colors), Modifier.weight(1f))
        Separator()
        TelemetryCell(stringRes(com.v2ray.ang.R.string.alone_k_exit), exitIp, colors.onSurface, Modifier.weight(1f))
    }
}

enum class Tone { NEUTRAL, OK, WARN, BAD }
private fun Tone.toColor(colors: com.v2ray.ang.ui.theme.AloneColorRoles) = when (this) {
    Tone.NEUTRAL -> colors.onSurface
    Tone.OK -> colors.ok
    Tone.WARN -> colors.warn
    Tone.BAD -> colors.error
}

@Composable
private fun stringRes(id: Int) = androidx.compose.ui.res.stringResource(id)

@Composable
private fun TelemetryCell(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = aloneMicroTextStyle(isFarsi = true), color = AloneTheme.colors.onSurfaceVariant, textAlign = TextAlign.Center)
        Text(value, style = MaterialTheme.typography.titleMedium, color = valueColor, textAlign = TextAlign.Center)
    }
}

@Composable
private fun Separator() {
    androidx.compose.foundation.layout.Box(
        Modifier.width(1.dp).height(28.dp).background(AloneTheme.colors.outlineVariant),
    )
}
