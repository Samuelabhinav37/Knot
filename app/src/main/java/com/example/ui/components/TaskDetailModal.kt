package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.audio.SoundEffectManager
import com.example.data.model.FeaturedTask
import com.example.data.model.TaskStepItem
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun TaskDetailModal(
    task: FeaturedTask,
    onDismiss: () -> Unit,
    onCompleteTask: (FeaturedTask) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSessionActive by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Overview, 1 = Details
    var chosenDurationMinutes by remember { mutableIntStateOf(task.durationMinutes) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CorporateBg)
                .systemBarsPadding()
        ) {
            AnimatedContent(
                targetState = isSessionActive,
                transitionSpec = {
                    fadeIn() + slideInHorizontally() togetherWith fadeOut() + slideOutHorizontally()
                },
                label = "TaskScreenTransition"
            ) { active ->
                if (!active) {
                    TaskOverviewLayout(
                        task = task,
                        selectedTab = selectedTab,
                        chosenDuration = chosenDurationMinutes,
                        onSelectDuration = { chosenDurationMinutes = it },
                        onTabSelected = { selectedTab = it },
                        onDismiss = onDismiss,
                        onBeginTask = {
                            SoundEffectManager.playPop()
                            isSessionActive = true
                        }
                    )
                } else {
                    ActiveTaskSessionLayout(
                        task = task,
                        durationMinutes = chosenDurationMinutes,
                        onBack = { isSessionActive = false },
                        onComplete = {
                            SoundEffectManager.playFanfare()
                            onCompleteTask(task)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Task Overview View - Direct Translation of the Reference Screenshot Layout
 */
@Composable
private fun TaskOverviewLayout(
    task: FeaturedTask,
    selectedTab: Int,
    chosenDuration: Int,
    onSelectDuration: (Int) -> Unit,
    onTabSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    onBeginTask: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Top Navigation Bar with Back / Close Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CorporateSurface)
                    .border(1.dp, CorporateCardBorder, CircleShape)
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "Task Details",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            // Balance placeholder
            Spacer(modifier = Modifier.size(40.dp))
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ========================================================
            // 1. Large Rounded Hero Image Card with Bottom Overlay Pill
            // ========================================================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(310.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(CorporateSurface)
                ) {
                    Image(
                        painter = painterResource(id = task.imageRes),
                        contentDescription = task.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.2f),
                                        Color.Black.copy(alpha = 0.75f)
                                    )
                                )
                            )
                    )

                    // Floating Glass Overlay Pill at bottom of Image (from screenshot!)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(14.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF141419).copy(alpha = 0.90f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${task.locationOrTag} • ${task.category}",
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Reward",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = "+${task.xpReward} XP",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CorporateAccentAmber
                                )
                            }
                        }
                    }
                }
            }

            // ========================================================
            // 2. Overview / Details Tab Row (from screenshot!)
            // ========================================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.clickable { onTabSelected(0) }
                    ) {
                        Text(
                            text = "Overview",
                            fontSize = 16.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 0) Color.White else TextDisabled
                        )
                        if (selectedTab == 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(2.5.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(CoralIndicator)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.clickable { onTabSelected(1) }
                    ) {
                        Text(
                            text = "Details",
                            fontSize = 16.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 1) Color.White else TextDisabled
                        )
                        if (selectedTab == 1) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(2.5.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(CoralIndicator)
                            )
                        }
                    }
                }
            }

            // ========================================================
            // 3. Quick Stats Row with Rounded Squares (from screenshot!)
            // Time, Intensity/Metric, and Difficulty (replacing stars!)
            // ========================================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stat 1: Duration (Clock)
                    QuickStatBox(
                        icon = Icons.Default.Schedule,
                        label = task.durationLabel,
                        iconTint = CorporateAccentBlue
                    )

                    // Stat 2: Intensity / Energy (Flame/Metric)
                    QuickStatBox(
                        icon = Icons.Default.LocalFireDepartment,
                        label = task.metricLabel,
                        iconTint = CoralIndicator
                    )

                    // Stat 3: Difficulty (Speed/Gauge replacing star!)
                    QuickStatBox(
                        icon = Icons.Default.Speed,
                        label = task.difficulty,
                        iconTint = CorporateAccentTeal
                    )
                }
            }

            // ========================================================
            // 3b. Customizable Session Target Duration
            // ========================================================
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target Session Duration",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "$chosenDuration mins",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CorporateAccentBlue
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val presets = remember(task.durationMinutes) {
                            val base = task.durationMinutes
                            listOf(
                                (base * 0.7).toInt().coerceAtLeast(5) to "Express",
                                base to "Standard",
                                (base * 1.5).toInt() to "Deep"
                            ).distinctBy { it.first }
                        }

                        presets.forEach { (mins, label) ->
                            val isSelected = (chosenDuration == mins)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) CorporateAccentBlue.copy(alpha = 0.18f) else CorporateSurface)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) CorporateAccentBlue else CorporateCardBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        SoundEffectManager.playPop()
                                        onSelectDuration(mins)
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${mins}m",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) CorporateAccentBlue else Color.White
                                    )
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        color = if (isSelected) CorporateAccentBlue.copy(alpha = 0.8f) else TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ========================================================
            // 4. Description Paragraph & Benefits / Steps Preview
            // ========================================================
            item {
                if (selectedTab == 0) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = task.fullOverview,
                            fontSize = 13.5.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Key Physiological Benefits",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        task.keyBenefits.forEach { benefit ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 5.dp)
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(CorporateAccentBlue)
                                )
                                Text(
                                    text = benefit,
                                    fontSize = 12.5.sp,
                                    color = TextSecondary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Routine Roadmap (${task.steps.size} Steps)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        task.steps.forEach { step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CorporateSurface)
                                    .border(1.dp, CorporateCardBorder, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(CorporatePrimaryLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${step.stepNumber}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = step.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                        if (step.durationText.isNotBlank()) {
                                            Text(
                                                text = step.durationText,
                                                fontSize = 11.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = step.instruction,
                                        fontSize = 11.5.sp,
                                        color = TextSecondary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // ========================================================
        // 5. Full-Width Bottom Action Button ("Begin Task 🚀")
        // Styled directly like the "Read more ✈️" pill in screenshot
        // ========================================================
        Button(
            onClick = onBeginTask,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF222228),
                contentColor = Color.White
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Begin Task",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.RocketLaunch,
                    contentDescription = null,
                    tint = CoralIndicator,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Rounded Square Quick Stat Box (Time, Flame/Metric, Difficulty)
 */
@Composable
private fun QuickStatBox(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    iconTint: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CorporateSurface)
            .border(1.dp, CorporateCardBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CorporateBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

/**
 * Guided Execution Mode with Interactive Steps & Live Countdown Timer
 */
@Composable
private fun ActiveTaskSessionLayout(
    task: FeaturedTask,
    durationMinutes: Int = task.durationMinutes,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val totalInitialSeconds = (durationMinutes * 60).coerceAtLeast(60)
    var totalSeconds by remember { mutableIntStateOf(totalInitialSeconds) }
    var isTimerRunning by remember { mutableStateOf(true) }
    val completedStepIndices = remember { mutableStateListOf<Int>() }
    var showCelebrationModal by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf("⚡ Energized") }

    // Coroutine Timer Tick
    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && totalSeconds > 0) {
            delay(1000L)
            totalSeconds--
            if (totalSeconds == 0) {
                SoundEffectManager.playFanfare()
                showCelebrationModal = true
            }
        }
    }

    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)
    val secondsElapsed = totalInitialSeconds - totalSeconds
    val elapsedMinutes = secondsElapsed / 60
    val elapsedSecs = secondsElapsed % 60
    val formattedElapsed = String.format("%02d:%02d", elapsedMinutes, elapsedSecs)
    val progressRatio = if (totalInitialSeconds > 0) {
        (totalInitialSeconds - totalSeconds).toFloat() / totalInitialSeconds.toFloat()
    } else 0f

    val allStepsDone = (completedStepIndices.size >= task.steps.size)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Session Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CorporateSurface)
                    .border(1.dp, CorporateCardBorder, CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isTimerRunning) CorporateSuccess else CorporateAccentAmber)
                    )
                    Text(
                        text = if (isTimerRunning) "In Progress" else "Paused",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.size(40.dp))
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // Live Countdown Timer Display Card
            // ==========================================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(CorporateSurface)
                        .border(1.dp, CorporateCardBorder, RoundedCornerShape(22.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Remaining Time",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )

                        Text(
                            text = formattedTime,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 2.sp
                        )

                        // Visual Progress Bar
                        LinearProgressIndicator(
                            progress = { progressRatio.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CorporateAccentBlue,
                            trackColor = CorporateBg
                        )

                        // Timer Action Controls
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Pause / Play
                            Button(
                                onClick = {
                                    SoundEffectManager.playPop()
                                    isTimerRunning = !isTimerRunning
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isTimerRunning) CorporatePrimaryLight else CorporateAccentBlue,
                                    contentColor = if (isTimerRunning) Color.White else Color.Black
                                ),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Icon(
                                    imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isTimerRunning) "Pause" else "Resume",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isTimerRunning) "Pause" else "Resume",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // +1 Min Quick Extension
                            OutlinedButton(
                                onClick = {
                                    SoundEffectManager.playPop()
                                    totalSeconds += 60
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Text(
                                    text = "+1 Min",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            // Reset Timer
                            OutlinedButton(
                                onClick = {
                                    SoundEffectManager.playPop()
                                    totalSeconds = task.durationMinutes * 60
                                    isTimerRunning = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reset",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // Sequential Steps Checklist Header
            // ==========================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Steps to Complete",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${completedStepIndices.size} of ${task.steps.size} steps completed",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    if (allStepsDone) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CorporateSuccessLight)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "All Done! 🎉",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CorporateSuccess
                            )
                        }
                    }
                }
            }

            // ==========================================
            // Interactive Step Checklist Cards
            // ==========================================
            items(task.steps) { step ->
                val isDone = completedStepIndices.contains(step.stepNumber)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CorporateSurface)
                        .border(
                            width = 1.dp,
                            color = if (isDone) CorporateSuccess.copy(alpha = 0.4f) else CorporateCardBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            SoundEffectManager.playPop()
                            if (isDone) {
                                completedStepIndices.remove(step.stepNumber)
                            } else {
                                completedStepIndices.add(step.stepNumber)
                            }
                        }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Checkbox
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isDone) CorporateSuccess else Color.Transparent)
                                .border(
                                    width = 1.5.dp,
                                    color = if (isDone) CorporateSuccess else TextDisabled,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Step ${step.stepNumber}: ${step.title}",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDone) TextMuted else Color.White,
                                    textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                                )

                                if (step.durationText.isNotBlank()) {
                                    Text(
                                        text = step.durationText,
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = step.instruction,
                                fontSize = 12.sp,
                                color = if (isDone) TextMuted.copy(alpha = 0.7f) else TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Complete & Claim Action Button
        Button(
            onClick = {
                isTimerRunning = false
                SoundEffectManager.playFanfare()
                showCelebrationModal = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (allStepsDone) CorporateSuccess else Color(0xFF222228),
                contentColor = Color.White
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (allStepsDone) "Complete Task & Claim +${task.xpReward} XP" else "Finish Session Early",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (allStepsDone) Icons.Default.CheckCircle else Icons.Default.Check,
                    contentDescription = null,
                    tint = if (allStepsDone) Color.White else CoralIndicator,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    // Celebration & Reflection Bottom Sheet / Modal
    if (showCelebrationModal) {
        Dialog(
            onDismissRequest = {
                showCelebrationModal = false
                onComplete()
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CorporateSurface)
                    .border(1.dp, CorporateCardBorder, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(CorporateAccentAmber.copy(alpha = 0.2f))
                            .border(1.dp, CorporateAccentAmber, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏆", fontSize = 32.sp)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Activity Accomplished!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.title,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    // Stat summary pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CorporateBg)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = formattedElapsed,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Active Time",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CorporateBg)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${completedStepIndices.size}/${task.steps.size}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CorporateAccentTeal
                                )
                                Text(
                                    text = "Steps Done",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CorporateBg)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "+${task.xpReward}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CorporateAccentAmber
                                )
                                Text(
                                    text = "XP Earned",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    // Mood Reflection
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "How are you feeling?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("⚡ Energized", "😌 Centered", "💪 Strong").forEach { mood ->
                                val isSelected = selectedMood == mood
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) CorporateAccentBlue.copy(alpha = 0.2f) else CorporateBg)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) CorporateAccentBlue else CorporateCardBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        SoundEffectManager.playPop()
                                        selectedMood = mood
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mood,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) CorporateAccentBlue else TextSecondary
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        showCelebrationModal = false
                        onComplete()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CorporateAccentBlue,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Claim Reward & Return to Oasis",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
}
