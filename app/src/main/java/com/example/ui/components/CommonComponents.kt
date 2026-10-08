package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EvidenceState
import com.example.ui.theme.*

// Section 7, 9 & 76: Core Design System Components for Śiṣya Abhyāsa

@Composable
fun SisyaCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceWhite,
    borderColor: Color = BorderColor,
    cornerRadius: Dp = 20.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val cardModifier = modifier
        .clip(shape)
        .background(backgroundColor, shape)
        .border(1.dp, borderColor, shape)
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onClick
                )
            } else Modifier
        )
        .padding(16.dp)

    Column(
        modifier = cardModifier,
        content = content
    )
}

@Composable
fun SisyaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isPrimary: Boolean = true,
    isLoading: Boolean = false,
    testTag: String = "sisya_button"
) {
    if (isPrimary) {
        Button(
            onClick = onClick,
            modifier = modifier
                .heightIn(min = 48.dp)
                .testTag(testTag),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryDarkTeal,
                contentColor = SurfaceWhite
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 2.dp)
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                color = SurfaceWhite
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .heightIn(min = 48.dp)
                .testTag(testTag),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = SurfaceWhite,
                contentColor = PrimaryText
            ),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.SolidColor(BorderColor)
            )
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = PrimaryDarkTeal, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                color = PrimaryText
            )
        }
    }
}

@Composable
fun EvidenceStateBadge(
    state: EvidenceState,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (state) {
        EvidenceState.SELF_DECLARED -> SecondarySurface to SecondaryText
        EvidenceState.PRACTICED -> InfoSoft to InfoBlue
        EvidenceState.OBSERVED -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
        EvidenceState.DEMONSTRATED -> PrimarySoftTeal to PrimaryDarkTeal
        EvidenceState.REPEATEDLY_DEMONSTRATED -> SuccessSoft to SuccessGreen
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(0.5.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = state.label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
            color = textColor
        )
    }
}

@Composable
fun StatusPill(
    text: String,
    color: Color = PrimaryDarkTeal,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = color
        )
    }
}

@Composable
fun TrustBadge(
    badgeType: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, txtColor, borderC) = when (badgeType) {
        "FACT" -> Triple(SuccessSoft, SuccessGreen, SuccessGreen.copy(alpha = 0.4f))
        "FACT & REVIEW" -> Triple(SuccessSoft, SuccessGreen, SuccessGreen.copy(alpha = 0.4f))
        "AI INTERPRETATION" -> Triple(WarningSoft, WarningAmber, WarningAmber.copy(alpha = 0.4f))
        "SOCRATIC GUIDANCE" -> Triple(PrimarySoftTeal, PrimaryDarkTeal, PrimaryDarkTeal.copy(alpha = 0.4f))
        else -> Triple(SecondarySurface, SecondaryText, BorderColor)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(0.5.dp, borderC, RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(
            text = badgeType,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
            color = txtColor
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = PrimaryText
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelLarge.copy(color = PrimaryDarkTeal, fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onActionClick)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun ProductLoopBar(
    currentStep: String,
    modifier: Modifier = Modifier
) {
    val steps = listOf("DISCOVER", "ARCHITECT", "LEARN", "BUILD", "COLLABORATE", "PROVE", "CAREER")
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PrimarySoftTeal)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEach { step ->
            val isCurrent = step.equals(currentStep, ignoreCase = true)
            val textColor = if (isCurrent) PrimaryDarkTeal else SecondaryText.copy(alpha = 0.7f)
            val weight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
            Text(
                text = step,
                fontSize = 9.sp,
                fontWeight = weight,
                color = textColor
            )
        }
    }
}
