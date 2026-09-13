package com.v2ray.ang.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalAloneColors: ProvidableCompositionLocal<AloneColorRoles> =
    staticCompositionLocalOf { error("AloneVpnTheme not applied — no AloneColorRoles provided") }

/** True while reduced-motion or battery-saver is active — ambient/looping animations must stop, no exceptions (spec.motion.rules). */
val LocalAloneReducedMotion: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { false }

/** True when the active UI language is fa — drives script rules independent of layout direction. */
val LocalAloneIsFarsi: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { true }

/** `AloneTheme.colors.primary` reads exactly like `var(--primary)` did in the prototype. */
object AloneTheme {
    val colors: AloneColorRoles
        @Composable get() = LocalAloneColors.current
}

/**
 * Root theme wrapper. [themeId] is one of AloneThemes' six ids (falls back to
 * the first / obsidian if unrecognised — e.g. a stale saved preference).
 */
@Composable
fun AloneVpnTheme(
    themeId: String,
    isDark: Boolean,
    isFarsi: Boolean,
    reducedMotion: Boolean = false,
    content: @Composable () -> Unit,
) {
    val knobs = AloneThemes.firstOrNull { it.id == themeId } ?: AloneThemes.first()
    val roles = deriveAloneColors(knobs, isDark)

    // Bridged for stock M3 components (Switch, Slider, TextField, ripples, ...)
    // that read MaterialTheme.colorScheme directly. Roles with no exact
    // source-of-truth (surfaceVariant, onError, inversePrimary) are aliased
    // to the closest derived role rather than left to M3's tonal defaults,
    // which would fight the custom palette; everything else is passed
    // through unchanged from AloneColorRoles.
    val onError = oklch(if (isDark) 0.215f else 0.995f, 0.165f * 0.30f, 26f) // same pattern as onPrimary, error's own hue
    val colorSchemeBuilder = if (isDark) ::darkColorScheme else ::lightColorScheme
    val materialScheme = colorSchemeBuilder(
        primary = roles.primary,
        onPrimary = roles.onPrimary,
        primaryContainer = roles.primaryContainer,
        onPrimaryContainer = roles.onPrimaryContainer,
        secondary = roles.secondary,
        onSecondary = roles.onSecondary,
        secondaryContainer = roles.secondaryContainer,
        onSecondaryContainer = roles.onSecondaryContainer,
        tertiary = roles.tertiary,
        onTertiary = roles.onTertiary,
        tertiaryContainer = roles.tertiaryContainer,
        onTertiaryContainer = roles.onTertiaryContainer,
        background = roles.surface,
        onBackground = roles.onSurface,
        surface = roles.surface,
        onSurface = roles.onSurface,
        surfaceVariant = roles.surfaceContainerHighest, // alias — spec has no separate surfaceVariant role
        surfaceTint = roles.primary,
        inverseSurface = roles.inverseSurface,
        inverseOnSurface = roles.inverseOnSurface,
        inversePrimary = roles.primaryContainer, // alias — spec has no separate inversePrimary role
        error = roles.error,
        onError = onError,
        errorContainer = roles.errorContainer,
        onErrorContainer = roles.onErrorContainer,
        outline = roles.outline,
        outlineVariant = roles.outlineVariant,
        scrim = roles.scrim,
        surfaceBright = roles.surfaceBright,
        surfaceDim = roles.surfaceDim,
        surfaceContainer = roles.surfaceContainer,
        surfaceContainerHigh = roles.surfaceContainerHigh,
        surfaceContainerHighest = roles.surfaceContainerHighest,
        surfaceContainerLow = roles.surfaceContainerLow,
        surfaceContainerLowest = roles.surfaceContainerLowest,
    )

    CompositionLocalProvider(
        LocalAloneColors provides roles,
        LocalAloneReducedMotion provides reducedMotion,
        LocalAloneIsFarsi provides isFarsi,
    ) {
        MaterialTheme(
            colorScheme = materialScheme,
            typography = aloneTypography(isFarsi),
            shapes = AloneShapes,
            content = content,
        )
    }
}
