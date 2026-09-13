package com.v2ray.ang.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// spec.shapes, all dp. "full" (999) has no slot in M3's 5-step Shapes class —
// pill buttons/chips use AloneShape.full directly instead of MaterialTheme.shapes.
object AloneShape {
    val extraSmall = RoundedCornerShape(6.dp)
    val small = RoundedCornerShape(10.dp)
    val medium = RoundedCornerShape(14.dp)
    val large = RoundedCornerShape(20.dp)
    val extraLarge = RoundedCornerShape(28.dp)
    val full = RoundedCornerShape(999.dp)
}

val AloneShapes = Shapes(
    extraSmall = AloneShape.extraSmall,
    small = AloneShape.small,
    medium = AloneShape.medium,
    large = AloneShape.large,
    extraLarge = AloneShape.extraLarge,
)
