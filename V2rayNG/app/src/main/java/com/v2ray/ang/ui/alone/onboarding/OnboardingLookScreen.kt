package com.v2ray.ang.ui.alone.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.v2ray.ang.ui.alone.icons.Icons as AloneIconSet
import com.v2ray.ang.ui.theme.AloneShape
import com.v2ray.ang.ui.theme.AloneThemes
import com.v2ray.ang.ui.theme.AloneTheme
import com.v2ray.ang.ui.theme.oklch

enum class AppearanceMode { LIGHT, DARK, SYSTEM }

/** spec screen onb-look: back+dots, headline/body, mode segmented, theme grid, CTA. */
@Composable
fun OnboardingLookScreen(
    selectedThemeId: String,
    mode: AppearanceMode,
    onBack: () -> Unit,
    onModeChange: (AppearanceMode) -> Unit,
    onThemeChange: (String) -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AloneTheme.colors
    Column(modifier.fillMaxSize().background(colors.surface).padding(20.dp)) {
        Row(Modifier.fillMaxWidth().height(40.dp), verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.foundation.layout.Box(
                Modifier.size(40.dp).clip(CircleShape).clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(AloneIconSet.Alone.Chevron, contentDescription = "back", tint = colors.onSurface, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.weight(1f))
            Dots(OnboardingStep.LOOK)
        }

        Text(
            "ظاهرش را انتخاب کن",
            style = MaterialTheme.typography.headlineMedium,
            color = colors.onSurface,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            "هر تم فقط پس‌زمینه را عوض نمی‌کند؛ رنگ تأکید، سایه‌ها و نور محیط هم با آن جابه‌جا می‌شوند.",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 16.dp),
        )

        AppearanceModeSegmented(mode = mode, onChange = onModeChange)

        Spacer(Modifier.height(16.dp))

        ThemeGrid(
            selectedThemeId = selectedThemeId,
            isDark = mode != AppearanceMode.LIGHT, // system approximated as dark for the preview; real value comes from the platform at runtime
            onSelect = onThemeChange,
            modifier = Modifier.weight(1f),
        )

        GlassCta(icon = AloneIconSet.Alone.Check, label = "شروع کنیم", onClick = onFinish)
    }
}

@Composable
private fun AppearanceModeSegmented(mode: AppearanceMode, onChange: (AppearanceMode) -> Unit) {
    val colors = AloneTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .clip(AloneShape.full)
            .background(colors.surfaceContainerLowest)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        SegmentedButton(AloneIconSet.Alone.Sun, "روشن", mode == AppearanceMode.LIGHT, Modifier.weight(1f)) { onChange(AppearanceMode.LIGHT) }
        SegmentedButton(AloneIconSet.Alone.Moon, "تیره", mode == AppearanceMode.DARK, Modifier.weight(1f)) { onChange(AppearanceMode.DARK) }
        SegmentedButton(AloneIconSet.Alone.Monitor, "سیستم", mode == AppearanceMode.SYSTEM, Modifier.weight(1f)) { onChange(AppearanceMode.SYSTEM) }
    }
}

@Composable
private fun SegmentedButton(icon: ImageVector, label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val colors = AloneTheme.colors
    Row(
        modifier
            .height(34.dp)
            .clip(AloneShape.full)
            .background(if (selected) colors.primary else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (selected) colors.onPrimary else colors.onSurfaceVariant,
            modifier = Modifier.size(14.dp),
        )
        Spacer(Modifier.width(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) colors.onPrimary else colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun ThemeGrid(selectedThemeId: String, isDark: Boolean, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
        modifier = modifier,
    ) {
        items(AloneThemes, key = { it.id }) { theme ->
            ThemeCell(themeId = theme.id, isDark = isDark, selected = theme.id == selectedThemeId, onClick = { onSelect(theme.id) })
        }
    }
}

@Composable
private fun ThemeCell(themeId: String, isDark: Boolean, selected: Boolean, onClick: () -> Unit) {
    val colors = AloneTheme.colors
    val knobs = AloneThemes.first { it.id == themeId }
    // spec's swatchCss(): a flattened preview gradient, independent of the app's own live surface
    // tint — deliberately simpler than the real derivation so every theme's card reads clearly at
    // a glance in a 3-column grid.
    val sl = if (isDark) knobs.darkSurfaceL else knobs.lightSurfaceL
    val lift = if (isDark) 1f else -1f
    val tint = if (isDark) 0.02f else 0.01f
    val colorA = oklch(sl, tint, knobs.hue)
    val colorB = oklch(sl + lift * 0.07f, tint * 1.4f, knobs.hue)
    val accent = oklch(if (isDark) 0.805f else 0.495f, knobs.chroma, knobs.hue)
    val shape = if (selected) AloneShape.large else AloneShape.medium

    Column(
        Modifier.clickable(onClick = onClick).padding(9.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        androidx.compose.foundation.layout.Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(shape)
                .background(Brush.linearGradient(listOf(colorA, colorB))),
        ) {
            androidx.compose.foundation.layout.Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 8.dp)
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(accent),
            )
        }
        Text(
            themeLabel(themeId),
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) colors.primary else colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 7.dp),
        )
    }
}

private fun themeLabel(id: String): String = when (id) {
    "obsidian" -> "ابسیدین"
    "aqua" -> "آکوا"
    "neon" -> "نئون"
    "midnight" -> "نیمه‌شب"
    "ember" -> "اِمبر"
    "matcha" -> "ماچا"
    else -> id
}
