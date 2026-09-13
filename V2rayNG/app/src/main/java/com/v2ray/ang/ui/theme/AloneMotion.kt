package com.v2ray.ang.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Two motion families, matching spec.motion.families:
 * - Spatial: position/size. Allowed to overshoot. Springs live here.
 * - Effects: colour/opacity/blur/elevation. Never overshoots — a value
 *   that clips past 1 reads as a glitch, not as energy. Tween only.
 *
 * Rule from the spec, kept as a rule rather than an API: press interactions
 * are quick and near-linear because the user caused them (AlonePressDurationMs,
 * AloneEasePressIn below); releases are slower and springy because the object
 * is recovering on its own. Never make the two symmetric.
 */
object AloneDuration {
    // spatial (may overshoot)
    const val SpatialFastMs = 200
    const val SpatialDefaultMs = 380
    const val SpatialSlowMs = 620

    // effects (never overshoots)
    const val EffectsFastMs = 130
    const val EffectsDefaultMs = 220
    const val EffectsSlowMs = 380

    /** Press-down duration — deliberately short/near-linear; the release uses a spring instead. */
    const val PressDownMs = 90

    /** Per-row delay for staggered list entrances. 40ms reads as simultaneous, 80ms as a queue. */
    const val ListStaggerMs = 52

    /** Ambient/looping animation periods — deliberately coprime-ish so no beat is perceivable together. */
    const val AmbientPeriodAMs = 23_000
    const val AmbientPeriodBMs = 31_000
    const val AmbientPeriodCMs = 41_000
}

object AloneEasing {
    val Standard: Easing = CubicBezierEasing(.2f, 0f, 0f, 1f)
    val Decelerate: Easing = CubicBezierEasing(0f, 0f, 0f, 1f)
    val Accelerate: Easing = CubicBezierEasing(.3f, 0f, 1f, 1f)
    val Emphasized: Easing = CubicBezierEasing(.22f, 1f, .24f, 1f)

    /** Press-in: near-linear, the user is driving it. */
    val PressIn: Easing = CubicBezierEasing(.4f, 0f, 1f, 1f)
}

/** effects-family tween using [easing], defaulting to the standard curve + default duration. */
fun aloneEffectsTween(
    durationMs: Int = AloneDuration.EffectsDefaultMs,
    easing: Easing = AloneEasing.Standard,
): FiniteAnimationSpec<Float> = tween(durationMs, easing = easing)

/** --ease-spring-snappy → spring(dampingRatio = .80, stiffness = 380), per spec.motion.easings. */
fun <T> aloneSpringSnappy(visibilityThreshold: T? = null): FiniteAnimationSpec<T> =
    spring(dampingRatio = 0.80f, stiffness = 380f, visibilityThreshold = visibilityThreshold)

/** --ease-spring-bouncy → spring(dampingRatio = .58, stiffness = 320). Connect dial, success pops. */
fun <T> aloneSpringBouncy(visibilityThreshold: T? = null): FiniteAnimationSpec<T> =
    spring(dampingRatio = 0.58f, stiffness = 320f, visibilityThreshold = visibilityThreshold)

/** --ease-spring-slow → spring(dampingRatio = .88, stiffness = 180). Sheets, large surfaces. */
fun <T> aloneSpringSlow(visibilityThreshold: T? = null): FiniteAnimationSpec<T> =
    spring(dampingRatio = 0.88f, stiffness = 180f, visibilityThreshold = visibilityThreshold)

/**
 * --ease-anticipate: a small backlash before committing — dips to -0.03 at
 * 10% of the duration, then resolves to 1 by the end. Meant for a 0f..1f
 * progress Animatable (e.g. a toggle or reveal), not a generic Easing curve.
 */
fun aloneAnticipateKeyframes(durationMs: Int = AloneDuration.SpatialDefaultMs): FiniteAnimationSpec<Float> =
    keyframes {
        durationMillis = durationMs
        -0.03f at (durationMs * 0.10f).toInt() using AloneEasing.Standard
        1f at durationMs using AloneEasing.Decelerate
    }
