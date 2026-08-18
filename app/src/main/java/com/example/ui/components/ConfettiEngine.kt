package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class ConfettiParticle(
    val startX: Float,
    val startY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val color: Color,
    val size: Float,
    val rotationSpeed: Float,
    val isCircle: Boolean
)

@Composable
fun ConfettiBurst(
    trigger: Int,
    originXRatio: Float = 0.5f,
    originYRatio: Float = 0.5f,
    particleCount: Int = 40,
    modifier: Modifier = Modifier
) {
    if (trigger <= 0) return

    val animProgress = remember(trigger) { Animatable(0f) }
    val particles = remember(trigger) {
        val colors = listOf(
            Color(0xFFA8E6CF), // Mint Green
            Color(0xFFFFAAA5), // Blush Pink
            Color(0xFFA0E7E5), // Sky Blue
            Color(0xFFFFF9A6), // Lemon Chiffon
            Color(0xFFE8D7FF), // Lilac
            Color(0xFFFFD166)  // Gold
        )
        List(particleCount) {
            val angle = Random.nextDouble(0.0, Math.PI * 2.0)
            val speed = Random.nextDouble(200.0, 750.0).toFloat()
            ConfettiParticle(
                startX = 0f,
                startY = 0f,
                velocityX = (cos(angle) * speed).toFloat(),
                velocityY = (sin(angle) * speed).toFloat() - 150f, // bias upward
                color = colors.random(),
                size = Random.nextFloat() * 14f + 8f,
                rotationSpeed = Random.nextFloat() * 720f - 360f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    LaunchedEffect(trigger) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1100, easing = LinearEasing)
        )
    }

    if (animProgress.value < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val t = animProgress.value
            val origin = Offset(size.width * originXRatio, size.height * originYRatio)
            val gravity = 900f * (t * t)

            particles.forEach { p ->
                val curX = origin.x + p.velocityX * t
                val curY = origin.y + p.velocityY * t + gravity
                val alpha = (1f - t).coerceIn(0f, 1f)

                rotate(degrees = p.rotationSpeed * t, pivot = Offset(curX, curY)) {
                    if (p.isCircle) {
                        drawCircle(
                            color = p.color.copy(alpha = alpha),
                            radius = p.size / 2,
                            center = Offset(curX, curY)
                        )
                    } else {
                        drawRect(
                            color = p.color.copy(alpha = alpha),
                            topLeft = Offset(curX - p.size / 2, curY - p.size / 3),
                            size = Size(p.size, p.size * 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnergySparkEffect(
    trigger: Int,
    startOffset: Offset,
    targetOffset: Offset,
    onReachedTarget: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (trigger <= 0) return

    val progress = remember(trigger) { Animatable(0f) }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 550, easing = LinearEasing)
        )
        onReachedTarget()
    }

    if (progress.value in 0.01f..0.99f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val t = progress.value
            // Quadratic Bezier arc
            val controlPoint = Offset(
                x = (startOffset.x + targetOffset.x) / 2 - 120f,
                y = (startOffset.y + targetOffset.y) / 2 - 200f
            )

            val u = 1 - t
            val currentX = u * u * startOffset.x + 2 * u * t * controlPoint.x + t * t * targetOffset.x
            val currentY = u * u * startOffset.y + 2 * u * t * controlPoint.y + t * t * targetOffset.y
            val currentPos = Offset(currentX, currentY)

            // Spark glow aura
            drawCircle(
                color = Color(0xFFFFD166).copy(alpha = 0.4f),
                radius = 28f,
                center = currentPos
            )
            drawCircle(
                color = Color(0xFFFFAAA5).copy(alpha = 0.8f),
                radius = 16f,
                center = currentPos
            )
            drawCircle(
                color = Color.White,
                radius = 8f,
                center = currentPos
            )

            // Sparkle trailing dots
            for (i in 1..4) {
                val trailT = (t - i * 0.04f).coerceAtLeast(0f)
                val tu = 1 - trailT
                val trX = tu * tu * startOffset.x + 2 * tu * trailT * controlPoint.x + trailT * trailT * targetOffset.x
                val trY = tu * tu * startOffset.y + 2 * tu * trailT * controlPoint.y + trailT * trailT * targetOffset.y
                drawCircle(
                    color = Color(0xFFFFF9A6).copy(alpha = (0.8f - i * 0.18f).coerceAtLeast(0f)),
                    radius = (8f - i * 1.5f).coerceAtLeast(2f),
                    center = Offset(trX, trY)
                )
            }
        }
    }
}
