package com.v2ray.ang.ui.alone.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.ui.alone.icons.Icons as AloneIconSet
import com.v2ray.ang.ui.theme.AloneTheme

/**
 * spec screen onb-vpn. `granted` should reflect the result of
 * VpnService.prepare() (null intent = already granted; otherwise launch the
 * returned intent via rememberLauncherForActivityResult(StartActivityForResult())).
 */
@Composable
fun OnboardingVpnScreen(
    onSkip: () -> Unit,
    onGrantRequested: () -> Unit,
    granted: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = AloneTheme.colors
    OnboardingScaffold(
        step = OnboardingStep.VPN,
        heroIcon = AloneIconSet.Alone.Shield,
        heroTint = colors.ok,
        title = "یک مجوز دیگر مانده",
        body = "اندروید پیش از ساختن تانل، تأیید خودت را می‌خواهد. پنجره‌ای که می‌بینی مال سیستم است، نه ما.",
        note = "در نسخه‌ی نیتیو این دکمه VpnService.prepare() را صدا می‌زند و نتیجه‌اش همان دیالوگ رسمی اندروید است.",
        resultText = if (granted) "مجوز VPN داده شد" else null,
        onSkipOrBack = onSkip,
        modifier = modifier,
        actions = {
            GlassCta(
                icon = AloneIconSet.Alone.Lock,
                label = "دادن مجوز VPN",
                onClick = onGrantRequested,
            )
        },
    )
}
