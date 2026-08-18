package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundEffectManager
import com.example.data.model.SkillTutorial
import com.example.ui.theme.*

@Composable
fun TutorialDialog(
    tutorial: SkillTutorial,
    onDismiss: () -> Unit,
    onCompleteStep: () -> Unit
) {
    var activeStepIndex by remember { mutableStateOf(0) }
    var isSimulatingVideo by remember { mutableStateOf(false) }
    var simulationProgress by remember { mutableStateOf(0f) }

    // Parse steps from JSON or defaults
    val steps = remember(tutorial) {
        listOf(
            "1. Preparation & Tool Gathering: Clear space and set clean surface.",
            "2. Fundamental Action: Align angles and perform primary motion steadily.",
            "3. Precision Adjustment: Tuck corners, test consistency, or fold edges.",
            "4. Final Polish & Organization: Store neatly in designated bin or finish serve."
        )
    }

    LaunchedEffect(isSimulatingVideo) {
        if (isSimulatingVideo) {
            simulationProgress = 0f
            while (simulationProgress < 1f) {
                kotlinx.coroutines.delay(100)
                simulationProgress += 0.05f
            }
            isSimulatingVideo = false
            SoundEffectManager.playChime()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(CloudWhite)
                .border(2.dp, MintGreenDark, RoundedCornerShape(32.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = tutorial.iconEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1-Min Interactive Guide",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = MintGreenDark
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SlateMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = tutorial.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = SlateText,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Mastery Level ${tutorial.masteryLevel} • Est. ${tutorial.estimatedTimeToMaster}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberTextDark
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Simulated Interactive 1-Min Video Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(listOf(EggYellowLight, MintGreenLight))
                        )
                        .border(2.dp, CloudWhite, RoundedCornerShape(24.dp))
                        .clickable {
                            isSimulatingVideo = true
                            SoundEffectManager.playPop()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSimulatingVideo) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "✨ Interactive Step Simulation...", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MintGreenDark)
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { simulationProgress },
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MintGreenDark,
                                trackColor = CloudWhite
                            )
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MintGreenDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = CloudWhite,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap to Play 1-Min Interactive Demo",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MintGreenDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Step by Step Micro Guide
                Text(
                    text = "ACTION STEPS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                steps.forEachIndexed { index, stepText ->
                    val isDone = index <= activeStepIndex
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDone) ChoiceSkyBg else SlateLight.copy(alpha = 0.4f))
                            .border(
                                width = 1.dp,
                                color = if (isDone) ChoiceSkyBorder else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                activeStepIndex = index
                                SoundEffectManager.playPop()
                            }
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) MintGreenDark else SlateMuted),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CloudWhite
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stepText,
                                fontSize = 11.sp,
                                fontWeight = if (isDone) FontWeight.Bold else FontWeight.Medium,
                                color = if (isDone) ChoiceSkyText else SlateMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Curated Resource Blogs / Websites
                Text(
                    text = "CURATED LEARNING BLOGS & WEBSITES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(CreamBackgroundLight)
                        .border(1.dp, EggAmber.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌐", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Official Guide & Expert Tutorials",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateText
                            )
                        }
                        Text(
                            text = "Visit konmari.com / seriouseats.com / houseplantjournal.com to explore advanced mastery photos & diagrams.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SlateMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                PoppableButton(
                    text = "Complete Step & Level Up Skill ✨",
                    onClick = {
                        SoundEffectManager.playFanfare()
                        onCompleteStep()
                        onDismiss()
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
