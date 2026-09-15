package com.v2ray.ang.ui.alone.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.v2ray.ang.ui.alone.icons.Icons as AloneIconSet
import com.v2ray.ang.ui.theme.AloneColorRoles
import com.v2ray.ang.ui.theme.AloneEasing
import com.v2ray.ang.ui.theme.LocalAloneColors
import com.v2ray.ang.ui.theme.LocalAloneReducedMotion
import com.v2ray.ang.ui.theme.aloneSpringBouncy
import com.v2ray.ang.ui.theme.mixOklab
import kotlin.math.cos
import kotlin.math.sin

private const val TRACK_RING_SIZE = 196
private const val BTN_SIZE = 158
private const val ICON_SIZE = 58
private const val PULSE_SIZE = 160
private const val WAVE_SIZE = 158
private const val ORBIT_SIZE = 228
private const val ORBIT_RADIUS = 114f

/** Simple 5-state view of ConnectionState, matching the CSS `data-state` values exactly. */
enum class DialVisualState { OFF, CONNECTING, ON, FAILED, DISCONNECTING }

fun ConnectionState.toVisual(): DialVisualState = when (this) {
    is ConnectionState.Off -> DialVisualState.OFF
    is ConnectionState.Connecting -> DialVisualState.CONNECTING
    is ConnectionState.On -> DialVisualState.ON
    is ConnectionState.Failed -> DialVisualState.FAILED
    is ConnectionState.Disconnecting -> DialVisualState.DISCONNECTING
}

/**
 * spec.components ConnectDial: "progress ring = drawArc with an Animatable sweep;
 * press = scaleOnPress 0.90; success = two expanding rings 180ms apart."
 *
 * @param sweepFraction 0f..1f — off:0, connecting:(step+1)/4, on:1 (spec's exact formula)
 */
@Composable
fun ConnectDial(
    state: DialVisualState,
    sweepFraction: Float,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAloneColors.current
    val reducedMotion = LocalAloneReducedMotion.current
    val density = LocalDensity.current

    val ringColor = when (state) {
        DialVisualState.FAILED -> colors.error
        DialVisualState.ON -> colors.ok
        else -> colors.primary
    }
    val ringVisible = state != DialVisualState.OFF

    // --dial-sweep transitions over 1.35s ease-standard in the source.
    val sweep = remember { Animatable(0f) }
    LaunchedEffect(sweepFraction) {
        sweep.animateTo(sweepFraction, tween(1350, easing = AloneEasing.Standard))
    }
    val ringAlpha = remember { Animatable(if (ringVisible) 1f else 0f) }
    LaunchedEffect(ringVisible) {
        ringAlpha.animateTo(if (ringVisible) 1f else 0f, tween(220, easing = AloneEasing.Standard))
    }

    // ── idle ambient loops (dialBob / dialBreathe / iconFloat / pulses / orbit spin / twinkle) ──
    val ambient = rememberInfiniteTransition(label = "dialAmbient")
    val busy = state == DialVisualState.CONNECTING || state == DialVisualState.DISCONNECTING
    val glowPeriod = if (busy) 1400 else 4800

    val bobT by ambient.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing)),
        label = "bob",
    )
    val breatheT by ambient.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(glowPeriod, easing = LinearEasing)),
        label = "breathe",
    )
    val floatT by ambient.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4600, easing = LinearEasing)),
        label = "float",
    )
    val orbitSpin by ambient.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(22_000, easing = LinearEasing)),
        label = "orbitSpin",
    )
    val pulseT by ambient.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing)),
        label = "pulse",
    )

    // ── press ──
    val pressScale = remember { Animatable(1f) }

    // ── one-shot success burst: two rings 180ms apart + a button scale-bounce ──
    val wave1 = remember { Animatable(0f) }
    val wave2 = remember { Animatable(0f) }
    val burst = remember { Animatable(1f) }
    var wasOn by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(state) {
        if (state == DialVisualState.ON && wasOn == 0f) {
            wasOn = 1f
            burst.snapTo(0.88f)
            burst.animateTo(
                1f,
                keyframes {
                    durationMillis = 900
                    0.88f at 0
                    1.13f at (900 * 0.24f).toInt()
                    0.96f at (900 * 0.48f).toInt()
                    1.035f at (900 * 0.70f).toInt()
                    0.992f at (900 * 0.88f).toInt()
                    1f at 900
                },
            )
        } else if (state != DialVisualState.ON) {
            wasOn = 0f
        }
    }
    LaunchedEffect(wasOn) {
        if (wasOn == 1f) {
            wave1.snapTo(0f); wave1.animateTo(1f, tween(1050, easing = AloneEasing.Decelerate))
        }
    }
    LaunchedEffect(wasOn) {
        if (wasOn == 1f) {
            kotlinx.coroutines.delay(180)
            wave2.snapTo(0f); wave2.animateTo(1f, tween(1300, easing = AloneEasing.Decelerate))
        }
    }

    // ── failure shake ──
    val shake = remember { Animatable(0f) }
    LaunchedEffect(state) {
        if (state == DialVisualState.FAILED) {
            shake.snapTo(0f)
            shake.animateTo(
                1f,
                keyframes {
                    durationMillis = 760
                    0f at 0
                    -1f at (760 * .14f).toInt()
                    0.86f at (760 * .31f).toInt()
                    -0.57f at (760 * .48f).toInt()
                    0.36f at (760 * .65f).toInt()
                    -0.14f at (760 * .82f).toInt()
                    0f at 760
                },
            )
        }
    }

    // ── icon morph: off <-> on, spring-bouncy rotate+scale+fade, per spec ──
    val iconOnProgress = remember { Animatable(if (state == DialVisualState.ON) 1f else 0f) }
    LaunchedEffect(state) {
        iconOnProgress.animateTo(if (state == DialVisualState.ON) 1f else 0f, aloneSpringBouncy())
    }

    val bob = bobKeyframe(bobT)
    val breathe = breatheKeyframe(breatheT)
    val floatY = floatKeyframe(floatT)
    val shakeOffset = shakeKeyframe(shake.value)

    Box(
        modifier = modifier.size(ORBIT_SIZE.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        // outer glow halo (glow-primary / glow-ok / glow-error) — soft, wide, behind everything
        Canvas(Modifier.size((BTN_SIZE + 70).dp)) {
            val c = if (state == DialVisualState.FAILED) colors.error else if (state == DialVisualState.ON) colors.ok else colors.primary
            drawCircle(
                brush = Brush.radialGradient(listOf(c.copy(alpha = .34f), c.copy(alpha = 0f))),
                radius = size.minDimension / 2,
            )
        }

        // orbit particles — only "go" while connected
        if (state == DialVisualState.ON || wasOn == 1f) {
            Canvas(Modifier.size(ORBIT_SIZE.dp).graphicsLayer { rotationZ = if (reducedMotion) 0f else orbitSpin }) {
                val dotAngles = floatArrayOf(0f, 120f, 238f)
                dotAngles.forEachIndexed { i, aDeg ->
                    val aRad = (aDeg - 90f) * (kotlin.math.PI.toFloat() / 180f)
                    val cx = size.width / 2 + ORBIT_RADIUS.dp.toPx() * cos(aRad)
                    val cy = size.height / 2 + ORBIT_RADIUS.dp.toPx() * sin(aRad)
                    val twinklePhase = (pulseT * (2.6f / (2.6f + i * 0.6f)) + i * 0.33f) % 1f
                    val alpha = 0.26f + (1f - 0.26f) * kotlin.math.abs(kotlin.math.sin(twinklePhase * kotlin.math.PI.toFloat()))
                    drawCircle(color = colors.ok, radius = 2.5.dp.toPx(), center = Offset(cx, cy), alpha = alpha)
                }
            }
        }

        // three ambient pulse rings — connected only
        if (state == DialVisualState.ON) {
            listOf(0f, 1.05f, 2.1f).forEach { delaySec ->
                val phase = (((pulseT * 3.2f) - delaySec).mod(3.2f)) / 3.2f
                val ringAlphaPulse = if (phase < 0.18f) (phase / 0.18f) * 0.6f else 0.6f * (1f - (phase - 0.18f) / 0.82f)
                val ringScale = 0.97f + phase * (1.7f - 0.97f)
                Canvas(Modifier.size(PULSE_SIZE.dp)) {
                    drawCircle(
                        color = colors.ok.copy(alpha = ringAlphaPulse.coerceIn(0f, 1f)),
                        radius = (size.minDimension / 2) * ringScale,
                        style = Stroke(width = (1.6f - phase * 1.2f).dp.toPx()),
                    )
                }
            }
        }

        // success double-shockwave
        if (wave1.value > 0f && wave1.value < 1f) {
            Canvas(Modifier.size(WAVE_SIZE.dp)) {
                drawCircle(
                    color = colors.ok.copy(alpha = (0.9f * (1f - wave1.value)).coerceIn(0f, 1f)),
                    radius = (size.minDimension / 2) * (0.94f + wave1.value * 1.36f),
                    style = Stroke(width = 1.6.dp.toPx()),
                )
            }
        }
        if (wave2.value > 0f && wave2.value < 1f) {
            Canvas(Modifier.size((WAVE_SIZE + 20).dp)) {
                drawCircle(
                    color = colors.primary.copy(alpha = (0.9f * 0.72f * (1f - wave2.value)).coerceIn(0f, 1f)),
                    radius = (size.minDimension / 2) * (0.94f + wave2.value * 1.36f),
                    style = Stroke(width = 1.2.dp.toPx()),
                )
            }
        }

        // track (static) + ring (animated progress)
        Canvas(Modifier.size(TRACK_RING_SIZE.dp)) {
            val strokeW = 3.dp.toPx()
            drawCircle(
                color = colors.onSurface.copy(alpha = 0.09f),
                radius = (size.minDimension - strokeW) / 2,
                style = Stroke(width = strokeW),
            )
        }
        Canvas(Modifier.size(TRACK_RING_SIZE.dp).graphicsLayer { alpha = ringAlpha.value }) {
            val strokeW = 3.dp.toPx()
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * sweep.value,
                useCenter = false,
                style = Stroke(width = strokeW, cap = StrokeCap.Round),
                topLeft = Offset(strokeW / 2, strokeW / 2),
                size = Size(size.width - strokeW, size.height - strokeW),
            )
        }

        // the button itself — idle bob + press scale + glassy layered background
        Box(
            modifier = Modifier
                .size(BTN_SIZE.dp)
                .graphicsLayer {
                    translationY = (if (reducedMotion) 0f else bob.second.dp.toPx()) + shakeOffset.second
                    rotationZ = (if (reducedMotion) 0f else bob.first) + shakeOffset.first
                    scaleX = pressScale.value * burst.value
                    scaleY = pressScale.value * burst.value
                }
                .clip(CircleShape)
                .background(dialButtonBrush(state, colors))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            pressScale.animateTo(0.90f, tween(90, easing = LinearEasing))
                            tryAwaitRelease()
                            pressScale.animateTo(1f, aloneSpringBouncy())
                        },
                        onTap = { onTap() },
                    )
                },
            contentAlignment = androidx.compose.ui.Alignment.Center,
        ) {
            // breathing inner glow
            Canvas(
                Modifier.size(BTN_SIZE.dp).graphicsLayer {
                    val s = 1f + (if (reducedMotion) 0f else breathe.first) * 0.08f
                    scaleX = s; scaleY = s; alpha = 0.7f + (if (reducedMotion) 0f else breathe.second) * 0.3f
                },
            ) {
                val tint = if (state == DialVisualState.ON) colors.ok else colors.primary
                drawCircle(brush = Brush.radialGradient(listOf(tint.copy(alpha = .20f), tint.copy(alpha = 0f))))
            }

            // icon: off <-> on morph (opposite rotate/scale, per spec)
            Box(
                Modifier.size(ICON_SIZE.dp).graphicsLayer {
                    translationY = if (reducedMotion) 0f else with(density) { floatY.dp.toPx() }
                },
                contentAlignment = androidx.compose.ui.Alignment.Center,
            ) {
                val offA = 1f - iconOnProgress.value
                Icon(
                    imageVector = AloneIconSet.Alone.Power,
                    contentDescription = null,
                    tint = colors.onSurface,
                    modifier = Modifier.size(ICON_SIZE.dp).graphicsLayer {
                        alpha = offA
                        rotationZ = 210f * iconOnProgress.value
                        val s = 1f - iconOnProgress.value * 0.5f
                        scaleX = s; scaleY = s
                    },
                )
                Icon(
                    imageVector = AloneIconSet.Alone.Shield,
                    contentDescription = null,
                    tint = colors.onSurface,
                    modifier = Modifier.size(ICON_SIZE.dp).graphicsLayer {
                        alpha = iconOnProgress.value
                        rotationZ = -150f * offA
                        val s = 1f - offA * 0.45f
                        scaleX = s; scaleY = s
                    },
                )
            }
        }
    }
}

/** dialBtn's layered glass background, state-tinted (primary default, ok when on). */
private fun dialButtonBrush(state: DialVisualState, colors: AloneColorRoles): Brush {
    val tint = if (state == DialVisualState.ON) colors.ok else colors.primary
    return Brush.radialGradient(
        colors = listOf(
            mixOklab(tint, colors.surfaceContainerHigh, 0.46f),
            colors.surfaceContainerHigh.copy(alpha = 0.62f),
        ),
    )
}

// ── keyframe helpers: (rotationDeg, translateYPx) sampled at fraction t in 0..1 ──
private fun bobKeyframe(t: Float): Pair<Float, Float> = when {
    t < 0.28f -> lerpPair(0f to 0f, .6f to -4f, t / 0.28f)
    t < 0.56f -> lerpPair(.6f to -4f, -.45f to 1.5f, (t - 0.28f) / 0.28f)
    t < 0.78f -> lerpPair(-.45f to 1.5f, .2f to -2f, (t - 0.56f) / 0.22f)
    else -> lerpPair(.2f to -2f, 0f to 0f, (t - 0.78f) / 0.22f)
}

/** (scaleDelta 0..1 -> multiply by 0.08 outside, opacityDelta 0..1 -> multiply by 0.3 outside) */
private fun breatheKeyframe(t: Float): Pair<Float, Float> = when {
    t < 0.38f -> lerpPair(0f to 0f, 1f to 1f, t / 0.38f)
    t < 0.66f -> lerpPair(1f to 1f, .8125f to .467f, (t - 0.38f) / 0.28f)
    else -> lerpPair(.8125f to .467f, 0f to 0f, (t - 0.66f) / 0.34f)
}

private fun floatKeyframe(t: Float): Float = when {
    t < 0.45f -> lerp(0f, -2.5f, t / 0.45f)
    t < 0.72f -> lerp(-2.5f, 1f, (t - 0.45f) / 0.27f)
    else -> lerp(1f, 0f, (t - 0.72f) / 0.28f)
}

/** returns (rotationDeg, translationXPx) for the failure shake, driven by shake.value 0..1 */
private fun shakeKeyframe(v: Float): Pair<Float, Float> {
    val stops = listOf(
        0f to (0f to 0f), .14f to (-2.4f to -7f), .31f to (2f to 6f),
        .48f to (-1.2f to -4f), .65f to (.7f to 2.5f), .82f to (-.25f to -1f), 1f to (0f to 0f),
    )
    for (i in 0 until stops.size - 1) {
        val (f0, v0) = stops[i]
        val (f1, v1) = stops[i + 1]
        if (v in f0..f1) {
            val local = if (f1 == f0) 0f else (v - f0) / (f1 - f0)
            return lerp(v0.first, v1.first, local) to lerp(v0.second, v1.second, local)
        }
    }
    return 0f to 0f
}

private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t.coerceIn(0f, 1f)
private fun lerpPair(a: Pair<Float, Float>, b: Pair<Float, Float>, t: Float) = lerp(a.first, b.first, t) to lerp(a.second, b.second, t)
