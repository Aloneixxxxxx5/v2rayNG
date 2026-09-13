package com.v2ray.ang.ui.alone.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.v2ray.ang.ui.alone.icons.Icons as AloneIconSet
import com.v2ray.ang.ui.theme.AloneShape
import com.v2ray.ang.ui.theme.AloneTheme

/**
 * spec screen onb-notif. `granted` should come from
 * rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())
 * on Android 13+ (POST_NOTIFICATIONS) — kept as local state here since wiring
 * a real launcher needs an Activity context this preview doesn't assume.
 */
@Composable
fun OnboardingNotifScreen(
    onSkip: () -> Unit,
    onGrantRequested: () -> Unit,
    granted: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = AloneTheme.colors
    OnboardingScaffold(
        step = OnboardingStep.NOTIF,
        heroIcon = AloneIconSet.Alone.Bell,
        heroTint = colors.warn,
        title = "در جریان بمان",
        body = "اجازه بده ALONE VPN وضعیت تانل و قطع‌شدن‌های ناگهانی را همان لحظه به تو خبر بدهد.",
        note = "اندروید ۱۳ و بالاتر برای این کار مجوز POST_NOTIFICATIONS می‌خواهد.",
        resultText = if (granted) "نوتیفیکیشن فعال شد" else null,
        onSkipOrBack = onSkip,
        modifier = modifier,
        actions = {
            GlassCta(
                icon = AloneIconSet.Alone.Bell,
                label = "اجازه‌ی نوتیفیکیشن",
                onClick = onGrantRequested,
            )
        },
    )
}

/** `.glass.btn.tall.wide` CTA — shared visual, kept local since only onboarding uses this exact shape+icon pairing. */
@Composable
internal fun GlassCta(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    val colors = AloneTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(AloneShape.large)
            .background(colors.glassFill)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = colors.primary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
    }
}
