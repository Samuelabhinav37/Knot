package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.viewmodel.AppSeason
import com.example.viewmodel.TimeOfDayTheme
import kotlin.random.Random

data class Particle(
    val x: Float,
    val initialY: Float,
    val speed: Float,
    val size: Float,
    val alpha: Float,
    val swaySpeed: Float,
    val color: Color
)

@Composable
fun AtmosphereOverlay(
    season: AppSeason,
    timeOfDay: TimeOfDayTheme,
    animationDensity: String,
    modifier: Modifier = Modifier
) {
    if (animationDensity == "LOW") return

    val particleCount = when (season) {
        AppSeason.WINTER -> 24
        AppSeason.SPRING -> 16
        AppSeason.SUMMER -> 14
        AppSeason.AUTUMN -> 18
        AppSeason.FESTIVAL -> 22
    }

    val particles = remember(season, timeOfDay) {
        List(particleCount) {
            val color = when (season) {
                AppSeason.WINTER -> Color.White.copy(alpha = 0.75f)
                AppSeason.SPRING -> Color(0xFFFFB6C1).copy(alpha = 0.65f) // Pink petals
                AppSeason.SUMMER -> Color(0xFFFDE68A).copy(alpha = 0.55f) // Golden sparkles
                AppSeason.AUTUMN -> Color(0xFFF59E0B).copy(alpha = 0.65f) // Amber leaves
                AppSeason.FESTIVAL -> listOf(
                    Color(0xFFFFB6C1), Color(0xFFA7F3D0), Color(0xFFBAE6FD), Color(0xFFFDE68A)
                ).random().copy(alpha = 0.7f)
            }
            Particle(
                x = Random.nextFloat(),
                initialY = Random.nextFloat(),
                speed = 0.15f + Random.nextFloat() * 0.25f,
                size = 3.5f + Random.nextFloat() * 4.5f,
                alpha = 0.4f + Random.nextFloat() * 0.5f,
                swaySpeed = 0.5f + Random.nextFloat() * 1.5f,
                color = color
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "atmosphere")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleProgress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles.forEach { p ->
            val currentY = ((p.initialY + progress * p.speed) % 1f) * height
            val sway = kotlin.math.sin((progress * p.swaySpeed * 2 * Math.PI).toFloat()) * 25f
            val currentX = (p.x * width + sway).coerceIn(0f, width)

            when (season) {
                AppSeason.WINTER -> {
                    // Snowflake
                    drawCircle(
                        color = p.color,
                        radius = p.size.dp.toPx() / 2,
                        center = Offset(currentX, currentY)
                    )
                }
                AppSeason.SPRING -> {
                    // Oval Petal
                    drawOval(
                        color = p.color,
                        topLeft = Offset(currentX - p.size, currentY - p.size / 2),
                        size = androidx.compose.ui.geometry.Size(p.size * 2, p.size)
                    )
                }
                AppSeason.SUMMER -> {
                    // Star Sparkle
                    drawCircle(
                        color = p.color,
                        radius = p.size.dp.toPx() / 2.5f,
                        center = Offset(currentX, currentY)
                    )
                }
                AppSeason.AUTUMN -> {
                    // Maple leaf dot
                    drawOval(
                        color = p.color,
                        topLeft = Offset(currentX - p.size, currentY - p.size),
                        size = androidx.compose.ui.geometry.Size(p.size * 1.8f, p.size * 1.2f)
                    )
                }
                AppSeason.FESTIVAL -> {
                    // Confetti square
                    drawRect(
                        color = p.color,
                        topLeft = Offset(currentX - p.size, currentY - p.size),
                        size = androidx.compose.ui.geometry.Size(p.size * 1.5f, p.size * 1.5f)
                    )
                }
            }
        }
    }
}
