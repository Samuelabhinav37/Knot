package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEffectManager
import com.example.data.model.SkillTutorial
import com.example.data.model.UserProfile
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SkillsTutorialsScreen(
    tutorials: List<SkillTutorial>,
    userProfile: UserProfile?,
    onOpenTutorial: (SkillTutorial) -> Unit,
    modifier: Modifier = Modifier
) {
    // Focus timer state
    var selectedPresetMinutes by remember { mutableIntStateOf(25) }
    var totalSeconds by remember { mutableIntStateOf(25 * 60) }
    var secondsRemaining by remember { mutableIntStateOf(25 * 60) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Zen Breathing Guide State
    var isBreathingActive by remember { mutableStateOf(false) }
    var breathPhase by remember { mutableStateOf("Inhale (4s)") }

    // SOP Filter & Expansion States for Spring Movements
    var selectedCategory by remember { mutableStateOf("All") }
    var expandedTutorialId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining -= 1
            if (secondsRemaining <= 0) {
                isTimerRunning = false
                SoundEffectManager.playFanfare()
            }
        }
    }

    // Breathing Cycle Loop
    LaunchedEffect(isBreathingActive) {
        while (isBreathingActive) {
            breathPhase = "Inhale (4s)"
            delay(4000L)
            if (!isBreathingActive) break
            breathPhase = "Hold (4s)"
            delay(4000L)
            if (!isBreathingActive) break
            breathPhase = "Exhale (4s)"
            delay(4000L)
            if (!isBreathingActive) break
            breathPhase = "Hold (4s)"
            delay(4000L)
        }
    }

    val infiniteBreathTransition = rememberInfiniteTransition(label = "breathAnimation")
    val breathScale by infiniteBreathTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathScale"
    )

    val haloAlpha by infiniteBreathTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloAlpha"
    )

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)
    val targetTimerProgress = if (totalSeconds > 0) (totalSeconds - secondsRemaining).toFloat() / totalSeconds.toFloat() else 0f
    val animatedTimerProgress by animateFloatAsState(
        targetValue = targetTimerProgress,
        animationSpec = ZenSpringSpecs.Progress,
        label = "timerProgressSpring"
    )

    // Filtered tutorials for dynamic spring reordering
    val filteredTutorials = remember(tutorials, selectedCategory) {
        if (selectedCategory == "All") {
            tutorials
        } else {
            tutorials.filter {
                it.category.contains(selectedCategory, ignoreCase = true) ||
                it.title.contains(selectedCategory, ignoreCase = true)
            }
        }
    }

    val categories = listOf("All", "Focus", "Mindset", "Systems", "Energy")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CorporateBg)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        flingBehavior = rememberZenSpringFlingBehavior()
    ) {
        // ==========================================
        // 1. ZEN HEADER & AMBIENT RHYTHM PILL
        // ==========================================
        item(key = "zen_header") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(
                        placementSpec = ZenSpringSpecs.ItemPlacement,
                        fadeInSpec = ZenSpringSpecs.ItemFade,
                        fadeOutSpec = ZenSpringSpecs.ItemFade
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Zen",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = (-0.6).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Mindful focus & calm flow",
                            fontSize = 13.5.sp,
                            color = TextMuted
                        )
                    }

                    // Ambient Zen Indicator Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF132E2B), Color(0xFF1A3B37))
                                )
                            )
                            .border(1.dp, CorporateAccentTeal.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(CorporateAccentTeal)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CALM STATE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = CorporateAccentTeal
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. INTERACTIVE ZEN BOX BREATHING CIRCLE
        // ==========================================
        item(key = "zen_breathing_box") {
            Box(
                modifier = Modifier
                    .animateItem(
                        placementSpec = ZenSpringSpecs.ItemPlacement,
                        fadeInSpec = ZenSpringSpecs.ItemFade,
                        fadeOutSpec = ZenSpringSpecs.ItemFade
                    )
                    .springPress(pressScale = 0.98f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1C1E26), Color(0xFF13141B))
                        )
                    )
                    .border(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(Color(0x35FFFFFF), Color(0x0CFFFFFF))
                        ),
                        RoundedCornerShape(22.dp)
                    )
                    .clickable {
                        SoundEffectManager.playPop()
                        isBreathingActive = !isBreathingActive
                    }
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(CorporateAccentTeal.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Spa,
                                    contentDescription = null,
                                    tint = CorporateAccentTeal,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "4-4-4 BOX BREATHING",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = CorporateAccentTeal
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isBreathingActive) breathPhase else "Tap to start breathing reset",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = if (isBreathingActive) "Follow the gentle pulse to calm cortisol" else "Calm your nervous system in 60 seconds",
                            fontSize = 12.5.sp,
                            color = TextMuted
                        )
                    }

                    // Pulsing Visual Breathing Ring with concentric halo
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .scale(if (isBreathingActive) breathScale else 1f),
                        contentAlignment = Alignment.Center
                    ) {
                        // Ambient Halo when active
                        if (isBreathingActive) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                CorporateAccentTeal.copy(alpha = haloAlpha),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isBreathingActive) {
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF133B36), Color(0xFF0F2623))
                                        )
                                    } else {
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF282834), Color(0xFF1C1C24))
                                        )
                                    }
                                )
                                .border(
                                    1.5.dp,
                                    if (isBreathingActive) CorporateAccentTeal else CorporateCardBorder,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBreathingActive) "🌿" else "Start",
                                fontSize = if (isBreathingActive) 22.sp else 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isBreathingActive) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. DEEP FOCUS INTERVAL TIMER
        // ==========================================
        item(key = "zen_focus_timer") {
            Box(
                modifier = Modifier
                    .animateItem(
                        placementSpec = ZenSpringSpecs.ItemPlacement,
                        fadeInSpec = ZenSpringSpecs.ItemFade,
                        fadeOutSpec = ZenSpringSpecs.ItemFade
                    )
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1C1E28), Color(0xFF13141B))
                        )
                    )
                    .border(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(Color(0x35FFFFFF), Color(0x0CFFFFFF))
                        ),
                        RoundedCornerShape(22.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(CorporateAccentBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = CorporateAccentBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DEEP FOCUS INTERVAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = TextMuted
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isTimerRunning) CorporateSuccessLight else CorporateBg)
                                .border(
                                    0.8.dp,
                                    if (isTimerRunning) CorporateSuccess.copy(alpha = 0.4f) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isTimerRunning) "RUNNING" else "STANDBY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp,
                                color = if (isTimerRunning) CorporateSuccess else TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Duration Preset Selectors with Spring Bounce
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(5 to "5m Reset", 10 to "10m Sprint", 25 to "25m Deep Work").forEach { (mins, label) ->
                            val isSelected = (selectedPresetMinutes == mins)
                            val presetScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.04f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                ),
                                label = "presetScale"
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .scale(presetScale)
                                    .springPress(pressScale = 0.94f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) {
                                            Brush.verticalGradient(
                                                listOf(Color(0xFF26334D), Color(0xFF1A2336))
                                            )
                                        } else {
                                            Brush.verticalGradient(
                                                listOf(Color(0xFF191922), Color(0xFF14141A))
                                            )
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) CorporateAccentBlue.copy(alpha = 0.6f) else CorporateCardBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        SoundEffectManager.playPop()
                                        selectedPresetMinutes = mins
                                        totalSeconds = mins * 60
                                        secondsRemaining = mins * 60
                                        isTimerRunning = false
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) CorporateAccentBlue else TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Timer Big Display
                    Text(
                        text = formattedTime,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Spring-Interpolated Progress Track
                    LinearProgressIndicator(
                        progress = { animatedTimerProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = CorporateAccentBlue,
                        trackColor = Color(0xFF14141B)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Timer Controls
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                SoundEffectManager.playPop()
                                isTimerRunning = !isTimerRunning
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTimerRunning) Color(0xFF2E2E3E) else CorporateAccentBlue
                            ),
                            modifier = Modifier
                                .springPress(pressScale = 0.94f)
                                .height(46.dp)
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isTimerRunning) "Pause" else "Start",
                                tint = if (isTimerRunning) Color.White else Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTimerRunning) "Pause" else "Start Focus",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTimerRunning) Color.White else Color.Black
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                SoundEffectManager.playPop()
                                isTimerRunning = false
                                secondsRemaining = selectedPresetMinutes * 60
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .springPress(pressScale = 0.94f)
                                .height(46.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Reset",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 4. STANDARD OPERATING PROCEDURES SECTION
        // ==========================================
        item(key = "sop_section_header") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(
                        placementSpec = ZenSpringSpecs.ItemPlacement,
                        fadeInSpec = ZenSpringSpecs.ItemFade,
                        fadeOutSpec = ZenSpringSpecs.ItemFade
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Standard Operating Procedures",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Structured guides for sustained focus & clarity",
                            fontSize = 12.5.sp,
                            color = TextMuted
                        )
                    }

                    Text(
                        text = "${filteredTutorials.size} guides",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Spring-Interactive Category Filter Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories, key = { it }) { cat ->
                        val isCatSelected = (selectedCategory == cat)
                        val catScale by animateFloatAsState(
                            targetValue = if (isCatSelected) 1.05f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "catScale"
                        )

                        Box(
                            modifier = Modifier
                                .scale(catScale)
                                .springPress(pressScale = 0.94f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isCatSelected) {
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF2C2C3A), Color(0xFF20202A))
                                        )
                                    } else {
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF16161D), Color(0xFF121217))
                                        )
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (isCatSelected) Color(0x44FFFFFF) else CorporateCardBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    SoundEffectManager.playPop()
                                    selectedCategory = cat
                                }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCatSelected) Color.White else TextMuted
                            )
                        }
                    }
                }
            }
        }

        // List of Operating Procedures with Spring-Physics Placement, Fade & Expansion
        items(filteredTutorials, key = { it.id }) { tutorial ->
            val isExpanded = (expandedTutorialId == tutorial.id)

            Box(
                modifier = Modifier
                    .animateItem(
                        placementSpec = ZenSpringSpecs.ItemPlacement,
                        fadeInSpec = ZenSpringSpecs.ItemFade,
                        fadeOutSpec = ZenSpringSpecs.ItemFade
                    )
                    .animateContentSize(animationSpec = ZenSpringSpecs.Expansion)
                    .springPress(pressScale = 0.98f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1C1E26), Color(0xFF14151B))
                        )
                    )
                    .border(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(
                                if (isExpanded) CorporateAccentTeal.copy(alpha = 0.5f) else Color(0x30FFFFFF),
                                Color(0x0CFFFFFF)
                            )
                        ),
                        RoundedCornerShape(18.dp)
                    )
                    .clickable {
                        SoundEffectManager.playPop()
                        expandedTutorialId = if (isExpanded) null else tutorial.id
                    }
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
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
                                        .background(CorporateAccentTealLight)
                                        .border(0.8.dp, CorporateAccentTeal.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = tutorial.category.uppercase(),
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.6.sp,
                                        color = CorporateAccentTeal
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "Est. ${tutorial.estimatedTimeToMaster}",
                                    fontSize = 11.5.sp,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(7.dp))

                            Text(
                                text = tutorial.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = tutorial.subtitle,
                                fontSize = 12.5.sp,
                                color = TextSecondary,
                                lineHeight = 17.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                SoundEffectManager.playPop()
                                onOpenTutorial(tutorial)
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22222E))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View Guide",
                                tint = CorporateAccentTeal,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Accordion Quick Preview Drawer (Spring Expanded)
                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(CorporateCardBorder)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "QUICK TAKEAWAY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Apply this operating procedure during morning focus blocks to establish cognitive baseline and eliminate decision fatigue.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                SoundEffectManager.playPop()
                                onOpenTutorial(tutorial)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CorporateAccentTealLight
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                        ) {
                            Text(
                                text = "Open Full Step-by-Step Guide →",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CorporateAccentTeal
                            )
                        }
                    }
                }
            }
        }
    }
}
