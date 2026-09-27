package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEffectManager
import com.example.ui.theme.*

@Composable
fun OnboardingFlowScreen(
    onCompleteOnboarding: (
        username: String,
        gender: String,
        avatar: String,
        archetype: String,
        interests: List<String>,
        squadCode: String,
        initialGoal: String
    ) -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    var username by remember { mutableStateOf("Alex Morgan") }
    var selectedAvatar by remember { mutableStateOf("⚡") }
    var selectedRole by remember { mutableStateOf("Operations Strategist") }
    var squadCode by remember { mutableStateOf("SQUAD-8X9") }
    var selectedFocusGoal by remember { mutableStateOf("Deep Work & Focus") }

    val avatars = listOf("⚡", "🧘", "🚀", "💡", "🌿", "🎯", "🔥", "✨")
    val roles = listOf(
        "Operations Strategist" to "Synchronize team objectives & backlog",
        "Execution Specialist" to "High-velocity deep work & delivery",
        "Systems Architect" to "Structure resilient workflows & tools",
        "Sprint Manager" to "Lead daily syncs & remove blockers"
    )
    val goals = listOf(
        "Deep Work & Focus",
        "Circadian Daily Rhythm",
        "Squad Team Sync",
        "Mindful Habit Mastery"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090B10))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CorporateSurface)
                .border(1.dp, CorporateCardBorder, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Step Pill Indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0..1) {
                            Box(
                                modifier = Modifier
                                    .width(if (step == i) 26.dp else 10.dp)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (step >= i) CorporateAccentBlue else Color.White.copy(alpha = 0.15f))
                            )
                        }
                    }

                    Text(
                        text = "Step ${step + 1} of 2",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Animated Logo
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(Color(0xFF00E5FF), Color(0xFF8B5CF6), Color(0xFFFF5370), Color(0xFF00E5FF))
                            )
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF131722)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = "Oasis",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (step == 0) "Craft Your Persona" else "Align Your Rhythm",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (step == 0)
                        "Personalize your presence in the Oasis workspace"
                    else
                        "Select your operational focus & squad connection",
                    fontSize = 13.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(22.dp))

                if (step == 0) {
                    // Step 0: Name & Avatar
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Your Name") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CorporateAccentBlue,
                                unfocusedBorderColor = CorporateCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Text(
                            text = "CHOOSE AVATAR BADGE",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextMuted
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            avatars.forEach { avatar ->
                                val isSelected = (selectedAvatar == avatar)
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) CorporateAccentBlueLight else CorporatePrimaryLight)
                                        .border(
                                            1.5.dp,
                                            if (isSelected) CorporateAccentBlue else CorporateCardBorder,
                                            CircleShape
                                        )
                                        .clickable {
                                            SoundEffectManager.playPop()
                                            selectedAvatar = avatar
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = avatar, fontSize = 18.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                SoundEffectManager.playPop()
                                step = 1
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CorporateAccentBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Continue",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    // Step 1: Role & Squad Code
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "OPERATIONAL FOCUS ROLE",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextMuted
                        )

                        roles.forEach { (role, desc) ->
                            val isSelected = (selectedRole == role)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) CorporateAccentBlueLight else CorporateBg)
                                    .border(
                                        1.dp,
                                        if (isSelected) CorporateAccentBlue else CorporateCardBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        SoundEffectManager.playPop()
                                        selectedRole = role
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = role,
                                            fontSize = 13.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) CorporateAccentBlue else Color.White
                                        )
                                        Text(
                                            text = desc,
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = CorporateAccentBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = squadCode,
                            onValueChange = { squadCode = it.uppercase() },
                            label = { Text("Squad Code (optional)") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CorporateAccentBlue,
                                unfocusedBorderColor = CorporateCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    SoundEffectManager.playPop()
                                    step = 0
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Text("Back", fontSize = 13.sp, color = TextSecondary)
                            }

                            Button(
                                onClick = {
                                    SoundEffectManager.playFanfare()
                                    val finalName = if (username.isNotBlank()) username.trim() else "Alex Morgan"
                                    val finalCode = if (squadCode.isNotBlank()) squadCode.trim() else "SQUAD-8X9"
                                    onCompleteOnboarding(
                                        finalName,
                                        "They/Them",
                                        selectedAvatar,
                                        selectedRole,
                                        listOf("Focus", "Zen Flow", "Squad Delivery"),
                                        finalCode,
                                        selectedFocusGoal
                                    )
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CorporateAccentBlue),
                                modifier = Modifier
                                    .weight(2f)
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = "Enter Oasis ✨",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
