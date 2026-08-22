package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundEffectManager
import com.example.ui.theme.*

data class TourStep(
    val title: String,
    val description: String,
    val iconEmoji: String,
    val color: Color
)

@Composable
fun UITourDialog(
    onDismiss: () -> Unit
) {
    var currentStepIndex by remember { mutableStateOf(0) }

    val tourSteps = listOf(
        TourStep(
            title = "1. The Knot Egg & Streak Hub",
            description = "Check off daily life tasks with your friends. Every task charges the incubation crystal and cracks the egg closer to hatching!",
            iconEmoji = "🐣",
            color = EggYellowLight
        ),
        TourStep(
            title = "2. Pairwise Matchup Arena",
            description = "Vote on tasks in fun head-to-head comparisons to determine which chores the squad should prioritize first.",
            iconEmoji = "⚖️",
            color = ChoicePinkBg
        ),
        TourStep(
            title = "3. 1-Min Interactive Skill Guides",
            description = "Learn useful routines like Japanese KonMari folding and 15-minute healthy meal prep with quick interactive steps and blogs.",
            iconEmoji = "📖",
            color = ChoiceSkyBg
        ),
        TourStep(
            title = "4. Pet Buddy Paradise",
            description = "Collect badges to summon rare companions! Feed, pet, and play with your active buddy in Pokemon-Go style.",
            iconEmoji = "🌸",
            color = MintGreenLight
        )
    )

    val currentStep = tourSteps[currentStepIndex]

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(36.dp))
                .background(CloudWhite)
                .border(2.5.dp, MintGreenDark, RoundedCornerShape(36.dp))
                .padding(22.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUICK TOUR (${currentStepIndex + 1}/4)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        color = MintGreenDark
                    )

                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Skip Tour",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hero Illustration Box
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(currentStep.color)
                        .border(2.dp, CloudWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = currentStep.iconEmoji, fontSize = 44.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = currentStep.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = SlateText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = currentStep.description,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SlateMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Step dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tourSteps.indices.forEach { index ->
                        val isSelected = (index == currentStepIndex)
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 10.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MintGreenDark else SlateLight)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Next or Finish Button
                PoppableButton(
                    text = if (currentStepIndex < tourSteps.size - 1) "Next Step →" else "Start Exploring ✨",
                    onClick = {
                        if (currentStepIndex < tourSteps.size - 1) {
                            currentStepIndex += 1
                            SoundEffectManager.playPop()
                        } else {
                            onDismiss()
                        }
                    },
                    backgroundColor = MintGreenPrimary,
                    bottomBorderColor = MintGreenBorderBottom,
                    contentColor = MintGreenDark,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
