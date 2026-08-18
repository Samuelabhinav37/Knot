package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
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
import com.example.data.model.ChoreItem
import com.example.ui.components.ChoreCategoryBadge
import com.example.ui.components.CloudCard
import com.example.ui.components.PoppableButton
import com.example.ui.components.squishClickable
import com.example.ui.theme.*
import com.example.viewmodel.PairwiseMatchup

@Composable
fun PairwiseSortScreen(
    matchup: PairwiseMatchup?,
    totalChoresCount: Int,
    onChooseCard: (chosenA: Boolean) -> Unit,
    onSkipMatchup: () -> Unit,
    onOpenAddChore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Cloud-Card Matchup ☁️",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = SlateText
                )
                Text(
                    text = "Tap the happier or higher priority task!",
                    fontSize = 13.sp,
                    color = SlateMuted
                )
            }

            Box(
                modifier = Modifier
                    .squishClickable(onClick = onSkipMatchup)
                    .clip(CircleShape)
                    .background(LilacLight)
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Shuffle Matchup",
                    tint = LilacDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (matchup == null) {
            // Empty / Need more chores state
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CloudCard(
                    backgroundColor = CloudWhite,
                    borderColor = SkyBlue,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(text = "🫧", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Add More Thought-Bubbles!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SlateText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Need at least 2 active chores in the oasis to run rapid cloud matchups.",
                            fontSize = 12.sp,
                            color = SlateMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PoppableButton(
                            text = "Post a Thought Bubble ✨",
                            onClick = onOpenAddChore,
                            backgroundColor = MintGreenPrimary
                        )
                    }
                }
            }
        } else {
            // Matchup Arena (Artistic Flair Choice A vs Choice B)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "WHICH IS BETTER?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = SlateText.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Task A: Soft Pink Card (#FCE7F3 / border-b-8 #F9A8D4 / text #9D174D)
                BouncyMatchupCard(
                    chore = matchup.choreA,
                    cardTheme = CardTheme.SOFT_PINK,
                    optionLabel = "Choice A",
                    onTap = { onChooseCard(true) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Task B: Sky Blue Card (#E0F2FE / border-b-8 #7DD3FC / text #075985)
                BouncyMatchupCard(
                    chore = matchup.choreB,
                    cardTheme = CardTheme.SKY_BLUE,
                    optionLabel = "Choice B",
                    onTap = { onChooseCard(false) }
                )
            }
        }

        // Bottom Sorting Info Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MintGreenLight)
                .border(1.dp, MintGreenDark.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🎯", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Pairwise sorting powers the synchronized Core Five list",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateText
                )
            }
        }
    }
}

enum class CardTheme {
    SOFT_PINK,
    SKY_BLUE
}

@Composable
private fun BouncyMatchupCard(
    chore: ChoreItem,
    cardTheme: CardTheme,
    optionLabel: String,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (bgColor, bottomBorderColor, textColor, defaultEmoji) = when (cardTheme) {
        CardTheme.SOFT_PINK -> Quad(
            ChoicePinkBg,
            ChoicePinkBorder,
            ChoicePinkText,
            "🧹"
        )
        CardTheme.SKY_BLUE -> Quad(
            ChoiceSkyBg,
            ChoiceSkyBorder,
            ChoiceSkyText,
            "🛠️"
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .squishClickable(onClick = onTap)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(40.dp),
                spotColor = bottomBorderColor.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(40.dp))
            .background(bgColor)
            .border(
                width = 2.dp,
                color = bottomBorderColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(40.dp)
            )
            .padding(vertical = 18.dp, horizontal = 20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            val emoji = when (chore.iconCategory.uppercase()) {
                "CLEANING" -> "🧹"
                "FIXING" -> "🛠️"
                "KITCHEN" -> "🧽"
                "PLANTS" -> "🌿"
                "LAUNDRY" -> "🧺"
                "ORGANIZING" -> "📦"
                "PETS" -> "🐾"
                else -> defaultEmoji
            }

            Text(text = emoji, fontSize = 28.sp)

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = chore.text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Choice A / Choice B Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.6f))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = optionLabel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = textColor
                )
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
