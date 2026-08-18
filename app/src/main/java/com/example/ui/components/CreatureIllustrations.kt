package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PetAccessory
import com.example.data.model.PetRarity
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedCreature(
    species: String,
    rarity: PetRarity,
    accessory: PetAccessory = PetAccessory.NONE,
    size: Dp = 140.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "creatureAnim")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    val wingFlap by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wing"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            val center = Offset(size.toPx() / 2, size.toPx() / 2 + bounceOffset)
            val isDowngrade = (rarity == PetRarity.SLACKER_DOWNGRADE)

            when {
                species.contains("Dragon", ignoreCase = true) -> {
                    drawMiniDragon(center, breathScale, wingFlap, isDowngrade)
                }
                species.contains("Bunny", ignoreCase = true) -> {
                    drawCosmicBunny(center, breathScale, isDowngrade)
                }
                species.contains("Sloth", ignoreCase = true) -> {
                    drawCloudSloth(center, breathScale)
                }
                species.contains("Lion", ignoreCase = true) -> {
                    drawGreyLion(center, breathScale)
                }
                else -> {
                    drawMiniDragon(center, breathScale, wingFlap, isDowngrade)
                }
            }

            // Draw Equipped Accessory
            drawPetAccessory(center, accessory)
        }
    }
}

private fun DrawScope.drawMiniDragon(
    center: Offset,
    breath: Float,
    wingAngle: Float,
    isDowngrade: Boolean
) {
    val bodyColor = if (isDowngrade) Color(0xFFB0BEC5) else Color(0xFFA8E6CF)
    val bellyColor = if (isDowngrade) Color(0xFFECEFF1) else Color(0xFFFFF9A6)
    val wingColor = if (isDowngrade) Color(0xFF90A4AE) else Color(0xFFFFAAA5)
    val hornColor = if (isDowngrade) Color(0xFFCFD8DC) else Color(0xFFFFD166)

    // Shadow
    drawOval(
        color = Color(0x22000000),
        topLeft = Offset(center.x - 45f, center.y + 42f),
        size = Size(90f, 18f)
    )

    // Left Wing
    rotate(wingAngle, pivot = Offset(center.x - 30f, center.y - 10f)) {
        val wingPath = Path().apply {
            moveTo(center.x - 30f, center.y - 5f)
            cubicTo(center.x - 75f, center.y - 45f, center.x - 65f, center.y + 15f, center.x - 30f, center.y + 10f)
            close()
        }
        drawPath(wingPath, wingColor)
    }

    // Right Wing
    rotate(-wingAngle, pivot = Offset(center.x + 30f, center.y - 10f)) {
        val wingPath = Path().apply {
            moveTo(center.x + 30f, center.y - 5f)
            cubicTo(center.x + 75f, center.y - 45f, center.x + 65f, center.y + 15f, center.x + 30f, center.y + 10f)
            close()
        }
        drawPath(wingPath, wingColor)
    }

    // Dragon Body
    val bodyRadius = 38f * breath
    drawCircle(color = bodyColor, radius = bodyRadius, center = center)

    // Soft Belly
    drawOval(
        color = bellyColor,
        topLeft = Offset(center.x - 22f, center.y - 10f),
        size = Size(44f, 42f)
    )

    // Cute Horns
    drawCircle(color = hornColor, radius = 8f, center = Offset(center.x - 20f, center.y - 34f))
    drawCircle(color = hornColor, radius = 8f, center = Offset(center.x + 20f, center.y - 34f))

    // Eyes
    if (isDowngrade) {
        // Droopy sad eyes
        drawCircle(color = Color(0xFF37474F), radius = 5f, center = Offset(center.x - 14f, center.y - 8f))
        drawCircle(color = Color(0xFF37474F), radius = 5f, center = Offset(center.x + 14f, center.y - 8f))
        // Tear drop
        drawCircle(color = Color(0xFF64B5F6), radius = 3f, center = Offset(center.x + 18f, center.y + 2f))
    } else {
        // Big sparkling kawaii eyes
        drawCircle(color = Color(0xFF263238), radius = 6.5f, center = Offset(center.x - 14f, center.y - 8f))
        drawCircle(color = Color(0xFF263238), radius = 6.5f, center = Offset(center.x + 14f, center.y - 8f))
        // Highlights
        drawCircle(color = Color.White, radius = 2.5f, center = Offset(center.x - 16f, center.y - 10f))
        drawCircle(color = Color.White, radius = 2.5f, center = Offset(center.x + 12f, center.y - 10f))
        // Blush marks
        drawCircle(color = Color(0x80FF8095), radius = 6f, center = Offset(center.x - 22f, center.y + 4f))
        drawCircle(color = Color(0x80FF8095), radius = 6f, center = Offset(center.x + 22f, center.y + 4f))
    }

    // Smiling / Sad mouth
    val mouthPath = Path().apply {
        if (isDowngrade) {
            moveTo(center.x - 8f, center.y + 8f)
            cubicTo(center.x - 4f, center.y + 4f, center.x + 4f, center.y + 4f, center.x + 8f, center.y + 8f)
        } else {
            moveTo(center.x - 8f, center.y + 4f)
            cubicTo(center.x - 4f, center.y + 11f, center.x + 4f, center.y + 11f, center.x + 8f, center.y + 4f)
        }
    }
    drawPath(mouthPath, Color(0xFF37474F), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f))
}

private fun DrawScope.drawCosmicBunny(
    center: Offset,
    breath: Float,
    isDowngrade: Boolean
) {
    val bodyColor = if (isDowngrade) Color(0xFFCFD8DC) else Color(0xFFFFB6C1)
    val earInner = if (isDowngrade) Color(0xFFECEFF1) else Color(0xFFFFF9A6)

    // Shadow
    drawOval(
        color = Color(0x22000000),
        topLeft = Offset(center.x - 40f, center.y + 40f),
        size = Size(80f, 16f)
    )

    // Long Cute Ears
    drawOval(color = bodyColor, topLeft = Offset(center.x - 26f, center.y - 65f), size = Size(18f, 45f))
    drawOval(color = earInner, topLeft = Offset(center.x - 23f, center.y - 58f), size = Size(12f, 32f))

    drawOval(color = bodyColor, topLeft = Offset(center.x + 8f, center.y - 65f), size = Size(18f, 45f))
    drawOval(color = earInner, topLeft = Offset(center.x + 11f, center.y - 58f), size = Size(12f, 32f))

    // Bunny Body
    val bodyRadius = 36f * breath
    drawCircle(color = bodyColor, radius = bodyRadius, center = center)

    // Eyes
    drawCircle(color = Color(0xFF263238), radius = 6f, center = Offset(center.x - 12f, center.y - 6f))
    drawCircle(color = Color(0xFF263238), radius = 6f, center = Offset(center.x + 12f, center.y - 6f))
    drawCircle(color = Color.White, radius = 2f, center = Offset(center.x - 14f, center.y - 8f))
    drawCircle(color = Color.White, radius = 2f, center = Offset(center.x + 10f, center.y - 8f))

    // Tiny pink nose
    drawCircle(color = Color(0xFFFF758F), radius = 3.5f, center = Offset(center.x, center.y + 2f))
    // Blush
    drawCircle(color = Color(0x80FFAAA5), radius = 7f, center = Offset(center.x - 22f, center.y + 4f))
    drawCircle(color = Color(0x80FFAAA5), radius = 7f, center = Offset(center.x + 22f, center.y + 4f))
}

private fun DrawScope.drawCloudSloth(center: Offset, breath: Float) {
    val bodyColor = Color(0xFFB0BEC5)
    val faceColor = Color(0xFFECEFF1)

    // Sloth Body
    drawCircle(color = bodyColor, radius = 36f * breath, center = center)
    // Fluffy face mask
    drawOval(color = faceColor, topLeft = Offset(center.x - 24f, center.y - 18f), size = Size(48f, 36f))

    // Eye patches
    drawOval(color = Color(0xFF78909C), topLeft = Offset(center.x - 20f, center.y - 8f), size = Size(14f, 10f))
    drawOval(color = Color(0xFF78909C), topLeft = Offset(center.x + 6f, center.y - 8f), size = Size(14f, 10f))

    // Sleepy closed eye lines
    val leftEye = Path().apply {
        moveTo(center.x - 18f, center.y - 4f)
        cubicTo(center.x - 14f, center.y - 1f, center.x - 10f, center.y - 1f, center.x - 6f, center.y - 4f)
    }
    val rightEye = Path().apply {
        moveTo(center.x + 6f, center.y - 4f)
        cubicTo(center.x + 10f, center.y - 1f, center.x + 14f, center.y - 1f, center.x + 18f, center.y - 4f)
    }
    drawPath(leftEye, Color(0xFF263238), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f))
    drawPath(rightEye, Color(0xFF263238), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f))

    // Cute snoozy nose
    drawCircle(color = Color(0xFF37474F), radius = 4f, center = Offset(center.x, center.y + 4f))
}

private fun DrawScope.drawGreyLion(center: Offset, breath: Float) {
    val maneColor = Color(0xFF90A4AE)
    val faceColor = Color(0xFFCFD8DC)

    // Mane
    for (i in 0 until 8) {
        val angle = i * (Math.PI / 4.0)
        val mx = center.x + (cos(angle) * 32f).toFloat()
        val my = center.y + (sin(angle) * 32f).toFloat()
        drawCircle(color = maneColor, radius = 16f, center = Offset(mx, my))
    }

    // Head
    drawCircle(color = faceColor, radius = 28f * breath, center = center)

    // Gentle sad eyes
    drawCircle(color = Color(0xFF263238), radius = 5f, center = Offset(center.x - 10f, center.y - 4f))
    drawCircle(color = Color(0xFF263238), radius = 5f, center = Offset(center.x + 10f, center.y - 4f))
    drawCircle(color = Color(0xFF64B5F6), radius = 2.5f, center = Offset(center.x + 13f, center.y + 4f))

    // Nose & gentle mouth
    drawCircle(color = Color(0xFF546E7A), radius = 3.5f, center = Offset(center.x, center.y + 4f))
}

private fun DrawScope.drawPetAccessory(center: Offset, accessory: PetAccessory) {
    when (accessory) {
        PetAccessory.FLOWER_CROWN -> {
            val flowerColors = listOf(Color(0xFFFFB6C1), Color(0xFFFFF9A6), Color(0xFFA0E7E5))
            for (i in 0..3) {
                val fx = center.x - 22f + (i * 14f)
                val fy = center.y - 38f
                drawCircle(color = flowerColors[i % 3], radius = 6f, center = Offset(fx, fy))
                drawCircle(color = Color.White, radius = 2.5f, center = Offset(fx, fy))
            }
        }
        PetAccessory.STAR_GLASSES -> {
            // Left star lens
            drawCircle(color = Color(0xFFFFD166), radius = 10f, center = Offset(center.x - 14f, center.y - 8f))
            drawCircle(color = Color(0xFF263238), radius = 7f, center = Offset(center.x - 14f, center.y - 8f))
            // Right star lens
            drawCircle(color = Color(0xFFFFD166), radius = 10f, center = Offset(center.x + 14f, center.y - 8f))
            drawCircle(color = Color(0xFF263238), radius = 7f, center = Offset(center.x + 14f, center.y - 8f))
            // Bridge
            drawLine(
                color = Color(0xFFFFD166),
                start = Offset(center.x - 6f, center.y - 8f),
                end = Offset(center.x + 6f, center.y - 8f),
                strokeWidth = 3f
            )
        }
        PetAccessory.WIZARD_HAT -> {
            val hatPath = Path().apply {
                moveTo(center.x, center.y - 65f)
                lineTo(center.x - 22f, center.y - 36f)
                lineTo(center.x + 22f, center.y - 36f)
                close()
            }
            drawPath(hatPath, Color(0xFF9D65E8))
            drawOval(color = Color(0xFF7E38DE), topLeft = Offset(center.x - 28f, center.y - 39f), size = Size(56f, 10f))
            drawCircle(color = Color(0xFFFFD166), radius = 4f, center = Offset(center.x, center.y - 65f))
        }
        PetAccessory.COZY_SCARF -> {
            drawOval(color = Color(0xFFFFAAA5), topLeft = Offset(center.x - 26f, center.y + 22f), size = Size(52f, 16f))
            // Scarf tail
            drawRect(color = Color(0xFFFFAAA5), topLeft = Offset(center.x + 10f, center.y + 26f), size = Size(12f, 22f))
        }
        PetAccessory.SPARKLE_AURA, PetAccessory.GOLDEN_HALO -> {
            drawOval(
                color = Color(0xFFFFD166),
                topLeft = Offset(center.x - 24f, center.y - 48f),
                size = Size(48f, 14f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.5f)
            )
        }
        PetAccessory.NONE -> {}
    }
}
