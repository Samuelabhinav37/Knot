package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import kotlin.math.abs

/**
 * Standard spring specs for the Zen workspace, providing a calm, organic,
 * and high-end tactile feel across all item movements and transitions.
 */
object ZenSpringSpecs {
    // Spring physics for list item movements, reordering, additions, and removals
    val ItemPlacement: SpringSpec<IntOffset> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    val ItemFade: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    // Soft spring for height / accordion expansions
    val Expansion: SpringSpec<androidx.compose.ui.unit.IntSize> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    // Float spring for progress bars & sliders
    val Progress: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )
}

/**
 * Adds an elastic, natural spring physics press effect to any clickable surface.
 * Compresses softly to [pressScale] while held, and springs back with an elastic bounce.
 */
fun Modifier.springPress(
    pressScale: Float = 0.96f,
    interactionSource: MutableInteractionSource? = null
): Modifier = composed {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressScale else 1f,
        animationSpec = if (isPressed) {
            spring(stiffness = Spring.StiffnessMediumLow)
        } else {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "springPressScale"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * A smooth, natural spring-decay fling behavior for LazyColumn and LazyRow,
 * delivering a gliding, organic scroll momentum that feels weighted and premium.
 */
@Composable
fun rememberZenSpringFlingBehavior(): FlingBehavior {
    val splineDecay = androidx.compose.animation.rememberSplineBasedDecay<Float>()
    return remember(splineDecay) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                var lastValue = 0f
                var remainingVelocity = initialVelocity

                // Animate fling with spline-based decay softened by spring deceleration
                AnimationState(
                    initialValue = 0f,
                    initialVelocity = initialVelocity
                ).animateDecay(splineDecay) {
                    val delta = value - lastValue
                    val consumed = scrollBy(delta)
                    lastValue = value
                    remainingVelocity = velocity
                    if (abs(delta - consumed) > 0.5f) {
                        cancelAnimation()
                    }
                }
                return remainingVelocity
            }
        }
    }
}
