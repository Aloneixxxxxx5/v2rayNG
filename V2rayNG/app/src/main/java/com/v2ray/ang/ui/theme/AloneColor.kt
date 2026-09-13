package com.v2ray.ang.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Every color role the UI reads, mirroring the CSS custom properties
 * (`--primary`, `--surface-container-high`, ...) 1:1 by name so translating
 * a screen from the HTML prototype is a mechanical lookup, not a guess.
 *
 * `onSecondary` and `onTertiary` are the two roles the prototype's CSS never
 * actually defines (it only ever paints text on the *Container* variants,
 * never directly on flat secondary/tertiary) — Compose's ColorScheme still
 * wants a value for them, so they're extrapolated using the exact same
 * pattern as `onPrimary` (onacc-l lightness, c*.30 chroma, role's own hue).
 * Flagging that clearly since it's the one place this file adds something
 * the source didn't specify.
 */
data class AloneColorRoles(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color, // extrapolated — see class doc
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color, // extrapolated — see class doc
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,

    val surface: Color,
    val surfaceDim: Color,
    val surfaceBright: Color,
    val surfaceContainerLowest: Color,
    val surfaceContainerLow: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,

    val outline: Color,
    val outlineVariant: Color,

    val ok: Color,
    val okContainer: Color,
    val onOkContainer: Color,
    val warn: Color,
    val warnContainer: Color,
    val onWarnContainer: Color,
    val error: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,

    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val scrim: Color,

    // prototype-only "recipes" (glass panels, press/hover states, shadows) —
    // computed from the roles above, not independent knobs
    val glassFill: Color,
    val glassEdge: Color,
    val glassHi: Color,
    val stateHover: Color,
    val statePress: Color,
    val stateSelect: Color,
    val shadowKey: Color,
    val shadowAmbient: Color,
)

/** The eight knobs a theme supplies; everything else is derived. */
data class AloneThemeKnobs(
    val id: String,
    val hue: Float,
    val chroma: Float,
    val darkSurfaceL: Float,
    val darkTint: Float,
    val lightSurfaceL: Float,
    val lightTint: Float,
)

/** The 6 shipped themes, values copied verbatim from `[data-theme="…"]` in the CSS. */
val AloneThemes: List<AloneThemeKnobs> = listOf(
    AloneThemeKnobs("obsidian", hue = 265f, chroma = .135f, darkSurfaceL = .155f, darkTint = .014f, lightSurfaceL = .977f, lightTint = .008f),
    AloneThemeKnobs("aqua",     hue = 205f, chroma = .125f, darkSurfaceL = .205f, darkTint = .026f, lightSurfaceL = .980f, lightTint = .012f),
    AloneThemeKnobs("neon",     hue = 322f, chroma = .185f, darkSurfaceL = .125f, darkTint = .028f, lightSurfaceL = .984f, lightTint = .014f),
    AloneThemeKnobs("midnight", hue = 250f, chroma = .048f, darkSurfaceL = .135f, darkTint = .005f, lightSurfaceL = .975f, lightTint = .004f),
    AloneThemeKnobs("ember",    hue = 48f,  chroma = .145f, darkSurfaceL = .162f, darkTint = .028f, lightSurfaceL = .982f, lightTint = .014f),
    AloneThemeKnobs("matcha",   hue = 152f, chroma = .118f, darkSurfaceL = .168f, darkTint = .022f, lightSurfaceL = .979f, lightTint = .011f),
)

/** The lightness anchors that are fixed per dark/light mode, shared by all 6 themes. */
private data class ModeConstants(
    val onL: Float, val onvL: Float, val olL: Float, val olvL: Float,
    val accL: Float, val onAccL: Float, val accCL: Float, val onAccCL: Float,
    val stL: Float, val stCL: Float, val onStCL: Float,
    val glassA: Float, val hiA: Float,
    val shadowKeyAlpha: Float, val shadowAmbientAlpha: Float,
    val lift: Float,
)

private val DARK = ModeConstants(
    onL = .955f, onvL = .775f, olL = .605f, olvL = .335f,
    accL = .805f, onAccL = .215f, accCL = .365f, onAccCL = .925f,
    stL = .79f, stCL = .325f, onStCL = .94f,
    glassA = .74f, hiA = .14f,
    shadowKeyAlpha = .58f, shadowAmbientAlpha = .34f,
    lift = 1f,
)

private val LIGHT = ModeConstants(
    onL = .205f, onvL = .445f, olL = .575f, olvL = .845f,
    accL = .495f, onAccL = .995f, accCL = .905f, onAccCL = .245f,
    stL = .505f, stCL = .915f, onStCL = .235f,
    glassA = .82f, hiA = .88f,
    shadowKeyAlpha = .18f, shadowAmbientAlpha = .10f,
    lift = -1f,
)

/**
 * The full role derivation. Every `oklch(...)` call below is a direct,
 * line-for-line port of the corresponding `--role: oklch(...)` rule in the
 * prototype's `:root` block — same order, same constants, same math.
 */
fun deriveAloneColors(theme: AloneThemeKnobs, isDark: Boolean): AloneColorRoles {
    val m = if (isDark) DARK else LIGHT
    val h = theme.hue
    val c = theme.chroma
    val surfaceL = if (isDark) theme.darkSurfaceL else theme.lightSurfaceL
    val tint = if (isDark) theme.darkTint else theme.lightTint
    val lift = m.lift

    val surface = oklch(surfaceL, tint, h)
    val surfaceContainerHigh = oklch(surfaceL + lift * .058f, tint, h)
    val outlineVariant = oklch(m.olvL, tint * 1.3f, h)
    val primary = oklch(m.accL, c, h)
    val onSurface = oklch(m.onL, tint * .8f, h)

    return AloneColorRoles(
        primary = primary,
        onPrimary = oklch(m.onAccL, c * .30f, h),
        primaryContainer = oklch(m.accCL, c * .88f, h),
        onPrimaryContainer = oklch(m.onAccCL, c * .42f, h),

        secondary = oklch(m.accL - lift * .055f, c * .42f, h + 26f),
        onSecondary = oklch(m.onAccL, c * .30f, h + 26f), // extrapolated, see AloneColorRoles doc
        secondaryContainer = oklch(surfaceL + lift * .115f, c * .30f, h + 26f),
        onSecondaryContainer = oklch(m.onAccCL, c * .28f, h + 26f),

        tertiary = oklch(m.accL, c * .82f, h + 64f),
        onTertiary = oklch(m.onAccL, c * .30f, h + 64f), // extrapolated, see AloneColorRoles doc
        tertiaryContainer = oklch(surfaceL + lift * .135f, c * .44f, h + 64f),
        onTertiaryContainer = oklch(m.onAccCL, c * .34f, h + 64f),

        surface = surface,
        surfaceDim = oklch(surfaceL - lift * .030f, tint, h),
        surfaceBright = oklch(surfaceL + lift * .130f, tint, h),
        surfaceContainerLowest = oklch(surfaceL - lift * .045f, tint, h),
        surfaceContainerLow = oklch(surfaceL + lift * .020f, tint, h),
        surfaceContainer = oklch(surfaceL + lift * .036f, tint, h),
        surfaceContainerHigh = surfaceContainerHigh,
        surfaceContainerHighest = oklch(surfaceL + lift * .085f, tint, h),
        onSurface = onSurface,
        onSurfaceVariant = oklch(m.onvL, tint * 1.7f, h),

        outline = oklch(m.olL, tint * 1.5f, h),
        outlineVariant = outlineVariant,

        ok = oklch(m.stL, .155f, 155f),
        okContainer = oklch(m.stCL, .085f, 155f),
        onOkContainer = oklch(m.onStCL, .055f, 155f),
        warn = oklch(m.stL, .150f, 78f),
        warnContainer = oklch(m.stCL, .085f, 78f),
        onWarnContainer = oklch(m.onStCL, .050f, 78f),
        error = oklch(m.stL, .165f, 26f),
        errorContainer = oklch(m.stCL, .095f, 26f),
        onErrorContainer = oklch(m.onStCL, .055f, 26f),

        inverseSurface = oklch(m.onL, tint * .8f, h),
        inverseOnSurface = oklch(surfaceL, tint, h),
        scrim = oklch(surfaceL * .34f, tint, h, alpha = .62f),

        glassFill = surfaceContainerHigh.copy(alpha = m.glassA),
        glassEdge = mixOklab(primary, outlineVariant, 0.26f),
        glassHi = Color.White.copy(alpha = m.hiA),
        stateHover = onSurface.copy(alpha = .08f),
        statePress = primary.copy(alpha = .16f),
        stateSelect = primary.copy(alpha = .14f),
        shadowKey = if (isDark) Color.Black.copy(alpha = m.shadowKeyAlpha) else oklch(.28f, .03f, h, alpha = m.shadowKeyAlpha),
        shadowAmbient = if (isDark) Color.Black.copy(alpha = m.shadowAmbientAlpha) else oklch(.28f, .03f, h, alpha = m.shadowAmbientAlpha),
    )
}
