package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEffectManager
import com.example.data.model.ChoreItem
import com.example.data.model.TaskDifficulty
import com.example.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Custom Compose Floating & Drifting Animation Modifier for Cloud Cards.
 *
 * Implements:
 * 1. Appearance Animation: Smooth buoyant entrance where the card drifts up from an offset,
 *    springing into floating equilibrium with soft overshoot damping.
 * 2. Ambient Floating & Drifting: Continuous organic dual-axis sinusoidal oscillation
 *    (vertical bobbing and subtle horizontal drifting sway with gentle roll/tilt).
 * 3. Selection Drifting: Expressive lift effect when chosen during pairwise comparison,
 *    drifting smoothly upward into the clouds with scale bloom and atmospheric aura,
 *    while companion cards gently sink and yield focus.
 */
fun Modifier.cloudFloatingDrift(
    appearTrigger: Any? = null,
    isSelected: Boolean = false,
    isUnselected: Boolean = false,
    driftPhaseOffsetMs: Int = 0,
    continuousDriftEnabled: Boolean = true,
    floatRangeDp: Float = 5.5f,
    driftRangeDp: Float = 3.5f,
    tiltRangeDeg: Float = 0.85f,
    selectedLiftDp: Float = -24f,
    unselectedSinkDp: Float = 12f
): Modifier = composed {
    val density = LocalDensity.current
    val floatRangePx = with(density) { floatRangeDp.dp.toPx() }
    val driftRangePx = with(density) { driftRangeDp.dp.toPx() }
    val selectedLiftPx = with(density) { selectedLiftDp.dp.toPx() }
    val unselectedSinkPx = with(density) { unselectedSinkDp.dp.toPx() }

    // 1. Ambient continuous floating & drifting transitions
    val infiniteTransition = rememberInfiniteTransition(label = "CloudContinuousDrift")

    val ambientFloatY by if (continuousDriftEnabled) {
        infiniteTransition.animateFloat(
            initialValue = -floatRangePx,
            targetValue = floatRangePx,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (2900 + (driftPhaseOffsetMs % 700)),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ambientFloatY"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    val ambientDriftX by if (continuousDriftEnabled) {
        infiniteTransition.animateFloat(
            initialValue = -driftRangePx,
            targetValue = driftRangePx,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (3800 + (driftPhaseOffsetMs % 900)),
                    easing = LinearOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ambientDriftX"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    val ambientTilt by if (continuousDriftEnabled) {
        infiniteTransition.animateFloat(
            initialValue = -tiltRangeDeg,
            targetValue = tiltRangeDeg,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3400,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ambientTilt"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    // 2. Entrance / Appearance Animation (triggered when appearTrigger changes)
    val appearAnim = remember(appearTrigger) { Animatable(0f) }
    LaunchedEffect(appearTrigger) {
        appearAnim.snapTo(0f)
        appearAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = 0.68f,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    val entranceProgress = appearAnim.value
    val entranceOffsetY = with(density) { (1f - entranceProgress) * 36.dp.toPx() }
    val entranceScale = 0.88f + (entranceProgress * 0.12f)
    val entranceAlpha = entranceProgress.coerceIn(0f, 1f)

    // 3. Selection Drifting State Animation (lifts when selected, sinks when unselected)
    val selectionTarget = when {
        isSelected -> 1f
        isUnselected -> -1f
        else -> 0f
    }

    val selectionProgress by animateFloatAsState(
        targetValue = selectionTarget,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = Spring.StiffnessLow
        ),
        label = "selectionDriftProgress"
    )

    val (selectionOffsetY, selectionScale, selectionAlpha) = when {
        selectionProgress > 0f -> {
            Triple(
                selectionProgress * selectedLiftPx,
                1f + (selectionProgress * 0.045f),
                1f
            )
        }
        selectionProgress < 0f -> {
            val sinkFactor = -selectionProgress
            Triple(
                sinkFactor * unselectedSinkPx,
                1f - (sinkFactor * 0.05f),
                1f - (sinkFactor * 0.60f)
            )
        }
        else -> Triple(0f, 1f, 1f)
    }

    this.graphicsLayer {
        // Compose dual-axis drifting movement: appearance + ambient + selection
        translationY = entranceOffsetY + ambientFloatY + selectionOffsetY
        translationX = ambientDriftX
        rotationZ = ambientTilt
        scaleX = entranceScale * selectionScale
        scaleY = entranceScale * selectionScale
        alpha = entranceAlpha * selectionAlpha
    }
}

/**
 * Floating Cloud Vapor Puffs effect that drifts upward when a cloud card is selected.
 */
@Composable
fun CloudVaporDriftEffect(
    isActive: Boolean,
    tintColor: Color = CorporateAccentBlue,
    modifier: Modifier = Modifier
) {
    if (!isActive) return

    val infiniteTransition = rememberInfiniteTransition(label = "CloudVapor")
    val vaporProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vaporProgress"
    )

    Canvas(modifier = modifier.fillMaxWidth().height(40.dp)) {
        val width = size.width
        val height = size.height

        val puffs = listOf(
            Triple(0.2f, 18f, 0.4f),
            Triple(0.35f, 24f, 0.7f),
            Triple(0.5f, 28f, 0.9f),
            Triple(0.65f, 22f, 0.6f),
            Triple(0.8f, 16f, 0.35f)
        )

        puffs.forEachIndexed { index, (xRatio, baseRadius, speedMult) ->
            val localProgress = ((vaporProgress * speedMult) + (index * 0.2f)) % 1f
            val yOffset = height * (1f - localProgress)
            val currentRadius = baseRadius * (0.6f + 0.4f * localProgress)
            val alpha = ((1f - localProgress) * 0.45f).coerceIn(0f, 1f)

            drawCircle(
                color = tintColor.copy(alpha = alpha),
                radius = currentRadius,
                center = Offset(width * xRatio, yOffset)
            )
        }
    }
}

/**
 * General Floating Drift Cloud Card container.
 */
@Composable
fun FloatingDriftCloudCard(
    modifier: Modifier = Modifier,
    appearTrigger: Any? = null,
    isSelected: Boolean = false,
    isUnselected: Boolean = false,
    driftPhaseOffsetMs: Int = 0,
    continuousDriftEnabled: Boolean = true,
    backgroundColor: Color = Color(0xFF1E212E),
    borderColor: Color = CorporateAccentBlue.copy(alpha = 0.5f),
    elevation: Dp = 6.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val currentElevation = if (isSelected) 14.dp else elevation

    Box(
        modifier = modifier
            .cloudFloatingDrift(
                appearTrigger = appearTrigger,
                isSelected = isSelected,
                isUnselected = isUnselected,
                driftPhaseOffsetMs = driftPhaseOffsetMs,
                continuousDriftEnabled = continuousDriftEnabled
            )
            .shadow(
                elevation = currentElevation,
                shape = RoundedCornerShape(24.dp),
                spotColor = borderColor.copy(alpha = if (isSelected) 0.5f else 0.25f),
                ambientColor = borderColor.copy(alpha = if (isSelected) 0.3f else 0.15f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 2.dp else 1.5.dp,
                brush = Brush.linearGradient(
                    colors = if (isSelected) {
                        listOf(borderColor, Color.White, borderColor)
                    } else {
                        listOf(borderColor, borderColor.copy(alpha = 0.25f), borderColor)
                    }
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            content()
        }
    }
}

/**
 * Specialized Pairwise Drifting Cloud Option Card for the Pairwise Comparison Sorting Arena.
 * Features cloud styling, floating drift movement, appearance bounce, and selection lift.
 */
@Composable
fun PairwiseCloudCard(
    label: String,
    chore: ChoreItem,
    accentColor: Color,
    onClick: () -> Unit,
    appearTrigger: Any? = null,
    isSelected: Boolean = false,
    isUnselected: Boolean = false,
    driftPhaseOffsetMs: Int = 0,
    modifier: Modifier = Modifier
) {
    val cloudBorderBrush = if (isSelected) {
        Brush.linearGradient(
            listOf(
                Color.White,
                accentColor,
                Color(0xFF38BDF8),
                accentColor
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(
                accentColor.copy(alpha = 0.65f),
                CorporateCardBorder.copy(alpha = 0.8f)
            )
        )
    }

    val cloudBackgroundBrush = Brush.verticalGradient(
        if (isSelected) {
            listOf(
                accentColor.copy(alpha = 0.22f),
                Color(0xFF1E212F),
                Color(0xFF151722)
            )
        } else {
            listOf(
                Color(0xFF1E202B),
                Color(0xFF14151E)
            )
        }
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .cloudFloatingDrift(
                appearTrigger = appearTrigger,
                isSelected = isSelected,
                isUnselected = isUnselected,
                driftPhaseOffsetMs = driftPhaseOffsetMs,
                continuousDriftEnabled = true
            )
            .springPress(pressScale = 0.97f)
            .shadow(
                elevation = if (isSelected) 14.dp else 5.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = accentColor.copy(alpha = if (isSelected) 0.55f else 0.25f)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(cloudBackgroundBrush)
            .border(
                width = if (isSelected) 2.dp else 1.5.dp,
                brush = cloudBorderBrush,
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header Row: Cloud Label + Metadata Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cloud Option Pill with Cloud Emoji
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = if (isSelected) 0.35f else 0.18f))
                        .border(
                            1.dp,
                            accentColor.copy(alpha = if (isSelected) 0.8f else 0.45f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 9.dp, vertical = 3.5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "☁️", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = label,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) Color.White else accentColor,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Category & Difficulty Pill Badges
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CorporateSurfaceElevated)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = chore.iconCategory,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CorporateSurfaceElevated)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = chore.difficulty.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when (chore.difficulty) {
                                TaskDifficulty.EASY -> CorporateSuccess
                                TaskDifficulty.MEDIUM -> CorporateAccentAmber
                                TaskDifficulty.HARD -> CoralIndicator
                            }
                        )
                    }
                }
            }

            // Chore Title
            Text(
                text = chore.text,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 21.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Bottom Row: Rating Stats & Floating Prioritize Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⭐ Elo: ${chore.score}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CorporateAccentAmber
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${chore.matchupWins}W / ${chore.matchupTotal} matches",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                // Selection CTA Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) accentColor else accentColor.copy(alpha = 0.16f)
                        )
                        .border(
                            1.dp,
                            if (isSelected) Color.White else accentColor.copy(alpha = 0.4f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSelected) {
                            Text(
                                text = "Prioritizing ✨",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Prioritize ➔",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Standard CloudCard component with optional floating & drifting animations.
 */
@Composable
fun CloudCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = CloudWhite,
    borderColor: Color = MintGreenPrimary.copy(alpha = 0.6f),
    elevation: Dp = 4.dp,
    enableFloatingDrift: Boolean = false,
    driftPhaseOffsetMs: Int = 0,
    isSelected: Boolean = false,
    isUnselected: Boolean = false,
    appearTrigger: Any? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val driftModifier = if (enableFloatingDrift) {
        Modifier.cloudFloatingDrift(
            appearTrigger = appearTrigger,
            isSelected = isSelected,
            isUnselected = isUnselected,
            driftPhaseOffsetMs = driftPhaseOffsetMs
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(driftModifier)
            .shadow(
                elevation = if (isSelected) elevation * 2.5f else elevation,
                shape = RoundedCornerShape(26.dp),
                spotColor = borderColor.copy(alpha = 0.35f),
                ambientColor = borderColor.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(26.dp))
            .background(backgroundColor)
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = 0.3f),
                        borderColor
                    )
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun PoppableButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MintGreenPrimary,
    bottomBorderColor: Color = MintGreenBorderBottom,
    contentColor: Color = MintGreenDark,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .squishClickable(enabled = enabled, onClick = onClick)
            .shadow(
                elevation = if (enabled) 3.dp else 1.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = bottomBorderColor.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = bottomBorderColor,
                shape = RoundedCornerShape(32.dp)
            )
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = contentColor,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ChoreCategoryBadge(category: String, modifier: Modifier = Modifier) {
    val (emoji, bg, border) = when (category.uppercase()) {
        "CLEANING" -> Triple("🧹", BlushPinkLight, BlushPink)
        "FIXING" -> Triple("🔧", SkyBlueLight, SkyBlue)
        "KITCHEN" -> Triple("🧽", LemonLight, LemonGold)
        "PLANTS" -> Triple("🌿", MintGreenLight, MintGreenDark)
        "LAUNDRY" -> Triple("🧺", LilacLight, LilacDark)
        "ORGANIZING" -> Triple("📦", PeachPuff, BlushPink)
        "PETS" -> Triple("🐾", BlushPinkLight, BubblePink)
        else -> Triple("✨", MintGreenLight, MintGreenPrimary)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.5.dp, border.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = category.lowercase().replaceFirstChar { it.uppercase() },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SlateText
            )
        }
    }
}

@Composable
fun MemberAvatarPill(
    name: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .squishClickable(onClick = onClick)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) MintGreenPrimary else CloudWhite)
            .border(
                width = 2.dp,
                color = if (isSelected) MintGreenDark else SlateMuted.copy(alpha = 0.25f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (isSelected) SlateText else SlateMuted
            )
        }
    }
}

