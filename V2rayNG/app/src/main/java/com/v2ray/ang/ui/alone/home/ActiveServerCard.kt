package com.v2ray.ang.ui.alone.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.v2ray.ang.ui.alone.icons.Icons as AloneIconSet
import com.v2ray.ang.ui.theme.AloneShape
import com.v2ray.ang.ui.theme.AloneTheme

/** spec.components ActiveServerCard: props server: Server, onClick. */
@Composable
fun ActiveServerCard(
    serverName: String,
    protocol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AloneTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .clip(AloneShape.large)
            .background(colors.glassFill)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(AloneIconSet.Alone.Globe, contentDescription = null, tint = colors.primary, modifier = Modifier.size(20.dp))
        Column(Modifier.weight(1f)) {
            Text(
                stringResource(com.v2ray.ang.R.string.alone_active_server),
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant,
            )
            Text(serverName, style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
        }
        Text(protocol, style = MaterialTheme.typography.labelMedium, color = colors.outline)
        Icon(
            AloneIconSet.Alone.Chevron,
            contentDescription = null,
            tint = colors.outline,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun stringResource(id: Int) = androidx.compose.ui.res.stringResource(id)
