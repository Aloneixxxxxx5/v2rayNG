package com.v2ray.ang.ui.alone.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.v2ray.ang.ui.alone.icons.Icons as AloneIconSet
import com.v2ray.ang.ui.theme.AloneShape
import com.v2ray.ang.ui.theme.AloneTheme

/** spec screen "language": two `.glass.btn.tall.wide` rows, data-action lang:fa / lang:en. */
@Composable
fun LanguageScreen(
    onSelect: (langCode: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AloneTheme.colors
    Column(
        modifier.fillMaxSize().background(colors.surface).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Choose your language", style = MaterialTheme.typography.headlineMedium, color = colors.onSurface, textAlign = TextAlign.Center)
        Text(
            "زبان خود را انتخاب کنید",
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
        )
        LanguageRow("فارسی", "FA", onClick = { onSelect("fa") })
        androidx.compose.foundation.layout.Spacer(Modifier.height(10.dp))
        LanguageRow("English", "EN", onClick = { onSelect("en") })
    }
}

@Composable
private fun LanguageRow(label: String, code: String, onClick: () -> Unit) {
    val colors = AloneTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(AloneShape.large)
            .background(colors.glassFill)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        androidx.compose.foundation.layout.Box(
            Modifier.size(32.dp).clip(CircleShape).background(colors.surfaceContainerHigh),
            contentAlignment = Alignment.Center,
        ) {
            Icon(AloneIconSet.Alone.Lang, contentDescription = null, tint = colors.onSurface, modifier = Modifier.size(18.dp))
        }
        Text(label, style = MaterialTheme.typography.titleLarge, color = colors.onSurface, modifier = Modifier.weight(1f))
        Text(code, style = MaterialTheme.typography.labelMedium, color = colors.outline)
    }
}
