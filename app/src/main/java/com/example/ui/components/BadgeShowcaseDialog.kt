package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BadgeItem
import com.example.ui.theme.*

@Composable
fun BadgeShowcaseDialog(
    badges: List<BadgeItem>,
    onDismiss: () -> Unit
) {
    val unlockedCount = badges.count { it.unlocked }
    val totalCount = badges.size.coerceAtLeast(6)
    val nextThreshold = if (unlockedCount < 3) 3 else 6
    val remainingForNextPet = (nextThreshold - unlockedCount).coerceAtLeast(0)

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
                        Text(text = "🏆", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Badges & Pet Milestones",
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

                Spacer(modifier = Modifier.height(10.dp))

                // Pet Unlock Milestone Banner (3 / 6 Badges)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(EggYellowLight)
                        .border(1.5.dp, EggAmber, RoundedCornerShape(22.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(EggYellowMid),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎁", fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SPECIAL PET SUMMON",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                color = AmberTextDark
                            )
                            Text(
                                text = "$unlockedCount / $nextThreshold Badges Unlocked",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberTextDeep
                            )
                            Text(
                                text = if (remainingForNextPet == 0) "✨ Pet Ready in Paradise!" else "$remainingForNextPet more badges to summon a rare companion!",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = SlateMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "COLLECTED TROPHIES & MILESTONES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Badges List
                badges.forEach { badge ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (badge.unlocked) ChoicePinkBg else SlateLight.copy(alpha = 0.4f))
                            .border(
                                width = 1.dp,
                                color = if (badge.unlocked) ChoicePinkBorder else Color.Transparent,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (badge.unlocked) CloudWhite else SlateLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (badge.unlocked) badge.iconEmoji else "🔒",
                                    fontSize = 20.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = badge.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (badge.unlocked) SlateText else SlateMuted
                                )
                                Text(
                                    text = badge.description,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SlateMuted
                                )
                            }

                            if (badge.unlocked) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MintGreenPrimary)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "UNLOCKED ✓",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MintGreenDark
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                PoppableButton(
                    text = "Close Gallery ✨",
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
