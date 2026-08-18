package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
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

    val unlockedBadges = badges.count { it.unlocked }
    val totalBadges = badges.size.coerceAtLeast(6)
    val nextThreshold = if (unlockedBadges < 3) 3 else 6

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Header
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
                    text = "Pokemon-Go style buddy interactions & care",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
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

        Spacer(modifier = Modifier.height(10.dp))

        // Badge to Pet Milestone Banner (3 / 6 Badges -> Pet Summon)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(22.dp), spotColor = EggAmber.copy(alpha = 0.3f))
                .clip(RoundedCornerShape(22.dp))
                .background(EggYellowLight)
                .border(1.5.dp, EggAmber, RoundedCornerShape(22.dp))
                .clickable { onOpenBadgesGallery() }
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(EggYellowMid),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🏆", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SUMMON PROGRESS: $unlockedBadges / $nextThreshold BADGES",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.1.sp,
                        color = AmberTextDark
                    )
                    Text(
                        text = "Unlock 3 & 6 badges to generate rare paradise companions!",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = SlateMuted
                    )
                }

                Text(
                    text = "View 🏆 →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberTextDeep
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Digital Terrarium Canvas Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .shadow(6.dp, RoundedCornerShape(28.dp), spotColor = MintGreenDark.copy(alpha = 0.25f))
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ChoiceSkyBg,
                            MintGreenLight,
                            Color(0xFFE6FAF1)
                        )
                    )
                )
                .border(2.dp, MintGreenDark, RoundedCornerShape(28.dp))
        ) {
            TerrariumBackdrop()

            // Living Pets Floating in Terrarium
            pets.forEach { pet ->
                val xRatio = pet.xPosRatio.coerceIn(0.1f, 0.85f)
                val yRatio = pet.yPosRatio.coerceIn(0.15f, 0.65f)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = (xRatio * 260).dp,
                            top = (yRatio * 150).dp
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
                                size = 70.dp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CloudWhite.copy(alpha = 0.9f))
                                    .border(1.dp, MintGreenDark, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = pet.name,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MintGreenDark
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pet Sanctuary Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hatched Companions (${pets.size})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = SlateText
            )
            Text(
                text = "Tap to feed & play in Buddy Mode",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MintGreenDark
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            items(pets) { pet ->
                PetArchiveCard(
                    pet = pet,
                    onClick = {
                        buddyPetForPlay = pet
                        SoundEffectManager.playPop()
                    }
                )
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
private fun TerrariumBackdrop() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Floating pastel clouds
        drawCircle(color = Color.White.copy(alpha = 0.85f), radius = 20f, center = Offset(50f, 35f))
        drawCircle(color = Color.White.copy(alpha = 0.85f), radius = 28f, center = Offset(80f, 30f))
        drawCircle(color = Color.White.copy(alpha = 0.85f), radius = 18f, center = Offset(110f, 38f))

        drawCircle(color = Color.White.copy(alpha = 0.75f), radius = 16f, center = Offset(size.width - 80f, 50f))
        drawCircle(color = Color.White.copy(alpha = 0.75f), radius = 24f, center = Offset(size.width - 55f, 45f))

        // Soft Green Hills at bottom
        val hillPath1 = Path().apply {
            moveTo(0f, size.height)
            lineTo(0f, size.height - 60f)
            cubicTo(size.width * 0.3f, size.height - 90f, size.width * 0.7f, size.height - 40f, size.width, size.height - 65f)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(hillPath1, MintGreenPrimary.copy(alpha = 0.7f))

        val hillPath2 = Path().apply {
            moveTo(0f, size.height)
            lineTo(0f, size.height - 35f)
            cubicTo(size.width * 0.4f, size.height - 25f, size.width * 0.6f, size.height - 75f, size.width, size.height - 35f)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(hillPath2, Color(0xFF8CE0BE).copy(alpha = 0.85f))

        // Tiny Sparkling Terrarium Pond
        drawOval(
            color = SkyBlue.copy(alpha = 0.8f),
            topLeft = Offset(size.width * 0.45f, size.height - 28f),
            size = Size(90f, 20f)
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
            .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = MintGreenDark.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(20.dp))
            .background(if (isLegendary) CloudWhite else Color(0xFFF9FBFA))
            .border(
                width = 1.5.dp,
                color = if (isLegendary) EggAmber else SlateLight,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            AnimatedCreature(
                species = pet.species,
                rarity = pet.rarity,
                accessory = pet.accessory,
                size = 60.dp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = pet.name,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = SlateText
            )

            Text(
                text = if (isLegendary) "👑 Legendary" else "🐾 Terrarium Pet",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLegendary) AmberTextDark else SlateMuted
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "❤️ ${pet.happinessLevel}%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ChoicePinkText)
                Text(text = "🍗 ${pet.hungerLevel}%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AmberTextDark)
            }
        }
    }
}
