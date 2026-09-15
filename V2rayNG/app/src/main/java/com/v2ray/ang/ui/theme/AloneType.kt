package com.v2ray.ang.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.v2ray.ang.R

/**
 * Font families. The .ttf files themselves still need to land in res/font/ —
 * see the README dropped next to this file for exactly which weights/names
 * are expected. Everything here degrades gracefully to the system font in
 * the meantime; nothing fails to compile if a font resource is missing
 * *as long as the placeholder files described in that README exist*.
 */
object AloneFonts {
    val persianUi = FontFamily(
        Font(R.font.vazirmatn_regular, FontWeight.W400),
        Font(R.font.vazirmatn_medium, FontWeight.W500),
        Font(R.font.vazirmatn_semibold, FontWeight.W600),
        Font(R.font.vazirmatn_bold, FontWeight.W700),
    )

    val latin = FontFamily(
        Font(R.font.geist_regular, FontWeight.W400),
        Font(R.font.geist_medium, FontWeight.W500),
        Font(R.font.geist_semibold, FontWeight.W600),
        Font(R.font.geist_bold, FontWeight.W700),
    )

    val mono = FontFamily(Font(R.font.geist_mono_regular, FontWeight.W400))
}

/** One row of the type scale, before script rules (fa/en) are applied. */
internal data class TypeRole(
    val sizeSp: Float,
    val weight: Int,
    val lineHeightMultiplier: Float,
    val trackingEm: Float,
)

// sp / weight / line-height multiplier / tracking(em) — copied verbatim from
// spec.typography.roles. Weights of 650/550 are intentional (the prototype's
// variable font interpolates them); Compose's FontWeight accepts any 1..1000
// value, so they're kept exact rather than rounded to 600/500.
private val ROLE_DISPLAY_L = TypeRole(40f, 700, 1.14f, -0.015f)
private val ROLE_DISPLAY_M = TypeRole(32f, 700, 1.17f, -0.012f)
private val ROLE_DISPLAY_S = TypeRole(26f, 700, 1.21f, -0.008f)
private val ROLE_HEADLINE_L = TypeRole(23f, 650, 1.26f, -0.005f)
private val ROLE_HEADLINE_M = TypeRole(20f, 650, 1.29f, -0.003f)
private val ROLE_HEADLINE_S = TypeRole(18f, 600, 1.33f, -0.002f)
private val ROLE_TITLE_L = TypeRole(16f, 600, 1.40f, 0f)
private val ROLE_TITLE_M = TypeRole(14f, 600, 1.43f, 0.004f)
private val ROLE_TITLE_S = TypeRole(13f, 600, 1.46f, 0.005f)
private val ROLE_BODY_L = TypeRole(14f, 400, 1.62f, 0f)
private val ROLE_BODY_M = TypeRole(13f, 400, 1.68f, 0f)
private val ROLE_BODY_S = TypeRole(12f, 400, 1.70f, 0f)
private val ROLE_LABEL_L = TypeRole(12f, 550, 1.36f, 0.01f)
private val ROLE_LABEL_M = TypeRole(11f, 550, 1.36f, 0.02f)
private val ROLE_LABEL_S = TypeRole(10f, 600, 1.34f, 0.045f)

/** Not an M3 Typography slot — exposed separately, used for eyebrow/meta labels. */
val AloneMicroRole = TypeRole(9.5f, 600, 1.30f, 0.09f)

/**
 * fa needs zero tracking (any letter-spacing breaks Arabic-script joining)
 * plus a looser line height for diacritics, and reads ~0.5sp larger; en uses
 * the role's declared tracking as-is. `isFarsi` should follow the app's
 * active UI language, not just layout direction.
 */
private fun TypeRole.toTextStyle(family: FontFamily, isFarsi: Boolean): TextStyle {
    val sizeSp = sizeSp + if (isFarsi) 0.5f else 0f
    val lineHeightMult = if (isFarsi) 1.11f else 1.0f
    val trackingEm = if (isFarsi) 0f else trackingEm
    return TextStyle(
        fontFamily = family,
        fontWeight = FontWeight(weight),
        fontSize = sizeSp.sp,
        lineHeight = (sizeSp * lineHeightMult).sp,
        letterSpacing = trackingEm.em,
    )
}

/**
 * Builds the M3 [Typography] for the given script. [isFarsi] picks the
 * Vazirmatn family + fa script rules; otherwise Geist + en rules — pass the
 * app's active language, independent of RTL/LTR layout direction.
 */
fun aloneTypography(isFarsi: Boolean): Typography {
    val family = if (isFarsi) AloneFonts.persianUi else AloneFonts.latin
    return Typography(
        displayLarge = ROLE_DISPLAY_L.toTextStyle(family, isFarsi),
        displayMedium = ROLE_DISPLAY_M.toTextStyle(family, isFarsi),
        displaySmall = ROLE_DISPLAY_S.toTextStyle(family, isFarsi),
        headlineLarge = ROLE_HEADLINE_L.toTextStyle(family, isFarsi),
        headlineMedium = ROLE_HEADLINE_M.toTextStyle(family, isFarsi),
        headlineSmall = ROLE_HEADLINE_S.toTextStyle(family, isFarsi),
        titleLarge = ROLE_TITLE_L.toTextStyle(family, isFarsi),
        titleMedium = ROLE_TITLE_M.toTextStyle(family, isFarsi),
        titleSmall = ROLE_TITLE_S.toTextStyle(family, isFarsi),
        bodyLarge = ROLE_BODY_L.toTextStyle(family, isFarsi),
        bodyMedium = ROLE_BODY_M.toTextStyle(family, isFarsi),
        bodySmall = ROLE_BODY_S.toTextStyle(family, isFarsi),
        labelLarge = ROLE_LABEL_L.toTextStyle(family, isFarsi),
        labelMedium = ROLE_LABEL_M.toTextStyle(family, isFarsi),
        labelSmall = ROLE_LABEL_S.toTextStyle(family, isFarsi),
    )
}

/** The `.t-micro` role — uppercase transform only applies in Latin (fa has no case). */
fun aloneMicroTextStyle(isFarsi: Boolean): TextStyle {
    val family = if (isFarsi) AloneFonts.persianUi else AloneFonts.latin
    return AloneMicroRole.toTextStyle(family, isFarsi)
}

/** userScale multipliers from spec.typography.userScale, for an accessibility text-size setting. */
val AloneUserScaleSteps = listOf(0.88f, 1.0f, 1.12f, 1.26f)
