package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.AppSeason

@Composable
fun OasisHomeScreen(
    meta: OasisMeta?,
    userProfile: UserProfile?,
    currentSeason: AppSeason,
    coreFive: List<ChoreItem>,
    members: List<GroupMember>,
    latestPet: HatchedPet?,
    taskFilter: String,
    onSelectTaskFilter: (String) -> Unit,
    onTapEgg: () -> Unit,
    onCompleteChore: (chore: ChoreItem, clickPos: Offset, eggPos: Offset) -> Unit,
    onOpenPhotoProof: (chore: ChoreItem) -> Unit,
    onOpenTutorial: (tutorialId: String) -> Unit,
    onSwitchMember: (memberName: String) -> Unit,
    onOpenThoughtBubble: () -> Unit,
    onOpenSquadRoom: () -> Unit,
    onOpenBadges: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartNewEgg: () -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onGoToParadise: () -> Unit,
    modifier: Modifier = Modifier
) {
    var eggCenterOffset by remember { mutableStateOf(Offset(500f, 400f)) }

    val eggCracks = meta?.eggCracks ?: 0
    val eggState = meta?.eggState ?: "PULSING"
    val timerSeconds = meta?.timerSecondsRemaining ?: 300
    val timerRunning = meta?.timerRunning ?: false
    val streak = meta?.currentStreak ?: 3

    val activeMember = members.firstOrNull { it.isCurrentActiveUser } ?: members.firstOrNull()

    val filteredChores = remember(coreFive, taskFilter) {
        when (taskFilter) {
            "SOLO" -> coreFive.filter { it.isSoloTask }
            "SQUAD" -> coreFive.filter { !it.isSoloTask }
            else -> coreFive
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Top Bar: App Identity, Season Pill & New Task Button
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .shadow(2.dp, RoundedCornerShape(14.dp))
                                .clip(RoundedCornerShape(14.dp))
                                .background(CloudWhite)
                                .border(1.5.dp, MintGreenDark, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "☁️", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Oasis",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MintGreenDark
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                // Season Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(EggYellowMid)
                                        .border(1.dp, EggAmber, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${currentSeason.iconEmoji} ${currentSeason.displayName}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = AmberTextDark
                                    )
                                }
                            }
                            Text(
                                text = "Daily Life Co-op & Mastery",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateMuted
                            )
                        }
                    }

                    // Top Action Buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PoppableButton(
                            text = "+ Task",
                            onClick = onOpenThoughtBubble,
                            backgroundColor = MintGreenPrimary,
                            bottomBorderColor = MintGreenBorderBottom,
                            contentColor = MintGreenDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Streak & Squad Code Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(28.dp), spotColor = EggYellowMid.copy(alpha = 0.4f))
                        .clip(RoundedCornerShape(28.dp))
                        .background(CloudWhite.copy(alpha = 0.85f))
                        .border(2.dp, CloudWhite, RoundedCornerShape(28.dp))
                        .clickable { onOpenSquadRoom() }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(EggYellowMid),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🏆", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$streak-DAY STREAK",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.1.sp,
                                    color = AmberTextDark
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• Room: ${userProfile?.squadCode ?: "OASIS-7X29"}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MintGreenDark
                                )
                            }
                            Text(
                                text = "${userProfile?.squadName ?: "Pastel Squad"} (${members.size} Friends)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberTextDeep
                            )
                        }

                        // Overlapping Avatars
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((-6).dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            members.take(4).forEach { member ->
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(ChoicePinkBg)
                                        .border(1.5.dp, CloudWhite, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = member.avatarEmoji, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Task Filter Tabs (All / Squad Shared / My Solo)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val filters = listOf(
                        Pair("ALL", "✨ All Routine Tasks"),
                        Pair("SQUAD", "👥 Shared Squad Goals"),
                        Pair("SOLO", "🌱 My Focus Track")
                    )

                    filters.forEach { (tag, label) ->
                        val isSelected = (taskFilter == tag)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MintGreenPrimary else CloudWhite)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MintGreenDark else SlateLight,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectTaskFilter(tag) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) MintGreenDark else SlateMuted
                            )
                        }
                    }
                }
            }
        }

        // 2. Timer & Incubation Stakes Countdown Bar
        item {
            TimerStakesBar(
                timerSeconds = timerSeconds,
                isRunning = timerRunning,
                eggState = eggState,
                onToggle = onToggleTimer,
                onReset = onResetTimer
            )
        }

        // 3. Hero Pulsing Egg / Hatched Companion Showcase
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        val rootPos = coordinates.positionInRoot()
                        eggCenterOffset = Offset(
                            rootPos.x + coordinates.size.width / 2f,
                            rootPos.y + coordinates.size.height / 2f
                        )
                    }
            ) {
                EggPulsingHero(
                    eggCracks = eggCracks,
                    eggState = eggState,
                    latestPet = latestPet,
                    onTapEgg = onTapEgg
                )
            }
        }

        // 4. Action Bar if Egg is Hatched or Sleeping
        if (eggState == "HATCHED" || eggState == "SLEEPING") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PoppableButton(
                        text = "Start New Egg 🐣",
                        onClick = onStartNewEgg,
                        backgroundColor = MintGreenPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (eggState == "HATCHED") {
                        PoppableButton(
                            text = "Visit Paradise 🌸",
                            onClick = onGoToParadise,
                            backgroundColor = BlushPink,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 5. Day-to-Day Task Checklist
        item {
            SparklingScrollHeader(activeCount = filteredChores.count { !it.isCompleted })
        }

        if (filteredChores.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(CloudWhite)
                        .border(1.5.dp, MintGreenLight, RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "✨", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "All Caught Up for Today!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SlateText
                        )
                        Text(
                            text = "Paced steady routine — wait 1 day for next daily unlock or add a custom task above.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SlateMuted
                        )
                    }
                }
            }
        } else {
            items(filteredChores, key = { it.id }) { chore ->
                CoreFiveTaskCard(
                    chore = chore,
                    onComplete = { clickPos ->
                        onCompleteChore(chore, clickPos, eggCenterOffset)
                    },
                    onOpenProof = { onOpenPhotoProof(chore) },
                    onOpenTutorial = { chore.tutorialId?.let { onOpenTutorial(it) } }
                )
            }
        }
    }
}

@Composable
private fun TimerStakesBar(
    timerSeconds: Int,
    isRunning: Boolean,
    eggState: String,
    onToggle: () -> Unit,
    onReset: () -> Unit
) {
    val minutes = timerSeconds / 60
    val seconds = timerSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val isUrgent = (timerSeconds < 60 && eggState == "PULSING")
    val timerBg = if (eggState == "SLEEPING") SlackerGrey.copy(alpha = 0.2f) else if (isUrgent) ChoicePinkBg else ChoiceSkyBg
    val timerBorder = if (eggState == "SLEEPING") SlackerGrey else if (isUrgent) ChoicePinkBorder else ChoiceSkyBorder

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(timerBg)
            .border(1.5.dp, timerBorder.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (eggState == "SLEEPING") "🌧️" else if (isUrgent) "⏳" else "⏱️",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (eggState == "SLEEPING") "Timer Expired (Egg Asleep)" else "Incubation Stakes Countdown",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateMuted
                    )
                    Text(
                        text = timeFormatted,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isUrgent) ChoicePinkText else SlateText
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (eggState == "PULSING") {
                    Box(
                        modifier = Modifier
                            .squishClickable(onClick = onToggle)
                            .clip(CircleShape)
                            .background(CloudWhite)
                            .border(1.dp, ChoiceSkyBorder, CircleShape)
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (isRunning) "⏸ Pause" else "▶ Start",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateText
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Box(
                    modifier = Modifier
                        .squishClickable(onClick = onReset)
                        .clip(CircleShape)
                        .background(CloudWhite)
                        .border(1.dp, SlateMuted.copy(alpha = 0.3f), CircleShape)
                        .padding(6.dp)
                ) {
                    Text(text = "🔄", fontSize = 11.sp, color = SlateText)
                }
            }
        }
    }
}

@Composable
private fun SparklingScrollHeader(activeCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "📜", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Daily Routine Checklist",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = SlateText
                )
                Text(
                    text = "Top priorities & skill exercises",
                    fontSize = 11.sp,
                    color = SlateMuted
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MintGreenPrimary)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = "$activeCount Left",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = MintGreenDark
            )
        }
    }
}

@Composable
private fun CoreFiveTaskCard(
    chore: ChoreItem,
    onComplete: (Offset) -> Unit,
    onOpenProof: () -> Unit,
    onOpenTutorial: () -> Unit,
    modifier: Modifier = Modifier
) {
    var checkButtonCenter by remember { mutableStateOf(Offset.Zero) }
    val isDone = chore.isCompleted

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDone) 1.dp else 3.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = MintGreenDark.copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(if (isDone) Color(0xFFF8FAF9) else CloudWhite)
            .border(
                width = 2.dp,
                color = if (isDone) SlateLight else MintGreenBorderBottom.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poppable Squishy Checkbox
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .onGloballyPositioned { coordinates ->
                        val pos = coordinates.positionInRoot()
                        checkButtonCenter = Offset(
                            pos.x + coordinates.size.width / 2f,
                            pos.y + coordinates.size.height / 2f
                        )
                    }
                    .squishClickable(enabled = !isDone) {
                        onComplete(checkButtonCenter)
                    }
                    .clip(CircleShape)
                    .background(
                        if (isDone) MintGreenPrimary
                        else EggYellowMid
                    )
                    .border(
                        width = 2.dp,
                        color = if (isDone) MintGreenDark else EggAmber,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Done",
                        tint = MintGreenDark,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(text = "✨", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Task Details & Category Icon
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ChoreCategoryBadge(category = chore.iconCategory)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "from ${chore.postedBy}",
                            fontSize = 10.sp,
                            color = SlateMuted
                        )
                    }

                    // Difficulty Tag
                    Text(
                        text = "${chore.difficulty.emoji} +${chore.difficulty.xp} XP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = when (chore.difficulty) {
                            TaskDifficulty.EASY -> MintGreenDark
                            TaskDifficulty.MEDIUM -> AmberTextDark
                            TaskDifficulty.HARD -> ChoicePinkText
                        }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = chore.text,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isDone) SlateMuted else SlateText,
                    textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Action chips: Proof of Work / 1-min Tutorial Guide
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (chore.requiresPhotoProof) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (chore.isVerified) MintGreenPrimary else ChoicePinkBg)
                                .border(
                                    width = 1.dp,
                                    color = if (chore.isVerified) MintGreenDark else ChoicePinkBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onOpenProof() }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (chore.isVerified) "📸 Proof Verified ✓" else "📸 Submit Photo Proof",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = if (chore.isVerified) MintGreenDark else ChoicePinkText
                            )
                        }
                    }

                    if (chore.tutorialId != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(ChoiceSkyBg)
                                .border(1.dp, ChoiceSkyBorder, RoundedCornerShape(10.dp))
                                .clickable { onOpenTutorial() }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "📖 1-Min Guide ▶",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChoiceSkyText
                            )
                        }
                    }
                }
            }
        }
    }
}
