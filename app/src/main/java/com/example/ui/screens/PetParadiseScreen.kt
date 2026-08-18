package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEffectManager
import com.example.data.model.BadgeItem
import com.example.data.model.HatchedPet
import com.example.data.model.PetAccessory
import com.example.data.model.PetRarity
import com.example.ui.components.AnimatedCreature
import com.example.ui.components.PetBuddyPlayDialog
import com.example.ui.components.PoppableButton
import com.example.ui.components.squishClickable
import com.example.ui.theme.*

@Composable
fun PetParadiseScreen(
    pets: List<HatchedPet>,
    badges: List<BadgeItem>,
    currentStreak: Int,
    onEquipAccessory: (petId: Long, accessory: PetAccessory) -> Unit,
    onFeedTreat: (treat: String) -> Unit,
    onPetBuddy: () -> Unit,
    onOpenBadgesGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    var buddyPetForPlay by remember { mutableStateOf<HatchedPet?>(null) }

    val unlockedBadgesCount = badges.count { it.unlocked }
    val totalBadgesCount = badges.size.coerceAtLeast(6)
    val nextThreshold = if (unlockedBadgesCount < 3) 3 else 6

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pet Paradise 🌸",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MintGreenDark
                    )
                    Text(
                        text = "Your achievement badges & hatched companions",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = SlateMuted
                    )
                }

                // Streak Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(EggYellowMid)
                        .border(1.dp, EggAmber, RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🔥 $currentStreak Streak",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberTextDark
                    )
                }
            }
        }

        // Digital Terrarium Canvas Area
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = MintGreenDark.copy(alpha = 0.2f))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                ChoiceSkyBg,
                                MintGreenLight,
                                Color(0xFFE6FAF1)
                            )
                        )
                    )
                    .border(2.dp, MintGreenDark, RoundedCornerShape(24.dp))
            ) {
                TerrariumBackdrop()

                // Living Pets in Terrarium
                pets.forEach { pet ->
                    val xRatio = pet.xPosRatio.coerceIn(0.1f, 0.82f)
                    val yRatio = pet.yPosRatio.coerceIn(0.15f, 0.6f)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = (xRatio * 260).dp,
                                top = (yRatio * 130).dp
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .clickable {
                                    buddyPetForPlay = pet
                                    SoundEffectManager.playPop()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AnimatedCreature(
                                    species = pet.species,
                                    rarity = pet.rarity,
                                    accessory = pet.accessory,
                                    size = 58.dp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CloudWhite.copy(alpha = 0.9f))
                                        .border(1.dp, MintGreenDark, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = pet.name,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MintGreenDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================================
        // 1. BADGES SECTION (FIRST)
        // ==========================================================
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🏆", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Badges & Achievements",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = SlateText
                        )
                    }

                    Text(
                        text = "$unlockedBadgesCount / $totalBadgesCount Unlocked",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberTextDark
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Milestone Progress Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(EggYellowLight)
                        .border(1.dp, EggAmber, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "✨", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SUMMON PROGRESS: $unlockedBadgesCount / $nextThreshold BADGES",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp,
                                color = AmberTextDark
                            )
                            Text(
                                text = if (unlockedBadgesCount < 3) "Unlock 3 badges to summon a rare companion!" else "Unlock 6 badges for the legendary dragon!",
                                fontSize = 10.sp,
                                color = SlateMuted
                            )
                        }
                    }
                }
            }
        }

        // Badges Grid Items
        item {
            val chunkedBadges = badges.chunked(2)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                chunkedBadges.forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEach { badge ->
                            BadgeCard(
                                badge = badge,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // ==========================================================
        // 2. HATCHED COMPANIONS SECTION (SECOND)
        // ==========================================================
        item {
            Column {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🐣", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Hatched Companions (${pets.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = SlateText
                        )
                    }

                    Text(
                        text = "Tap to feed & play",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MintGreenDark
                    )
                }
            }
        }

        // Hatched Pets Grid Items
        item {
            val chunkedPets = pets.chunked(2)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                chunkedPets.forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEach { pet ->
                            PetArchiveCard(
                                pet = pet,
                                onClick = {
                                    buddyPetForPlay = pet
                                    SoundEffectManager.playPop()
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    // Buddy Play Interactive Modal
    buddyPetForPlay?.let { pet ->
        PetBuddyPlayDialog(
            pet = pet,
            onDismiss = { buddyPetForPlay = null },
            onFeedTreat = { treat ->
                onFeedTreat(treat)
            },
            onPetBuddy = onPetBuddy,
            onEquipAccessory = { acc ->
                onEquipAccessory(pet.id, acc)
                buddyPetForPlay = pet.copy(accessory = acc)
            }
        )
    }
}

@Composable
private fun BadgeCard(
    badge: BadgeItem,
    modifier: Modifier = Modifier
) {
    val isUnlocked = badge.unlocked
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isUnlocked) CloudWhite else Color(0xFFF6F8F7))
            .border(
                width = 1.5.dp,
                color = if (isUnlocked) EggAmber else SlateLight,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isUnlocked) EggYellowMid else Color(0xFFE8ECE9)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isUnlocked) {
                        Text(text = badge.iconEmoji, fontSize = 18.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = SlateMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (isUnlocked) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EggYellowLight)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Tier ${badge.badgeTier}",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberTextDeep
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = badge.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) SlateText else SlateMuted
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = badge.description,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                color = SlateMuted,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun TerrariumBackdrop() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Floating pastel clouds
        drawCircle(color = Color.White.copy(alpha = 0.85f), radius = 18f, center = Offset(50f, 30f))
        drawCircle(color = Color.White.copy(alpha = 0.85f), radius = 24f, center = Offset(75f, 26f))
        drawCircle(color = Color.White.copy(alpha = 0.85f), radius = 16f, center = Offset(100f, 32f))

        drawCircle(color = Color.White.copy(alpha = 0.75f), radius = 14f, center = Offset(size.width - 70f, 40f))
        drawCircle(color = Color.White.copy(alpha = 0.75f), radius = 20f, center = Offset(size.width - 48f, 36f))

        // Soft Green Hills at bottom
        val hillPath1 = Path().apply {
            moveTo(0f, size.height)
            lineTo(0f, size.height - 50f)
            cubicTo(size.width * 0.3f, size.height - 75f, size.width * 0.7f, size.height - 35f, size.width, size.height - 55f)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(hillPath1, MintGreenPrimary.copy(alpha = 0.7f))

        val hillPath2 = Path().apply {
            moveTo(0f, size.height)
            lineTo(0f, size.height - 30f)
            cubicTo(size.width * 0.4f, size.height - 20f, size.width * 0.6f, size.height - 60f, size.width, size.height - 30f)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(hillPath2, Color(0xFF8CE0BE).copy(alpha = 0.85f))

        // Tiny Sparkling Terrarium Pond
        drawOval(
            color = SkyBlue.copy(alpha = 0.8f),
            topLeft = Offset(size.width * 0.45f, size.height - 24f),
            size = Size(80f, 18f)
        )
    }
}

@Composable
private fun PetArchiveCard(pet: HatchedPet, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val isLegendary = (pet.rarity == PetRarity.LEGENDARY)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .squishClickable(onClick = onClick)
            .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = MintGreenDark.copy(alpha = 0.15f))
            .clip(RoundedCornerShape(18.dp))
            .background(if (isLegendary) CloudWhite else Color(0xFFF9FBFA))
            .border(
                width = 1.5.dp,
                color = if (isLegendary) EggAmber else SlateLight,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            AnimatedCreature(
                species = pet.species,
                rarity = pet.rarity,
                accessory = pet.accessory,
                size = 54.dp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = pet.name,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = SlateText
            )

            Text(
                text = if (isLegendary) "👑 Legendary" else "🐾 Companion",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLegendary) AmberTextDark else SlateMuted
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "❤️ ${pet.happinessLevel}%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ChoicePinkText)
                Text(text = "🍗 ${pet.hungerLevel}%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AmberTextDark)
            }
        }
    }
}
