package com.v2ray.ang.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * OKLCH → sRGB conversion.
 *
 * Android/Compose has no native OKLCH color space, so every color the HTML
 * prototype expresses as `oklch(L C H)` is converted here with the same math
 * the browser uses internally (CSS Color 4 defines oklch() in terms of OKLab,
 * and OKLab→linear-sRGB is Björn Ottosson's published transform). This is
 * standard, publicly documented color science — not anything proprietary.
 *
 * Pipeline: OKLCH(L,C,H) -> OKLab(L,a,b) -> linear sRGB -> gamma-encoded sRGB.
 *
 * @param l Lightness, 0..1
 * @param c Chroma, typically 0..~0.4
 * @param hDeg Hue, degrees (0..360)
 * @param alpha 0..1
 */
fun oklch(l: Float, c: Float, hDeg: Float, alpha: Float = 1f): Color {
    val hRad = hDeg * (PI.toFloat() / 180f)
    val a = c * cos(hRad)
    val b = c * sin(hRad)
    return oklab(l, a, b, alpha)
}

private fun oklab(l: Float, a: Float, b: Float, alpha: Float): Color {
    // OKLab -> LMS (cube-root domain)
    val l_ = l + 0.3963377774f * a + 0.2158037573f * b
    val m_ = l - 0.1055613458f * a - 0.0638541728f * b
    val s_ = l - 0.0894841775f * a - 1.2914855480f * b

    val lCube = l_ * l_ * l_
    val mCube = m_ * m_ * m_
    val sCube = s_ * s_ * s_

    // LMS -> linear sRGB
    var r = +4.0767416621f * lCube - 3.3077115913f * mCube + 0.2309699292f * sCube
    var g = -1.2684380046f * lCube + 2.6097574011f * mCube - 0.3413193965f * sCube
    var bl = -0.0041960863f * lCube - 0.7034186147f * mCube + 1.7076147010f * sCube

    // Linear -> gamma-encoded sRGB, clamped (out-of-gamut colors just clip,
    // same as what a browser does for the small handful of over-saturated
    // combinations this palette never actually reaches)
    r = linearToSrgb(r).coerceIn(0f, 1f)
    g = linearToSrgb(g).coerceIn(0f, 1f)
    bl = linearToSrgb(bl).coerceIn(0f, 1f)

    return Color(red = r, green = g, blue = bl, alpha = alpha.coerceIn(0f, 1f))
}

private fun linearToSrgb(c: Float): Float {
    val cAbs = kotlin.math.abs(c)
    val sign = if (c < 0f) -1f else 1f
    return if (cAbs <= 0.0031308f) {
        c * 12.92f
    } else {
        sign * (1.055f * cAbs.pow(1f / 2.4f) - 0.055f)
    }
}

private fun srgbToLinear(c: Float): Float {
    val cAbs = kotlin.math.abs(c)
    val sign = if (c < 0f) -1f else 1f
    return if (cAbs <= 0.04045f) c / 12.92f else sign * ((cAbs + 0.055f) / 1.055f).pow(2.4f)
}

/** L, a, b (Cartesian OKLab), used only as an intermediate for [mixOklab]. */
private data class Oklab(val l: Float, val a: Float, val b: Float, val alpha: Float)

private fun Color.toOklab(): Oklab {
    val r = srgbToLinear(red)
    val g = srgbToLinear(green)
    val b = srgbToLinear(blue)

    val l = 0.4122214708f * r + 0.5363325363f * g + 0.0514459929f * b
    val m = 0.2119034982f * r + 0.6806995451f * g + 0.1073969566f * b
    val s = 0.0883024619f * r + 0.2817188376f * g + 0.6299787005f * b

    val l_ = l.pow(1f / 3f)
    val m_ = m.pow(1f / 3f)
    val s_ = s.pow(1f / 3f)

    return Oklab(
        l = 0.2104542553f * l_ + 0.7936177850f * m_ - 0.0040720468f * s_,
        a = 1.9779984951f * l_ - 2.4285922050f * m_ + 0.4505937099f * s_,
        b = 0.0259040371f * l_ + 0.7827717662f * m_ - 0.8086757660f * s_,
        alpha = alpha,
    )
}

/**
 * Perceptual blend of two already-resolved sRGB colors in OKLab space —
 * the Kotlin equivalent of CSS `color-mix(in oklab, a p%, b)`.
 *
 * @param aFraction how much of [a] to keep, 0..1 (CSS's "p%" on the first color)
 */
fun mixOklab(a: Color, b: Color, aFraction: Float): Color {
    val oa = a.toOklab()
    val ob = b.toOklab()
    val t = aFraction.coerceIn(0f, 1f)
    val l = oa.l * t + ob.l * (1 - t)
    val ca = oa.a * t + ob.a * (1 - t)
    val cb = oa.b * t + ob.b * (1 - t)
    val alpha = oa.alpha * t + ob.alpha * (1 - t)
    // OKLab -> OKLCH -> back through the same sRGB path as oklch() above,
    // reusing it directly keeps a single source of truth for the gamma step.
    val c = kotlin.math.sqrt(ca * ca + cb * cb)
    val h = kotlin.math.atan2(cb, ca) * (180f / PI.toFloat())
    return oklch(l, c, h, alpha)
}
