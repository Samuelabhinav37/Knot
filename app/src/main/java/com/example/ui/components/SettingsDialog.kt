package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
    ) -> Unit
) {
    var username by remember { mutableStateOf(userProfile?.username ?: "Mia") }
    var avatarEmoji by remember { mutableStateOf(userProfile?.avatarEmoji ?: "🌸") }
    var archetype by remember { mutableStateOf(userProfile?.personalityArchetype ?: "The Cozy Cultivator") }
    var season by remember { mutableStateOf(userProfile?.seasonOverride ?: "AUTO") }
    var dayNight by remember { mutableStateOf(userProfile?.dayNightOverride ?: "AUTO") }
    var animationDensity by remember { mutableStateOf(userProfile?.animationDensity ?: "HIGH") }

    val seasonsList = listOf(
        Pair("AUTO", "Auto (Date) 📅"),
        Pair("WINTER", "Winter ❄️"),
        Pair("SPRING", "Spring 🌸"),
        Pair("SUMMER", "Summer ☀️"),
        Pair("AUTUMN", "Autumn 🍂"),
        Pair("FESTIVAL", "Festival 🎉")
    )

    val dayNightList = listOf(
        Pair("AUTO", "Auto (Clock) ⏰"),
        Pair("DAY", "Daylight ☀️"),
        Pair("SUNSET", "Sunset 🌅"),
        Pair("NIGHT", "Night Mode 🌙")
    )

    val archetypesList = listOf(
        "The Cozy Cultivator",
        "The Energetic Organizer",
        "The Culinary Artisan",
        "The Handy Crafter",
        "The Plant Whisperer",
        "The Mindful Minimalist"
    )

    val avatars = listOf("🌸", "🌿", "⚡", "🎀", "🦊", "🐻", "🐼", "🐱", "🦄", "🌟", "🍳", "🛠️")

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(36.dp))
                .background(CloudWhite)
                .border(2.5.dp, MintGreenDark, RoundedCornerShape(36.dp))
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
                        Text(text = "⚙️", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Settings & Atmosphere",
                            fontSize = 15.sp,
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

                Spacer(modifier = Modifier.height(12.dp))

                // Profile Name & Avatar
                Text(
                    text = "PROFILE & IDENTITY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(EggYellowMid),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = avatarEmoji, fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintGreenDark,
                            unfocusedBorderColor = SlateLight
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Avatar choices
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    avatars.take(6).forEach { emoji ->
                        val isSelected = (avatarEmoji == emoji)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MintGreenPrimary else SlateLight.copy(alpha = 0.3f))
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) MintGreenDark else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                avatarEmoji = emoji
                                SoundEffectManager.playPop()
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 16.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Personality Archetype
                Text(
                    text = "PERSONALITY ARCHETYPE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                archetypesList.take(3).forEach { arch ->
                    val isSelected = (archetype == arch)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ChoicePinkBg else CloudWhite)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) ChoicePinkBorder else SlateLight,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                archetype = arch
                                SoundEffectManager.playPop()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = arch,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            color = if (isSelected) ChoicePinkText else SlateText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Season Atmosphere
                Text(
                    text = "SEASON THEME & WEATHER VIBES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    seasonsList.take(3).forEach { (code, label) ->
                        val isSelected = (season == code)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MintGreenPrimary else SlateLight.copy(alpha = 0.4f))
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.dp,
                                    color = if (isSelected) MintGreenDark else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    season = code
                                    SoundEffectManager.playPop()
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) MintGreenDark else SlateText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    seasonsList.drop(3).forEach { (code, label) ->
                        val isSelected = (season == code)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MintGreenPrimary else SlateLight.copy(alpha = 0.4f))
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.dp,
                                    color = if (isSelected) MintGreenDark else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    season = code
                                    SoundEffectManager.playPop()
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) MintGreenDark else SlateText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Day / Sunset / Night Theme
                Text(
                    text = "TIME OF DAY & LIGHTING",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    dayNightList.forEach { (code, label) ->
                        val isSelected = (dayNight == code)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ChoiceSkyBg else SlateLight.copy(alpha = 0.4f))
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.dp,
                                    color = if (isSelected) ChoiceSkyBorder else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    dayNight = code
                                    SoundEffectManager.playPop()
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) ChoiceSkyText else SlateText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Animation Density (Minimal vs Rich)
                Text(
                    text = "UI MINIMALISM & ANIMATION DENSITY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isHigh = (animationDensity == "HIGH")
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isHigh) MintGreenPrimary else SlateLight.copy(alpha = 0.4f))
                            .border(
                                width = if (isHigh) 1.5.dp else 0.dp,
                                color = if (isHigh) MintGreenDark else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                animationDensity = "HIGH"
                                SoundEffectManager.playPop()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✨ High (Full Atmosphere)",
                            fontSize = 10.sp,
                            fontWeight = if (isHigh) FontWeight.Black else FontWeight.Medium,
                            color = if (isHigh) MintGreenDark else SlateMuted
                        )
                    }

                    val isLow = (animationDensity == "LOW")
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isLow) ChoicePinkBg else SlateLight.copy(alpha = 0.4f))
                            .border(
                                width = if (isLow) 1.5.dp else 0.dp,
                                color = if (isLow) ChoicePinkBorder else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                animationDensity = "LOW"
                                SoundEffectManager.playPop()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🌿 Low (Minimal & Battery)",
                            fontSize = 10.sp,
                            fontWeight = if (isLow) FontWeight.Black else FontWeight.Medium,
                            color = if (isLow) ChoicePinkText else SlateMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                PoppableButton(
                    text = "Save Settings ✨",
                    onClick = {
                        onSaveSettings(season, dayNight, animationDensity, archetype, username, avatarEmoji)
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
