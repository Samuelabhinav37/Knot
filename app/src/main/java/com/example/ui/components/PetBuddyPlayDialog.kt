package com.example.ui.components

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundEffectManager
import com.example.data.model.HatchedPet
import com.example.data.model.PetAccessory
import com.example.ui.theme.*

@Composable
fun PetBuddyPlayDialog(
    pet: HatchedPet,
    onDismiss: () -> Unit,
    onFeedTreat: (treat: String) -> Unit,
    onPetBuddy: () -> Unit,
    onEquipAccessory: (PetAccessory) -> Unit
) {
    var heartSpawnCount by remember { mutableStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "buddy_bounce")
    val bounceScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buddy_scale"
    )

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
                        Text(text = "🌟", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Buddy Mode: ${pet.name}",
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

                Spacer(modifier = Modifier.height(8.dp))

                // Buddy Mascot Interactive Canvas
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(bounceScale)
                        .shadow(8.dp, CircleShape, spotColor = EggAmber.copy(alpha = 0.4f))
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(EggYellowLight, ChoicePinkBg, Color.Transparent)
                            )
                        )
                        .border(3.dp, CloudWhite, CircleShape)
                        .clickable {
                            heartSpawnCount += 1
                            onPetBuddy()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (pet.accessory != PetAccessory.NONE) {
                            Text(text = pet.accessory.iconEmoji, fontSize = 24.sp)
                        }
                        val emoji = when (pet.species) {
                            "Mini Dragon" -> "🐲"
                            "Cosmic Bunny" -> "🐰"
                            "Rainbow Gryphon" -> "🦅"
                            "Sparkle Cat" -> "🐱"
                            "Cloud Sloth" -> "🦥"
                            "Grey Lion" -> "🦁"
                            else -> "🐣"
                        }
                        Text(text = emoji, fontSize = 54.sp)
                    }

                    // Floating Hearts Feedback
                    if (heartSpawnCount > 0) {
                        Text(
                            text = "❤️",
                            fontSize = 28.sp,
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tap to Pet & Caress • ${pet.species}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberTextDark
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Meters (Hunger & Happiness)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Hunger Meter
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(ChoicePinkBg)
                            .border(1.dp, ChoicePinkBorder, RoundedCornerShape(18.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "HUNGER", fontSize = 9.sp, fontWeight = FontWeight.Black, color = ChoicePinkText)
                                Text(text = "${pet.hungerLevel}%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ChoicePinkText)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (pet.hungerLevel / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = ChoicePinkText,
                                trackColor = CloudWhite
                            )
                        }
                    }

                    // Happiness Meter
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(ChoiceSkyBg)
                            .border(1.dp, ChoiceSkyBorder, RoundedCornerShape(18.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "HAPPINESS", fontSize = 9.sp, fontWeight = FontWeight.Black, color = ChoiceSkyText)
                                Text(text = "${pet.happinessLevel}%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ChoiceSkyText)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (pet.happinessLevel / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = ChoiceSkyText,
                                trackColor = CloudWhite
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Feed Treats Row
                Text(
                    text = "FEED DELICIOUS TREATS",
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
                    val treats = listOf(
                        Pair("🍓 Berry", "Berry"),
                        Pair("🥐 Croissant", "Croissant"),
                        Pair("⭐ Star Candy", "Star Candy")
                    )

                    treats.forEach { (label, name) ->
                        Button(
                            onClick = {
                                SoundEffectManager.playPop()
                                onFeedTreat(name)
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MintGreenPrimary),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .border(1.dp, MintGreenBorderBottom, RoundedCornerShape(16.dp))
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MintGreenDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Accessories Closet
                Text(
                    text = "DRESS ACCESSORY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PetAccessory.values().take(5).forEach { acc ->
                        val isEquipped = (pet.accessory == acc)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isEquipped) EggYellowMid else CloudWhite)
                                .border(
                                    width = if (isEquipped) 2.dp else 1.dp,
                                    color = if (isEquipped) EggAmber else SlateLight,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onEquipAccessory(acc) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = acc.iconEmoji, fontSize = 16.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                PoppableButton(
                    text = "Done Playing ✨",
                    onClick = onDismiss,
                    backgroundColor = MintGreenPrimary,
                    bottomBorderColor = MintGreenBorderBottom,
                    contentColor = MintGreenDark,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
