package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundEffectManager
import com.example.data.model.UserProfile
import com.example.ui.theme.*

@Composable
fun SettingsDialog(
    userProfile: UserProfile?,
    onDismiss: () -> Unit,
    onSaveSettings: (
        season: String,
        dayNight: String,
        animationDensity: String,
        archetype: String,
        username: String,
        avatarEmoji: String
    ) -> Unit,
    onReplayIntro: (() -> Unit)? = null
) {
    var username by remember { mutableStateOf(userProfile?.username ?: "Alex Morgan") }
    var archetype by remember { mutableStateOf(userProfile?.personalityArchetype ?: "Operations Strategist") }

    val roles = listOf(
        "Operations Strategist",
        "Execution Specialist",
        "Systems Architect",
        "Quality Reviewer",
        "Sprint Manager"
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CorporateSurface)
                .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
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
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CorporateAccentBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = CorporateAccentBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Settings",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Manage profile and workspace preferences",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

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

                // Name field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CorporatePrimary,
                        unfocusedBorderColor = CorporateCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Role
                Text(
                    text = "ROLE ARCHETYPE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp,
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    roles.forEach { role ->
                        val isSelected = (archetype == role)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CorporateAccentBlueLight else CorporateBg)
                                .border(
                                    1.dp,
                                    if (isSelected) CorporateAccentBlue else CorporateCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    archetype = role
                                    SoundEffectManager.playPop()
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = role,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) CorporateAccentBlue else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val cleanName = if (username.isNotBlank()) username.trim() else "Alex Morgan"
                        onSaveSettings("AUTO", "AUTO", "LOW", archetype, cleanName, "👤")
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CorporatePrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Save Preferences",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                if (onReplayIntro != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            SoundEffectManager.playPop()
                            onDismiss()
                            onReplayIntro()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Replay Intro Animation ✨",
                            fontSize = 13.sp,
                            color = CorporateAccentBlue
                        )
                    }
                }
            }
        }
    }
}
