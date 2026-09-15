package com.v2ray.ang.ui.alone.home

import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.v2ray.ang.ui.theme.AloneEasing
import com.v2ray.ang.ui.theme.AloneTheme

/** spec.components HandshakeProgress: props steps: List<HandshakeStep>, current: Int. */
@Composable
fun HandshakeProgress(
    current: Int, // -1 = hidden/idle, 0..3 = which step is active/done
    modifier: Modifier = Modifier,
) {
    if (current < 0) return
    val colors = AloneTheme.colors
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        HandshakeStep.entries.forEachIndexed { i, _ ->
            val target = when {
                i < current -> colors.primary
                i == current -> colors.primary
                else -> colors.onSurface.copy(alpha = 0.12f)
            }
            val color by animateColorAsState(target, tween(220, easing = AloneEasing.Standard), label = "hsSeg")
            androidx.compose.foundation.layout.Box(
                Modifier.weight(1f).height(3.dp).clip(RoundedCornerShape(999.dp)).background(color),
            )
        }
    }
}

/** The small caption under the segments, e.g. "TLS handshake". */
@Composable
fun HandshakeStepLabel(step: HandshakeStep?, modifier: Modifier = Modifier) {
    if (step == null) return
    Text(
        text = handshakeStepText(step),
        style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
        color = AloneTheme.colors.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

@Composable
private fun handshakeStepText(step: HandshakeStep): String {
    val resId = when (step) {
        HandshakeStep.RESOLVE -> com.v2ray.ang.R.string.alone_hs_resolve
        HandshakeStep.TCP -> com.v2ray.ang.R.string.alone_hs_tcp
        HandshakeStep.TLS -> com.v2ray.ang.R.string.alone_hs_tls
        HandshakeStep.TUNNEL -> com.v2ray.ang.R.string.alone_hs_tunnel
    }
    return androidx.compose.ui.res.stringResource(resId)
}
