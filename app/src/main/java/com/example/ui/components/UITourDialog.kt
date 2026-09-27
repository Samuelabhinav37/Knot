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
    val domain: String
)

@Composable
fun UITourDialog(
    onDismiss: () -> Unit
) {
    var currentStepIndex by remember { mutableStateOf(0) }

    val tourSteps = listOf(
        TourStep(
            title = "1. Today Rhythm",
            description = "Daily inspiration, squad status, daily routine checks, and squad goals.",
            domain = "Today"
        ),
        TourStep(
            title = "2. Squad Hub",
            description = "Connect with teammates via squad code or complete the 6-question workflow alignment quiz.",
            domain = "Squad"
        ),
        TourStep(
            title = "3. Zen",
            description = "Mindful 4-4-4 breathing resets, deep focus intervals, and operating procedures.",
            domain = "Zen"
        ),
        TourStep(
            title = "4. Profile & Settings",
            description = "Access profile information, team metrics, and settings via the top-right button.",
            domain = "Profile"
        )
    )

    val step = tourSteps[currentStepIndex]

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CorporateSurface)
                .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Workspace Walkthrough",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CorporateBg)
                        .border(1.dp, CorporateCardBorder, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CorporateAccentBlueLight)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = step.domain,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CorporateAccentBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = step.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = step.description,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dots indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tourSteps.indices.forEach { idx ->
                        Box(
                            modifier = Modifier
                                .size(if (idx == currentStepIndex) 8.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (idx == currentStepIndex) CorporatePrimary else CorporateCardBorder)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (currentStepIndex > 0) {
                        OutlinedButton(
                            onClick = {
                                SoundEffectManager.playPop()
                                currentStepIndex--
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Previous", fontSize = 12.sp, color = TextPrimary)
                        }
                    }

                    Button(
                        onClick = {
                            SoundEffectManager.playPop()
                            if (currentStepIndex < tourSteps.size - 1) {
                                currentStepIndex++
                            } else {
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CorporatePrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (currentStepIndex < tourSteps.size - 1) "Next" else "Got It",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
