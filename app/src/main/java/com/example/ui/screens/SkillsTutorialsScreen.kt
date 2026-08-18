package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
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
import com.example.audio.SoundEffectManager
import com.example.data.model.SkillTutorial
import com.example.data.model.UserProfile
import com.example.ui.components.PoppableButton
import com.example.ui.theme.*

@Composable
fun SkillsTutorialsScreen(
    tutorials: List<SkillTutorial>,
    userProfile: UserProfile?,
    onOpenTutorial: (SkillTutorial) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📖", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Life Mastery Paths",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MintGreenDark
                            )
                        }
                        Text(
                            text = "1-Min interactive guides & master pathways",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SlateMuted
                        )
                    }

                    // Level Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(EggYellowMid)
                            .border(1.5.dp, EggAmber, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Lvl ${userProfile?.level ?: 1} Artisan",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberTextDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // XP Progress Bar
                val currentXp = userProfile?.currentXp ?: 240
                val nextXp = userProfile?.nextLevelXp ?: 500
                val progress = (currentXp.toFloat() / nextXp.toFloat()).coerceIn(0f, 1f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CloudWhite)
                        .border(1.dp, MintGreenLight, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "SKILL MASTERY PROGRESS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                color = MintGreenDark
                            )
                            Text(
                                text = "$currentXp / $nextXp XP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateMuted
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MintGreenDark,
                            trackColor = SlateLight
                        )
                    }
                }
            }
        }

        // Active Focus Goal Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(28.dp), spotColor = EggAmber.copy(alpha = 0.35f))
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(listOf(EggYellowLight, ChoicePinkBg))
                    )
                    .border(2.dp, CloudWhite, RoundedCornerShape(28.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(CloudWhite),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌟", fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CURRENT FOCUS TRACK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = AmberTextDark
                        )
                        Text(
                            text = "Daily Habit Cadence: 1 Task / Day",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberTextDeep
                        )
                        Text(
                            text = "Paced steady learning with 1-day lockouts to prevent habit burnout.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SlateMuted
                        )
                    }
                }
            }
        }

        // List of Skill Tutorials
        items(tutorials) { tutorial ->
            SkillTutorialCard(
                tutorial = tutorial,
                onOpen = { onOpenTutorial(tutorial) }
            )
        }
    }
}

@Composable
private fun SkillTutorialCard(
    tutorial: SkillTutorial,
    onOpen: () -> Unit
) {
    val levelLabel = when (tutorial.masteryLevel) {
        1 -> "Beginner"
        2 -> "Apprentice"
        3 -> "Adept"
        else -> "Master"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(28.dp), spotColor = MintGreenDark.copy(alpha = 0.25f))
            .clip(RoundedCornerShape(28.dp))
            .background(CloudWhite)
            .border(2.dp, MintGreenBorderBottom.copy(alpha = 0.4f), RoundedCornerShape(28.dp))
            .clickable { onOpen() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MintGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = tutorial.iconEmoji, fontSize = 26.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = tutorial.category.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.1.sp,
                            color = MintGreenDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${tutorial.estimatedTimeToMaster}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateMuted
                        )
                    }

                    Text(
                        text = tutorial.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = SlateText
                    )
                }

                // Mastery Stars
                Row {
                    for (i in 1..4) {
                        val isFilled = (tutorial.masteryLevel >= i)
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isFilled) EggAmber else SlateLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tutorial.subtitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = SlateMuted,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tier Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ChoiceSkyBg)
                        .border(1.dp, ChoiceSkyBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Tier: $levelLabel",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChoiceSkyText
                    )
                }

                // Launch 1-min Interactive Guide Button
                PoppableButton(
                    text = "1-Min Guide ▶",
                    onClick = onOpen,
                    backgroundColor = MintGreenPrimary,
                    bottomBorderColor = MintGreenBorderBottom,
                    contentColor = MintGreenDark
                )
            }
        }
    }
}
