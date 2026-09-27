package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundEffectManager
import com.example.data.model.ChoreItem
import com.example.data.model.GroupMember
import com.example.data.model.TaskDifficulty
import com.example.data.model.UserProfile
import com.example.ui.components.CloudVaporDriftEffect
import com.example.ui.components.PairwiseCloudCard
import com.example.ui.components.ZenSpringSpecs
import com.example.ui.components.cloudFloatingDrift
import com.example.ui.components.rememberZenSpringFlingBehavior
import com.example.ui.components.springPress
import com.example.ui.theme.*
import com.example.viewmodel.PairwiseMatchup
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Data models for Squad Alignment Matching Quiz
data class SquadTaskOption(
    val title: String,
    val description: String,
    val domain: String
)

data class SquadQuizQuestion(
    val id: Int,
    val domainTitle: String,
    val questionText: String,
    val cardA: SquadTaskOption,
    val cardB: SquadTaskOption
)

data class SquadMatchResult(
    val squadName: String,
    val squadCode: String,
    val overallMatchPercent: Int,
    val summary: String,
    val teammates: List<SquadTeammate>
)

data class SquadTeammate(
    val name: String,
    val role: String,
    val matchPercent: Int
)

/**
 * Main Pairwise Sort Screen:
 * Houses the primary 'Pairwise Comparison' Sorting Algorithm Component that lets users
 * prioritize chores between two options to organize their daily circadian rhythm,
 * and also provides access to Squad Alignment & Code sharing.
 */
@Composable
fun PairwiseSortScreen(
    chores: List<ChoreItem> = emptyList(),
    currentMatchup: PairwiseMatchup? = null,
    onChooseMatchup: (chosenA: Boolean) -> Unit = {},
    onTieMatchup: () -> Unit = {},
    onSkipMatchup: () -> Unit = {},
    onResetScores: () -> Unit = {},
    onAddChore: (text: String, category: String, difficulty: TaskDifficulty) -> Unit = { _, _, _ -> },
    onCompleteChore: (chore: ChoreItem) -> Unit = {},
    userProfile: UserProfile? = null,
    members: List<GroupMember> = emptyList(),
    onJoinSquadWithCode: (code: String) -> Unit = {},
    onJoinMatchedSquad: (squadName: String, squadCode: String, matchedMembers: List<GroupMember>) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    // 0 = Pairwise Prioritizer (Daily Rhythm), 1 = Squad Hub & Team Code
    var mainTab by remember { mutableIntStateOf(0) }
    var showAddChoreDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CorporateBg)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        flingBehavior = rememberZenSpringFlingBehavior()
    ) {
        // 1. Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (mainTab == 0) "Daily Rhythm Prioritizer" else "Squad Hub",
                            fontSize = 24.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = (-0.6).sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (mainTab == 0)
                                "Pairwise comparison sorting: decide between 2 options to organize today."
                            else
                                "Connect with your squad and align work rhythms.",
                            fontSize = 12.5.sp,
                            color = TextMuted,
                            lineHeight = 16.sp
                        )
                    }

                    if (mainTab == 0) {
                        IconButton(
                            onClick = {
                                SoundEffectManager.playPop()
                                showAddChoreDialog = true
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CorporatePrimaryLight)
                                .border(1.dp, CorporateCardBorder, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Chore",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. High-Level Segmented Tabs: Pairwise Prioritizer vs Squad Hub
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CorporateSurface)
                    .border(1.dp, CorporateCardBorder, RoundedCornerShape(14.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (mainTab == 0) CorporatePrimaryLight else Color.Transparent)
                        .border(
                            1.dp,
                            if (mainTab == 0) CorporateAccentBlue.copy(alpha = 0.4f) else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            mainTab = 0
                            SoundEffectManager.playPop()
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = if (mainTab == 0) CorporateAccentBlue else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pairwise Sort",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (mainTab == 0) Color.White else TextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (mainTab == 1) CorporatePrimaryLight else Color.Transparent)
                        .border(
                            1.dp,
                            if (mainTab == 1) CorporateAccentBlue.copy(alpha = 0.4f) else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            mainTab = 1
                            SoundEffectManager.playPop()
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = if (mainTab == 1) CorporateAccentBlue else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Squad Hub",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (mainTab == 1) Color.White else TextSecondary
                        )
                    }
                }
            }
        }

        // ========================================================
        // TAB 0: PAIRWISE COMPARISON SORTING ALGORITHM COMPONENT
        // ========================================================
        if (mainTab == 0) {
            // A. Algorithm Status & Metrics Banner
            item {
                PairwiseStatusOverviewCard(
                    chores = chores,
                    onSkipMatchup = onSkipMatchup,
                    onResetScores = { showResetConfirmDialog = true }
                )
            }

            // B. The Pairwise Comparison Matchup Arena (Option A vs Option B)
            item {
                PairwiseComparisonArena(
                    matchup = currentMatchup,
                    allChores = chores,
                    onChooseOption = onChooseMatchup,
                    onTie = onTieMatchup,
                    onSkip = onSkipMatchup,
                    onAddNewChore = { showAddChoreDialog = true }
                )
            }

            // C. Organized Daily Rhythm Schedule Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Organized Daily Rhythm",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Dynamic circadian blocks ranked by pairwise Elo score",
                            fontSize = 11.5.sp,
                            color = TextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CorporateAccentTealLight)
                            .border(1.dp, CorporateAccentTeal.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Live Sorted",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CorporateAccentTeal
                        )
                    }
                }
            }

            // Partition chores into 3 Circadian Rhythm Blocks
            val activeChores = chores.filter { !it.isCompleted }
            val completedChores = chores.filter { it.isCompleted }

            // 1. Morning Peak Focus Block (Ranks 1 & 2)
            val morningChores = activeChores.take(2)
            if (morningChores.isNotEmpty()) {
                item {
                    RhythmBlockHeader(
                        title = "Morning Focus Block",
                        timeRange = "8:00 AM – 12:00 PM",
                        icon = "🌅",
                        badge = "Peak Energy • High Priority",
                        color = Color(0xFFF59E0B)
                    )
                }

                items(morningChores, key = { it.id }) { chore ->
                    val overallRank = activeChores.indexOfFirst { it.id == chore.id } + 1
                    RhythmChoreCard(
                        chore = chore,
                        rank = overallRank,
                        onComplete = { onCompleteChore(chore) }
                    )
                }
            }

            // 2. Afternoon Momentum Block (Ranks 3 & 4)
            val afternoonChores = activeChores.drop(2).take(2)
            if (afternoonChores.isNotEmpty()) {
                item {
                    RhythmBlockHeader(
                        title = "Afternoon Momentum Block",
                        timeRange = "1:00 PM – 5:00 PM",
                        icon = "☀️",
                        badge = "Steady Flow • Execution",
                        color = Color(0xFF38BDF8)
                    )
                }

                items(afternoonChores, key = { it.id }) { chore ->
                    val overallRank = activeChores.indexOfFirst { it.id == chore.id } + 1
                    RhythmChoreCard(
                        chore = chore,
                        rank = overallRank,
                        onComplete = { onCompleteChore(chore) }
                    )
                }
            }

            // 3. Evening Wrap-Up Block (Ranks 5+)
            val eveningChores = activeChores.drop(4)
            if (eveningChores.isNotEmpty()) {
                item {
                    RhythmBlockHeader(
                        title = "Evening Wrap-up & Reset",
                        timeRange = "5:00 PM – 8:00 PM",
                        icon = "🌙",
                        badge = "Documentation • Restoration",
                        color = Color(0xFFA78BFA)
                    )
                }

                items(eveningChores, key = { it.id }) { chore ->
                    val overallRank = activeChores.indexOfFirst { it.id == chore.id } + 1
                    RhythmChoreCard(
                        chore = chore,
                        rank = overallRank,
                        onComplete = { onCompleteChore(chore) }
                    )
                }
            }

            // Completed Chores Collapsible View
            if (completedChores.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CorporateSurface)
                            .border(1.dp, CorporateCardBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Completed Chores Today (${completedChores.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        completedChores.forEach { doneItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = CorporateSuccess,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = doneItem.text,
                                        fontSize = 12.sp,
                                        color = TextMuted,
                                        textDecoration = TextDecoration.LineThrough,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "+${doneItem.difficulty.xp} XP",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CorporateSuccess
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // ========================================================
            // TAB 1: SQUAD HUB & TEAM ALIGNMENT QUIZ
            // ========================================================
            item {
                SquadHubTabContent(
                    userProfile = userProfile,
                    members = members,
                    onJoinSquadWithCode = onJoinSquadWithCode,
                    onJoinMatchedSquad = onJoinMatchedSquad
                )
            }
        }
    }

    // Dialog: Add Custom Chore to Prioritizer
    if (showAddChoreDialog) {
        QuickAddChoreDialog(
            onDismiss = { showAddChoreDialog = false },
            onConfirm = { text, category, difficulty ->
                onAddChore(text, category, difficulty)
                showAddChoreDialog = false
                Toast.makeText(context, "Added chore to Daily Rhythm!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Dialog: Reset Confirmation
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Text(
                    text = "Reset Rhythm Rankings?",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "This resets chore comparison scores back to default levels so you can start a fresh pairwise comparison sorting round.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetScores()
                        showResetConfirmDialog = false
                        Toast.makeText(context, "Pairwise scores reset", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CoralIndicator,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showResetConfirmDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CorporateSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

/**
 * Metric card showing algorithm status, total comparisons, and confidence level
 */
@Composable
private fun PairwiseStatusOverviewCard(
    chores: List<ChoreItem>,
    onSkipMatchup: () -> Unit,
    onResetScores: () -> Unit
) {
    val totalComparisons = chores.sumOf { it.matchupTotal } / 2
    val confidencePercent = if (chores.size < 2) 0 else ((totalComparisons * 12).coerceIn(20, 96))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF191B24), Color(0xFF111218))
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(Color(0x35FFFFFF), Color(0x0CFFFFFF))
                ),
                RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CorporateAccentBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = null,
                            tint = CorporateAccentBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Elo Sorting Engine",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Binary preference optimization",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = {
                            SoundEffectManager.playTick()
                            onSkipMatchup()
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CorporateSurface)
                            .border(1.dp, CorporateCardBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Shuffle Matchup",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            SoundEffectManager.playPop()
                            onResetScores()
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CorporateSurface)
                            .border(1.dp, CorporateCardBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Reset Rankings",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Quick Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickMetricPill(
                    label = "Active Pool",
                    value = "${chores.count { !it.isCompleted }} Chores",
                    modifier = Modifier.weight(1f)
                )
                QuickMetricPill(
                    label = "Decisions Made",
                    value = "$totalComparisons Pairs",
                    modifier = Modifier.weight(1f)
                )
                QuickMetricPill(
                    label = "Sorting Depth",
                    value = "$confidencePercent%",
                    modifier = Modifier.weight(1f)
                )
            }

            // Explanatory Note
            Text(
                text = "Comparing pairs removes decision fatigue: higher-rated chores naturally rise to your peak circadian morning blocks.",
                fontSize = 11.5.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun QuickMetricPill(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CorporateBg)
            .border(1.dp, CorporateCardBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * The Interactive Pairwise Arena Component where users vote between Option A and Option B
 */
@Composable
private fun PairwiseComparisonArena(
    matchup: PairwiseMatchup?,
    allChores: List<ChoreItem>,
    onChooseOption: (chosenA: Boolean) -> Unit,
    onTie: () -> Unit,
    onSkip: () -> Unit,
    onAddNewChore: () -> Unit
) {
    if (matchup == null || allChores.filter { !it.isCompleted }.size < 2) {
        // Empty / Fallback State
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CorporateSurface)
                .border(1.dp, CorporateCardBorder, RoundedCornerShape(18.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "⚖️", fontSize = 36.sp)
                Text(
                    text = "Add At Least 2 Chores to Prioritize",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Add daily chores or uncheck completed items to start pairwise rhythm sorting.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onAddNewChore,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("+ Add Chore to Sort", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }
            }
        }
        return
    }

    val choreA = matchup.choreA
    val choreB = matchup.choreB

    // Selection tracking for custom floating drifting animation
    var selectedOption by remember { mutableStateOf<Boolean?>(null) } // true = A, false = B
    var isSelecting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1B1D27), Color(0xFF12131A))
                )
            )
            .border(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(CorporateAccentBlue.copy(alpha = 0.5f), Color(0x10FFFFFF))
                ),
                RoundedCornerShape(22.dp)
            )
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Arena Headline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "☁️ Pairwise Cloud Matchup",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• Floating drift priority",
                        fontSize = 12.sp,
                        color = CorporateAccentBlue
                    )
                }

                Text(
                    text = if (isSelecting) "Prioritizing..." else "Select A or B",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelecting) CorporateAccentBlue else TextMuted
                )
            }

            // OPTION A CLOUD-CARD (Floating & Drifting)
            PairwiseCloudCard(
                label = "OPTION A",
                chore = choreA,
                accentColor = CorporateAccentBlue,
                appearTrigger = choreA.id,
                isSelected = selectedOption == true,
                isUnselected = selectedOption == false,
                driftPhaseOffsetMs = 0,
                onClick = {
                    if (!isSelecting) {
                        isSelecting = true
                        selectedOption = true
                        SoundEffectManager.playPop()
                        coroutineScope.launch {
                            delay(340) // Allow floating drift lift animation to play
                            onChooseOption(true)
                            selectedOption = null
                            isSelecting = false
                        }
                    }
                }
            )

            // Dynamic VS Divider with Cloud Vapor Accent
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = CorporateCardBorder
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .clip(CircleShape)
                        .background(CorporateBg)
                        .border(1.dp, CorporateAccentBlue.copy(alpha = 0.4f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "VS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CorporateAccentBlue
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = CorporateCardBorder
                )
            }

            // OPTION B CLOUD-CARD (Floating & Drifting with counter-phase)
            PairwiseCloudCard(
                label = "OPTION B",
                chore = choreB,
                accentColor = Color(0xFFA78BFA),
                appearTrigger = choreB.id,
                isSelected = selectedOption == false,
                isUnselected = selectedOption == true,
                driftPhaseOffsetMs = 1500, // Organic counter-phase drift
                onClick = {
                    if (!isSelecting) {
                        isSelecting = true
                        selectedOption = false
                        SoundEffectManager.playPop()
                        coroutineScope.launch {
                            delay(340) // Allow floating drift lift animation to play
                            onChooseOption(false)
                            selectedOption = null
                            isSelecting = false
                        }
                    }
                }
            )

            // Cloud Vapor Drift Effect overlay on selection
            if (isSelecting) {
                CloudVaporDriftEffect(
                    isActive = true,
                    tintColor = if (selectedOption == true) CorporateAccentBlue else Color(0xFFA78BFA)
                )
            }

            // Alternative Choices: Tie / Skip / Add
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        SoundEffectManager.playPop()
                        onTie()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextSecondary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(CorporateCardBorder, CorporateCardBorder))
                    )
                ) {
                    Text("Equal Priority (Tie)", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = {
                        SoundEffectManager.playTick()
                        onSkip()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextSecondary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(CorporateCardBorder, CorporateCardBorder))
                    )
                ) {
                    Text("Skip Matchup", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/**
 * Individual Option Card inside the Arena with spring interaction and rich metadata
 */
@Composable
private fun PairwiseOptionCard(
    label: String,
    chore: ChoreItem,
    accentColor: Color,
    onClick: () -> Unit
) {
    PairwiseCloudCard(
        label = label,
        chore = chore,
        accentColor = accentColor,
        onClick = onClick,
        appearTrigger = chore.id
    )
}

/**
 * Circadian Block Header (Morning, Afternoon, Evening)
 */
@Composable
private fun RhythmBlockHeader(
    title: String,
    timeRange: String,
    icon: String,
    badge: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = timeRange,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(color.copy(alpha = 0.15f))
                .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                .padding(horizontal = 7.dp, vertical = 2.5.dp)
        ) {
            Text(
                text = badge,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

/**
 * Individual Card in the Organized Daily Rhythm view
 */
@Composable
private fun RhythmChoreCard(
    chore: ChoreItem,
    rank: Int,
    onComplete: () -> Unit
) {
    val rankBadgeColor = when (rank) {
        1 -> Color(0xFFF59E0B) // Gold
        2 -> Color(0xFF94A3B8) // Silver
        3 -> Color(0xFFD97706) // Bronze
        else -> CorporateAccentBlue
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .springPress(pressScale = 0.985f)
            .clip(RoundedCornerShape(14.dp))
            .background(CorporateSurface)
            .border(1.dp, CorporateCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Rank Number Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(rankBadgeColor.copy(alpha = 0.18f))
                        .border(1.dp, rankBadgeColor.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#$rank",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = rankBadgeColor
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chore.text,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${chore.iconCategory} • Score ${chore.score} • Assigned to ${chore.postedBy}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Quick Complete Button
            IconButton(
                onClick = {
                    SoundEffectManager.playChime()
                    onComplete()
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CorporateSuccessLight)
                    .border(1.dp, CorporateSuccess.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Complete Chore",
                    tint = CorporateSuccess,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Dialog to add a custom chore to the prioritizer pool
 */
@Composable
private fun QuickAddChoreDialog(
    onDismiss: () -> Unit,
    onConfirm: (text: String, category: String, difficulty: TaskDifficulty) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("ORGANIZING") }
    var difficulty by remember { mutableStateOf(TaskDifficulty.EASY) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CorporateSurface)
                .border(1.dp, CorporateCardBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Daily Rhythm Chore",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("e.g. Deep focus on architecture review", color = TextDisabled, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CorporateAccentBlue,
                        unfocusedBorderColor = CorporateCardBorder,
                        focusedContainerColor = CorporateBg,
                        unfocusedContainerColor = CorporateBg,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Category Selector
                Text(text = "Category", fontSize = 12.sp, color = TextMuted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("ORGANIZING", "PLANNING", "REVIEW", "DEVELOPMENT").forEach { cat ->
                        val isSelected = category == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CorporatePrimaryLight else CorporateBg)
                                .border(
                                    1.dp,
                                    if (isSelected) CorporateAccentBlue else CorporateCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { category = cat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat.take(4),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextMuted
                            )
                        }
                    }
                }

                // Difficulty Selector
                Text(text = "Difficulty & Priority Base", fontSize = 12.sp, color = TextMuted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TaskDifficulty.values().forEach { diff ->
                        val isSelected = difficulty == diff
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CorporatePrimaryLight else CorporateBg)
                                .border(
                                    1.dp,
                                    if (isSelected) CorporateAccentBlue else CorporateCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { difficulty = diff }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${diff.label} (${diff.xp}XP)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextMuted
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (text.isNotBlank()) {
                            onConfirm(text.trim(), category, difficulty)
                        }
                    },
                    enabled = text.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Add to Prioritizer Pool", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

/**
 * Squad Hub tab content: Team Code & 6-Question Alignment Quiz
 */
@Composable
private fun SquadHubTabContent(
    userProfile: UserProfile?,
    members: List<GroupMember>,
    onJoinSquadWithCode: (code: String) -> Unit,
    onJoinMatchedSquad: (squadName: String, squadCode: String, matchedMembers: List<GroupMember>) -> Unit
) {
    var selectedOptionTab by remember { mutableIntStateOf(0) }
    var enteredCode by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val questions = remember {
        listOf(
            SquadQuizQuestion(
                id = 1,
                domainTitle = "Daily Rhythm",
                questionText = "When is your mind most energized and ready to focus?",
                cardA = SquadTaskOption(
                    title = "Morning Focus Blocks",
                    description = "Tackle the most important goals early in the morning",
                    domain = "Focus"
                ),
                cardB = SquadTaskOption(
                    title = "Afternoon Momentum",
                    description = "Build steady momentum through structured afternoon sessions",
                    domain = "Execution"
                )
            ),
            SquadQuizQuestion(
                id = 2,
                domainTitle = "Team Communication",
                questionText = "How do you prefer to keep teammates in sync?",
                cardA = SquadTaskOption(
                    title = "Written Daily Updates",
                    description = "Clear written bullet points with detailed links and notes",
                    domain = "Documentation"
                ),
                cardB = SquadTaskOption(
                    title = "Quick Live Check-ins",
                    description = "Short collaborative touchpoints to discuss blockers",
                    domain = "Collaboration"
                )
            ),
            SquadQuizQuestion(
                id = 3,
                domainTitle = "Goal Setting",
                questionText = "How do you map out your weekly milestones?",
                cardA = SquadTaskOption(
                    title = "Structured Checklists",
                    description = "Specific daily checklists planned in advance",
                    domain = "Planning"
                ),
                cardB = SquadTaskOption(
                    title = "Adaptive Sprints",
                    description = "Flexible daily goals adjusted to live project needs",
                    domain = "Agility"
                )
            ),
            SquadQuizQuestion(
                id = 4,
                domainTitle = "Quality & Review",
                questionText = "What is your approach to feedback and review?",
                cardA = SquadTaskOption(
                    title = "Continuous Mini-Reviews",
                    description = "Frequent small check-ins as tasks progress",
                    domain = "Iterative"
                ),
                cardB = SquadTaskOption(
                    title = "Comprehensive Milestone Review",
                    description = "Thorough formal review once deliverables are complete",
                    domain = "Precision"
                )
            ),
            SquadQuizQuestion(
                id = 5,
                domainTitle = "Deep Work",
                questionText = "What is your ideal focus environment?",
                cardA = SquadTaskOption(
                    title = "Quiet Solo Focus Blocks",
                    description = "Uninterrupted quiet sessions for deep thinking",
                    domain = "Deep Focus"
                ),
                cardB = SquadTaskOption(
                    title = "Shared Virtual Workspace",
                    description = "Working alongside peers in a collaborative session",
                    domain = "Co-Working"
                )
            ),
            SquadQuizQuestion(
                id = 6,
                domainTitle = "Sprint Rhythm",
                questionText = "How do you wrap up your weekly work cycle?",
                cardA = SquadTaskOption(
                    title = "Friday Squad Retrospective",
                    description = "Celebrate wins and document lessons learned together",
                    domain = "Reflection"
                ),
                cardB = SquadTaskOption(
                    title = "Monday Launch Planning",
                    description = "Kick off the new week with crystal-clear targets",
                    domain = "Initiative"
                )
            )
        )
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() }
    var isCalculatingMatch by remember { mutableStateOf(false) }
    var matchResult by remember { mutableStateOf<SquadMatchResult?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Sub-tabs: Code vs Match
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CorporateSurface)
                .border(1.dp, CorporateCardBorder, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedOptionTab == 0) CorporatePrimaryLight else Color.Transparent)
                    .clickable {
                        selectedOptionTab = 0
                        SoundEffectManager.playPop()
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Squad Code",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selectedOptionTab == 0) Color.White else TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedOptionTab == 1) CorporatePrimaryLight else Color.Transparent)
                    .clickable {
                        selectedOptionTab = 1
                        SoundEffectManager.playPop()
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Find Matching Squad",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selectedOptionTab == 1) Color.White else TextSecondary
                )
            }
        }

        if (selectedOptionTab == 0) {
            // Option 1: Team Code
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CorporateSurface)
                    .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CorporateAccentBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = CorporateAccentBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Join Existing Squad",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Enter a 6-character squad code",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    OutlinedTextField(
                        value = enteredCode,
                        onValueChange = { enteredCode = it.uppercase() },
                        placeholder = { Text("e.g. SQUAD-8X9", color = TextDisabled) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CorporateAccentBlue,
                            unfocusedBorderColor = CorporateCardBorder,
                            focusedContainerColor = CorporateBg,
                            unfocusedContainerColor = CorporateBg,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (enteredCode.isNotBlank()) {
                                SoundEffectManager.playPop()
                                onJoinSquadWithCode(enteredCode.trim())
                                Toast.makeText(context, "Joined squad $enteredCode", Toast.LENGTH_SHORT).show()
                                enteredCode = ""
                            }
                        },
                        enabled = enteredCode.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                            disabledContainerColor = CorporateCardBorder,
                            disabledContentColor = TextDisabled
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Text(
                            text = "Connect to Squad",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (enteredCode.isNotBlank()) Color.Black else TextDisabled
                        )
                    }
                }
            }

            // Active Squad Card
            val currentCode = userProfile?.squadCode ?: "SQUAD-8X9"
            val currentName = userProfile?.squadName ?: "Alpha Squad"

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CorporateSurface)
                    .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Your Active Squad",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CorporateBg)
                            .border(1.dp, CorporateCardBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Code: $currentCode",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Squad Code", currentCode))
                                Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Teammates in Active Squad
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Squad Teammates (${members.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                members.forEach { member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CorporateSurface)
                            .border(1.dp, CorporateCardBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CorporateAccentBlueLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = member.name.take(1),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CorporateAccentBlue
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = member.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = member.personalityArchetype,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CorporateSuccessLight)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${member.tasksCompletedCount} Done",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CorporateSuccess
                            )
                        }
                    }
                }
            }
        } else {
            // Option 2: 6-Question Alignment Matching
            if (isCalculatingMatch) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CorporateSurface)
                        .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = CorporateAccentBlue,
                            modifier = Modifier.size(40.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Finding Compatible Squad...",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Evaluating collaboration rhythms and focus patterns",
                            fontSize = 12.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (matchResult != null) {
                val res = matchResult!!
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CorporateSurface)
                        .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Matched Squad Found",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = res.squadName,
                                    fontSize = 13.sp,
                                    color = TextMuted
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CorporateSuccessLight)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "${res.overallMatchPercent}% Match",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CorporateSuccess
                                )
                            }
                        }

                        Text(
                            text = res.summary,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )

                        Button(
                            onClick = {
                                SoundEffectManager.playPop()
                                val newMembers = res.teammates.map {
                                    GroupMember(
                                        name = it.name,
                                        personalityArchetype = it.role,
                                        primaryInterest = "Operations",
                                        tasksCompletedCount = (2..5).random(),
                                        isCurrentActiveUser = false
                                    )
                                }
                                onJoinMatchedSquad(res.squadName, res.squadCode, newMembers)
                                Toast.makeText(context, "Joined ${res.squadName}", Toast.LENGTH_SHORT).show()
                                matchResult = null
                                userAnswers.clear()
                                currentQuestionIndex = 0
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Join Squad",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            } else {
                val currentQ = questions[currentQuestionIndex]
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMuted
                        )
                        Text(
                            text = currentQ.domainTitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CorporateAccentBlue
                        )
                    }

                    LinearProgressIndicator(
                        progress = { (currentQuestionIndex + 1) / questions.size.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = CorporatePrimary,
                        trackColor = CorporateCardBorder
                    )

                    Text(
                        text = currentQ.questionText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    SquadQuizCard(
                        option = currentQ.cardA,
                        label = "Option A",
                        isSelected = userAnswers[currentQ.id] == 0,
                        onClick = {
                            SoundEffectManager.playPop()
                            userAnswers[currentQ.id] = 0
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                            } else {
                                coroutineScope.launch {
                                    isCalculatingMatch = true
                                    delay(1000)
                                    isCalculatingMatch = false
                                    val matchScore = 82 + (userAnswers.values.sum() * 3) % 15
                                    matchResult = SquadMatchResult(
                                        squadName = "Focus & Flow Squad",
                                        squadCode = "SQUAD-FL7",
                                        overallMatchPercent = matchScore.coerceIn(70, 97),
                                        summary = "Strong compatibility in morning deep work, written updates, and purposeful weekly reflections.",
                                        teammates = listOf(
                                            SquadTeammate("Elena Vance", "Strategy Lead", matchScore),
                                            SquadTeammate("Marcus Cole", "Execution Specialist", matchScore - 3),
                                            SquadTeammate("Priya Nair", "Quality Reviewer", matchScore - 2)
                                        )
                                    )
                                }
                            }
                        }
                    )

                    SquadQuizCard(
                        option = currentQ.cardB,
                        label = "Option B",
                        isSelected = userAnswers[currentQ.id] == 1,
                        onClick = {
                            SoundEffectManager.playPop()
                            userAnswers[currentQ.id] = 1
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                            } else {
                                coroutineScope.launch {
                                    isCalculatingMatch = true
                                    delay(1000)
                                    isCalculatingMatch = false
                                    val matchScore = 78 + (userAnswers.values.sum() * 4) % 18
                                    matchResult = SquadMatchResult(
                                        squadName = "Agile Core Squad",
                                        squadCode = "SQUAD-AG3",
                                        overallMatchPercent = matchScore.coerceIn(70, 97),
                                        summary = "High alignment in dynamic collaborative check-ins, adaptable goal sprints, and team standups.",
                                        teammates = listOf(
                                            SquadTeammate("Sarah Jenkins", "Product Coordinator", matchScore),
                                            SquadTeammate("David Chen", "Sprint Manager", matchScore - 3),
                                            SquadTeammate("Amara Okafor", "Systems Architect", matchScore - 4)
                                        )
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SquadQuizCard(
    option: SquadTaskOption,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .cloudFloatingDrift(
                appearTrigger = option.title,
                isSelected = isSelected,
                driftPhaseOffsetMs = if (label.contains("A", ignoreCase = true)) 0 else 1400,
                floatRangeDp = 3.5f,
                driftRangeDp = 2.2f,
                selectedLiftDp = -14f
            )
            .springPress(pressScale = 0.97f)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) CorporateAccentBlueLight else CorporateSurface)
            .border(
                1.5.dp,
                if (isSelected) CorporateAccentBlue else CorporateCardBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) CorporateAccentBlue else CorporateBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option.domain,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = option.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = option.description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = CorporateAccentBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
