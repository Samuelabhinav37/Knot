package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEffectManager
import com.example.data.model.*
import com.example.ui.components.squishClickable
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
    val userName = userProfile?.username ?: "Alex"
    val squadName = userProfile?.squadName ?: "Cadre Unit Alpha"
    val squadCode = userProfile?.squadCode ?: "CADRE-8X9"
    val streak = meta?.currentStreak ?: 5

    // Display only two daily routine checks as requested
    val routineChecks = remember(coreFive) {
        if (coreFive.size >= 2) {
            coreFive.take(2)
        } else if (coreFive.size == 1) {
            coreFive + listOf(
                ChoreItem(
                    id = 9999L,
                    text = "Review daily objectives",
                    iconCategory = "PLANNING",
                    postedBy = userName,
                    difficulty = TaskDifficulty.EASY,
                    isCompleted = false
                )
            )
        } else {
            listOf(
                ChoreItem(
                    id = 9998L,
                    text = "Organize workspace",
                    iconCategory = "ORGANIZING",
                    postedBy = userName,
                    difficulty = TaskDifficulty.EASY,
                    isCompleted = false
                ),
                ChoreItem(
                    id = 9999L,
                    text = "Review daily objectives",
                    iconCategory = "PLANNING",
                    postedBy = userName,
                    difficulty = TaskDifficulty.EASY,
                    isCompleted = false
                )
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CorporateBg)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Welcome Greeting Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Welcome back, $userName",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Daily operational overview and routine status.",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }

                // Clean Add Task Action Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CorporatePrimary)
                        .clickable {
                            SoundEffectManager.playPop()
                            onOpenThoughtBubble()
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Task",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add Task",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 2. Cadre Squad Status Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CorporateSurface)
                    .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
                    .clickable { onOpenSquadRoom() }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CorporateAccentBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = "Squad",
                                tint = CorporateAccentBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = squadName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CorporateBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = squadCode,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextMuted
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${members.size} active members • $streak day consistency streak",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open Squad Details",
                        tint = TextDisabled,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 3. Daily Routine Checks Section (Exactly 2 items)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Routine Checks",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Core daily operational checklist",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    val completedCount = routineChecks.count { it.isCompleted }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (completedCount == 2) CorporateSuccessLight else CorporateBg)
                            .border(
                                1.dp,
                                if (completedCount == 2) CorporateSuccess.copy(alpha = 0.3f) else CorporateCardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$completedCount / 2 Completed",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (completedCount == 2) CorporateSuccess else TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    routineChecks.forEach { chore ->
                        CorporateRoutineCard(
                            chore = chore,
                            onToggle = {
                                onCompleteChore(chore, Offset.Zero, Offset.Zero)
                            },
                            onOpenProof = { onOpenPhotoProof(chore) },
                            onOpenTutorial = { chore.tutorialId?.let { onOpenTutorial(it) } }
                        )
                    }
                }
            }
        }

        // 4. Squad Goals Section (Changed from weekly goals to squad goals)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Squad Goals",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Team milestone objectives for current cycle",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    Text(
                        text = "3 of 4 Done",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CorporateAccentBlue
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CorporateSurface)
                        .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Progress Bar
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Overall Team Progress",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "75%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { 0.75f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = CorporatePrimary,
                                trackColor = CorporateBg,
                            )
                        }

                        Divider(color = CorporateCardBorder, thickness = 0.8.dp)

                        // Squad Goal Items
                        SquadGoalRow(
                            title = "Complete daily routine checks across team",
                            assignee = "Team Target",
                            isDone = true
                        )
                        SquadGoalRow(
                            title = "Execute deep work focus blocks",
                            assignee = "All Members",
                            isDone = true
                        )
                        SquadGoalRow(
                            title = "Conduct peer operational review",
                            assignee = userName,
                            isDone = true
                        )
                        SquadGoalRow(
                            title = "Align on sprint deliverables",
                            assignee = "Pending Sync",
                            isDone = false
                        )
                    }
                }
            }
        }

        // 5. Performance Metrics Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricSummaryCard(
                    title = "Tasks Completed",
                    value = "28",
                    subtitle = "This cycle",
                    modifier = Modifier.weight(1f)
                )
                MetricSummaryCard(
                    title = "Cadre Sync",
                    value = "94%",
                    subtitle = "Team alignment",
                    modifier = Modifier.weight(1f)
                )
                MetricSummaryCard(
                    title = "Focus Streak",
                    value = "${streak}d",
                    subtitle = "Unbroken",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CorporateRoutineCard(
    chore: ChoreItem,
    onToggle: () -> Unit,
    onOpenProof: () -> Unit,
    onOpenTutorial: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDone = chore.isCompleted

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CorporateSurface)
            .border(
                width = 1.dp,
                color = if (isDone) CorporateSuccess.copy(alpha = 0.3f) else CorporateCardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clean Checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isDone) CorporateSuccess else Color.Transparent)
                    .border(
                        width = 1.5.dp,
                        color = if (isDone) CorporateSuccess else TextDisabled,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable {
                        SoundEffectManager.playPop()
                        onToggle()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chore.text,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDone) TextMuted else TextPrimary,
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CorporateBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = chore.iconCategory.lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Assigned: ${chore.postedBy}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    if (chore.requiresPhotoProof) {
                        Text(
                            text = if (chore.isVerified) "• Proof Verified" else "• Requires Proof",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (chore.isVerified) CorporateSuccess else CorporateAccentAmber,
                            modifier = Modifier.clickable { onOpenProof() }
                        )
                    }

                    if (chore.tutorialId != null) {
                        Text(
                            text = "• View Guide",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = CorporateAccentBlue,
                            modifier = Modifier.clickable { onOpenTutorial() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SquadGoalRow(
    title: String,
    assignee: String,
    isDone: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (isDone) CorporateSuccessLight else CorporateBg)
                    .border(
                        1.dp,
                        if (isDone) CorporateSuccess else TextDisabled,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = CorporateSuccess,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                color = if (isDone) TextMuted else TextPrimary,
                textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
            )
        }

        Text(
            text = assignee,
            fontSize = 11.sp,
            color = TextMuted
        )
    }
}

@Composable
private fun MetricSummaryCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CorporateSurface)
            .border(1.dp, CorporateCardBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}
