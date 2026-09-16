package com.v2ray.ang.ui.alone.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.v2ray.ang.ui.alone.icons.Icons as AloneIconSet
import com.v2ray.ang.ui.theme.AloneShape
import com.v2ray.ang.ui.theme.AloneTheme

/** Which of the 3 onboarding steps this is — drives which dot is lit. */
enum class OnboardingStep { NOTIF, VPN, LOOK }

/**
 * spec screens onb-notif / onb-vpn / onb-look share this shell exactly
 * (`.ob`): top row (skip-or-back + dots), hero chip, title, body, an
 * optional post-grant confirmation row, a note, and bottom-pinned actions.
 */
@Composable
fun OnboardingScaffold(
    step: OnboardingStep,
    heroIcon: ImageVector,
    heroTint: Color,
    title: String,
    body: String,
    note: String,
    resultText: String? = null, // null = not shown yet; non-null = permission granted
    onSkipOrBack: () -> Unit,
    skipIsBack: Boolean = false,
    actions: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AloneTheme.colors
    Column(modifier.fillMaxSize().background(colors.surface).padding(20.dp)) {
        // ob-top
        Row(Modifier.fillMaxWidth().height(40.dp), verticalAlignment = Alignment.CenterVertically) {
            if (skipIsBack) {
                androidx.compose.foundation.layout.Box(
                    Modifier.size(40.dp).clip(CircleShape).clickable(onClick = onSkipOrBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(AloneIconSet.Alone.Chevron, contentDescription = "back", tint = colors.onSurface, modifier = Modifier.size(18.dp))
                }
            } else {
                Text(
                    "رد شدن",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.clickable(onClick = onSkipOrBack),
                )
            }
            Spacer(Modifier.weight(1f))
            Dots(step)
        }

        // ob-hero
        androidx.compose.foundation.layout.Box(
            Modifier
                .padding(top = 20.dp, bottom = 16.dp)
                .size(92.dp)
                .clip(AloneShape.extraLarge)
                .background(heroTint.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(heroIcon, contentDescription = null, tint = heroTint, modifier = Modifier.size(38.dp))
        }

        Text(title, style = MaterialTheme.typography.headlineMedium, color = colors.onSurface, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Text(
            body,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 9.dp),
        )

        if (resultText != null) {
            Row(
                Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(AloneIconSet.Alone.Check, contentDescription = null, tint = colors.ok, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(7.dp))
                Text(resultText, style = MaterialTheme.typography.bodyMedium, color = colors.ok)
            }
        }

        Text(
            note,
            style = MaterialTheme.typography.labelMedium,
            color = colors.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 11.dp),
        )

        Spacer(Modifier.weight(1f))
        Column(Modifier.fillMaxWidth().padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            actions()
        }
    }
}

@Composable
internal fun Dots(step: OnboardingStep) {
    val colors = AloneTheme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
        OnboardingStep.entries.forEach { s ->
            val on = s == step
            androidx.compose.foundation.layout.Box(
                Modifier
                    .height(6.dp)
                    .width(if (on) 18.dp else 6.dp)
                    .clip(AloneShape.full)
                    .background(if (on) colors.primary else colors.outlineVariant),
            )
        }
    }
}
