package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Provides a tactile "squish-and-stretch" physics animation when tapped,
 * mimicking bubbly Tamagotchi / Fall Guys style UI elements.
 */
fun Modifier.squishClickable(
    enabled: Boolean = true,
    hapticFeedback: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            // Squish: horizontal expansion, vertical compression
            scaleX.animateTo(1.10f, spring(stiffness = Spring.StiffnessMediumLow))
            scaleY.animateTo(0.88f, spring(stiffness = Spring.StiffnessMediumLow))
        } else {
            // Pop back with bouncy overshoot
            scaleX.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )
            scaleY.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )
        }
    }

    this
        .graphicsLayer {
            this.scaleX = scaleX.value
            this.scaleY = scaleY.value
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null, // Custom physics replaces default ripple
            enabled = enabled,
            onClick = onClick
        )
}
