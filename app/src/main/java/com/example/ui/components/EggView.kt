package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HatchedPet
import com.example.data.model.PetRarity
import com.example.ui.theme.*

@Composable
fun EggPulsingHero(
    eggCracks: Int, // 0..5
    eggState: String, // "PULSING", "HATCHED", "SLEEPING"
    latestPet: HatchedPet?,
    onTapEgg: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "eggGlow")

    // Pulsing warm glow scale
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    // Sparkle pulse
    val sparkleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkleAlpha"
    )

    // Subtle gentle wobble
    val wobbleAngle by infiniteTransition.animateFloat(
        initialValue = if (eggState == "PULSING") -2.5f else 0f,
        targetValue = if (eggState == "PULSING") 2.5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wobble"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        if (eggState == "HATCHED" && latestPet != null) {
            // Hatched Creature Showcase
            HatchedPetHeroCard(pet = latestPet)
        } else {
            // Egg View (Pulsing or Sleeping)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Raincloud if sleeping / failed
                if (eggState == "SLEEPING") {
                    SleepingRaincloudView()
                }

                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .squishClickable(onClick = onTapEgg),
                    contentAlignment = Alignment.Center
                ) {
                    // Floating decorative sparkles
                    Text(
                        text = "✨",
                        fontSize = 24.sp,
                        color = EggAmber.copy(alpha = sparkleAlpha),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 6.dp, start = 12.dp)
                    )
                    Text(
                        text = "✨",
                        fontSize = 22.sp,
                        color = EggAmber.copy(alpha = sparkleAlpha * 0.7f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 12.dp, end = 12.dp)
                    )

                    // Pulsing Golden Glow Aura behind egg
                    if (eggState == "PULSING") {
                        Box(
                            modifier = Modifier
                                .size(190.dp)
                                .scale(glowScale)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            EggYellowMid.copy(alpha = 0.65f),
                                            EggAmber.copy(alpha = 0.35f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }

                    // Egg Canvas
                    Canvas(
                        modifier = Modifier.size(170.dp)
                    ) {
                        val center = Offset(size.width / 2, size.height / 2 + 6f)
                        val isSleeping = (eggState == "SLEEPING")

                        rotate(if (isSleeping) 0f else wobbleAngle, pivot = center) {
                            drawEggBody(
                                center = center,
                                cracks = eggCracks,
                                isSleeping = isSleeping
                            )
                        }
                    }

                    // Status Pill Badge (Pulsing... or Hatched)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 14.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(CloudWhite.copy(alpha = 0.9f))
                            .border(1.5.dp, CloudWhite, RoundedCornerShape(30.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (eggState == "SLEEPING") "ASLEEP" else if (eggCracks >= 5) "READY TO HATCH! ✨" else "PULSING...",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = if (eggState == "SLEEPING") SlackerSad else AmberTextDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Global Hatching Tracker (5 Dots)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "GLOBAL HATCHING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = AmberTextDark.copy(alpha = 0.75f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..5) {
                            val isFilled = (eggCracks >= i)
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isFilled) MintGreenDark else SlateLight)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawEggBody(
    center: Offset,
    cracks: Int,
    isSleeping: Boolean
) {
    // Soft Shadow
    drawOval(
        color = Color(0x22000000),
        topLeft = Offset(center.x - 52f, center.y + 60f),
        size = Size(104f, 22f)
    )

    // Egg Shape Path
    val eggPath = Path().apply {
        moveTo(center.x, center.y - 70f)
        // Top-right to bottom-right
        cubicTo(center.x + 55f, center.y - 65f, center.x + 58f, center.y + 40f, center.x, center.y + 68f)
        // Bottom-left to top-left
        cubicTo(center.x - 58f, center.y + 40f, center.x - 55f, center.y - 65f, center.x, center.y - 70f)
        close()
    }

    val shellGradient = if (isSleeping) {
        Brush.verticalGradient(
            colors = listOf(Color(0xFFCFD8DC), Color(0xFFB0BEC5), Color(0xFF90A4AE)),
            startY = center.y - 70f,
            endY = center.y + 68f
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(EggYellowLight, EggYellowMid, EggAmber),
            startY = center.y - 70f,
            endY = center.y + 68f
        )
    }

    drawPath(path = eggPath, brush = shellGradient)

    // Shell border (thick artistic white frame)
    drawPath(
        path = eggPath,
        color = if (isSleeping) Color(0xFF78909C) else Color.White,
        style = Stroke(width = 6.dp.toPx())
    )

    // Cute pastel / golden spots
    val spotColor = if (isSleeping) Color(0x40FFFFFF) else Color(0x60FFFFFF)
    drawCircle(color = spotColor, radius = 10f, center = Offset(center.x - 22f, center.y - 20f))
    drawCircle(color = spotColor, radius = 14f, center = Offset(center.x + 22f, center.y + 12f))
    drawCircle(color = spotColor, radius = 8f, center = Offset(center.x - 18f, center.y + 30f))

    if (!isSleeping) {
        // Blush marks on egg
        drawOval(
            color = Color(0x70F59E0B),
            topLeft = Offset(center.x - 38f, center.y + 4f),
            size = Size(18f, 10f)
        )
        drawOval(
            color = Color(0x70F59E0B),
            topLeft = Offset(center.x + 20f, center.y + 4f),
            size = Size(18f, 10f)
        )

        // Cute smiling eyes on egg
        val eyePath = Path().apply {
            moveTo(center.x - 22f, center.y)
            cubicTo(center.x - 18f, center.y - 6f, center.x - 14f, center.y - 6f, center.x - 10f, center.y)
            moveTo(center.x + 10f, center.y)
            cubicTo(center.x + 14f, center.y - 6f, center.x + 18f, center.y - 6f, center.x + 22f, center.y)
        }
        drawPath(eyePath, color = AmberTextDeep, style = Stroke(width = 2.5f))
    }

    // Cracks depending on score (1 to 5)
    if (cracks >= 1) {
        val crack1 = Path().apply {
            moveTo(center.x - 10f, center.y - 45f)
            lineTo(center.x + 4f, center.y - 32f)
            lineTo(center.x - 4f, center.y - 20f)
            lineTo(center.x + 8f, center.y - 8f)
        }
        drawPath(crack1, color = Color.White, style = Stroke(width = 4f))
        drawPath(crack1, color = AmberTextDark, style = Stroke(width = 1.8f))
    }

    if (cracks >= 2) {
        val crack2 = Path().apply {
            moveTo(center.x + 24f, center.y - 30f)
            lineTo(center.x + 36f, center.y - 14f)
            lineTo(center.x + 28f, center.y + 2f)
        }
        drawPath(crack2, color = Color.White, style = Stroke(width = 4f))
        drawPath(crack2, color = AmberTextDark, style = Stroke(width = 1.8f))
    }

    if (cracks >= 3) {
        val crack3 = Path().apply {
            moveTo(center.x - 30f, center.y + 10f)
            lineTo(center.x - 18f, center.y + 24f)
            lineTo(center.x - 26f, center.y + 38f)
        }
        drawPath(crack3, color = Color.White, style = Stroke(width = 4f))
        drawPath(crack3, color = AmberTextDark, style = Stroke(width = 1.8f))
    }

    if (cracks >= 4) {
        val crack4 = Path().apply {
            moveTo(center.x + 4f, center.y + 22f)
            lineTo(center.x + 18f, center.y + 38f)
            lineTo(center.x + 8f, center.y + 52f)
        }
        drawPath(crack4, color = Color.White, style = Stroke(width = 4.5f))
        drawPath(crack4, color = AmberTextDark, style = Stroke(width = 2f))
    }
}

@Composable
private fun SleepingRaincloudView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "🌧️", fontSize = 28.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Z z z . . .",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SlateMuted
            )
        }
        Text(
            text = "Egg went to sleep! Reset timer to try again.",
            fontSize = 12.sp,
            color = SlackerSad,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EggStatusIndicator(eggCracks: Int, eggState: String) {
    val labelText = when {
        eggState == "SLEEPING" -> "Cheerfully Asleep 💤"
        eggCracks == 0 -> "Pulsing Warm Glow (0/5)"
        eggCracks in 1..4 -> "Cracking with Energy! ($eggCracks/5)"
        else -> "Ready to Hatch! (5/5) ✨"
    }

    val glowColor = when (eggCracks) {
        0 -> MintGreenPrimary
        in 1..3 -> SkyBlue
        4 -> BlushPink
        else -> LemonGold
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(glowColor.copy(alpha = 0.25f))
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (eggCracks == 5) "🐣" else if (eggState == "SLEEPING") "💤" else "✨",
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = labelText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SlateText
            )
        }
    }
}

@Composable
fun HatchedPetHeroCard(pet: HatchedPet, modifier: Modifier = Modifier) {
    val isLegendary = (pet.rarity == PetRarity.LEGENDARY)
    val cardBg = if (isLegendary) LemonLight else CloudWhite
    val cardBorder = if (isLegendary) LemonGold else SlackerGrey

    CloudCard(
        backgroundColor = cardBg,
        borderColor = cardBorder,
        elevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Rarity Banner
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isLegendary) LemonGold else SlackerGrey.copy(alpha = 0.3f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isLegendary) "👑 100% TEAM CO-OP LEGENDARY!" else "🐾 PLAYFUL SLACKER REMINDER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isLegendary) SlateText else SlackerSad
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Animated Creature Illustration
            AnimatedCreature(
                species = pet.species,
                rarity = pet.rarity,
                accessory = pet.accessory,
                size = 150.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = pet.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = SlateText
            )

            Text(
                text = pet.species,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isLegendary) BlushPinkDark else SlackerSad
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = pet.personality,
                fontSize = 12.sp,
                color = SlateMuted,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            if (!isLegendary) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tip: Make sure all squad members check off at least 1 task to hatch in full rainbow color!",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = BlushPinkDark
                )
            }
        }
    }
}
