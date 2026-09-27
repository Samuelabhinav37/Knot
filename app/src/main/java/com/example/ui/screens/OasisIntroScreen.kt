package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.SoundEffectManager
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Modern, powerful, cinematic Zen Intro Animation for Oasis
 * Features expanding circadian pulse rings, glowing central emblem,
 * typographic stagger reveals, and ambient rhythm particles.
 */
@Composable
fun OasisIntroScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var animationStage by remember { mutableIntStateOf(0) }

    // Pulsing aura animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringRotation"
    )

    // Entrance Spring Values
    val logoScale = remember { Animatable(0.4f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val subAlpha = remember { Animatable(0f) }
    val progressAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        SoundEffectManager.playPop()
        // Phase 1: Logo zoom & fade
        logoAlpha.animateTo(1f, tween(400))
        logoScale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
        animationStage = 1

        // Phase 2: Brand typography reveal
        delay(200)
        textAlpha.animateTo(1f, tween(500))
        animationStage = 2

        // Phase 3: Tagline & sync bar
        delay(250)
        subAlpha.animateTo(1f, tween(400))
        progressAnim.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
        animationStage = 3

        // Phase 4: Hold momentarily then finish
        delay(700)
        onFinish()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090B10))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                // Tap anywhere to skip instantly
                onFinish()
            },
        contentAlignment = Alignment.Center
    ) {
        // Dynamic Zen Canvas: Concentric Ambient Ripple Waves
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f - 40.dp.toPx())
            val maxRadius = size.width.coerceAtLeast(size.height) * 0.45f

            // Outer subtle rings
            drawCircle(
                color = Color(0xFF00E5FF).copy(alpha = 0.04f * pulseScale),
                radius = maxRadius * 0.9f * pulseScale,
                center = center,
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = Color(0xFF8B5CF6).copy(alpha = 0.06f * pulseScale),
                radius = maxRadius * 0.65f * pulseScale,
                center = center,
                style = Stroke(width = 1.8f)
            )
            drawCircle(
                color = Color(0xFFFF5370).copy(alpha = 0.08f * pulseScale),
                radius = maxRadius * 0.42f * pulseScale,
                center = center,
                style = Stroke(width = 2.0f)
            )
        }

        // Center Content Container
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Central Glowing Emblem Orb
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(130.dp)
                    .scale(logoScale.value * pulseScale)
                    .alpha(logoAlpha.value)
            ) {
                // Multi-layered Ambient Glow
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF00E5FF).copy(alpha = 0.35f),
                                    Color(0xFF8B5CF6).copy(alpha = 0.20f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Outer Ring Border with Glass Specular Effect
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF131722))
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color(0xFF00E5FF),
                                    Color(0xFF8B5CF6),
                                    Color(0xFFFF5370),
                                    Color(0xFF00E5FF)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Modern Center Icon: Spa / Zen Lotus Flame
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "Oasis Zen Emblem",
                        tint = Color.White,
                        modifier = Modifier.size(46.dp)
                    )
                }

                // Orbiting Little Light Sparkle
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E5FF))
                        .align(Alignment.TopEnd)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Typographic Brand: "O A S I S"
            Text(
                text = "O A S I S",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 10.sp,
                color = Color.White,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic Tagline
            Text(
                text = "Zen Rhythm • Daily Mastery • Squad Flow",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(subAlpha.value)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Sleek Loading / Rhythm Sync Bar
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF1E2330))
                    .alpha(subAlpha.value)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressAnim.value)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF00E5FF),
                                    Color(0xFF8B5CF6),
                                    Color(0xFFFF5370)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (progressAnim.value < 0.95f) "Calibrating workspace..." else "Ready to thrive ✨",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted,
                modifier = Modifier.alpha(subAlpha.value)
            )
        }

        // Top Right Subtle Skip Button
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 16.dp, end = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .clickable { onFinish() }
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Skip",
                    fontSize = 12.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Skip",
                    tint = TextMuted,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}
