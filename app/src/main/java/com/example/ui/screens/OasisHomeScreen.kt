package com.example.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.R
import com.example.audio.SoundEffectManager
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
    onGoToPrioritize: () -> Unit = {},
    onDeleteChore: (Long) -> Unit = {},
    onAddQuickHabit: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val userName = userProfile?.username?.split(" ")?.firstOrNull() ?: "Alex"
    val streak = meta?.currentStreak ?: 5

    var searchQuery by remember { mutableStateOf("") }
    var selectedChip by remember { mutableStateOf("All") }
    var selectedTaskForDetail by remember { mutableStateOf<FeaturedTask?>(null) }
    var likedTaskIds by remember { mutableStateOf(setOf("task_cycling", "task_alpine_breath")) }
    val featuredTasks = remember { SampleTasksRepository.tasks }
    var completedTaskNotice by remember { mutableStateOf<String?>(null) }

    // Advanced Filter State
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedDifficultyFilter by remember { mutableStateOf("All") }
    var selectedDurationFilter by remember { mutableIntStateOf(0) } // 0 = Any, 15, 30
    var filterFavoritesOnly by remember { mutableStateOf(false) }
    var goalStatusFilter by remember { mutableStateOf("All") } // "All", "Active", "Done"
    var choreStepsCompleted by remember { mutableStateOf(mapOf<Long, Set<Int>>()) }

    val isFilterActive = selectedDifficultyFilter != "All" || selectedDurationFilter != 0 || filterFavoritesOnly

    // Filter featured activities
    val filteredFeaturedTasks = remember(
        featuredTasks, searchQuery, selectedChip,
        selectedDifficultyFilter, selectedDurationFilter, filterFavoritesOnly, likedTaskIds
    ) {
        featuredTasks.filter { task ->
            val matchesSearch = searchQuery.isBlank() ||
                task.title.contains(searchQuery, ignoreCase = true) ||
                task.shortDescription.contains(searchQuery, ignoreCase = true) ||
                task.difficulty.contains(searchQuery, ignoreCase = true) ||
                task.locationOrTag.contains(searchQuery, ignoreCase = true)

            val matchesDifficulty = selectedDifficultyFilter == "All" ||
                task.difficulty.equals(selectedDifficultyFilter, ignoreCase = true)

            val matchesDuration = when (selectedDurationFilter) {
                15 -> task.durationMinutes <= 15
                30 -> task.durationMinutes <= 30
                else -> true
            }

            val matchesFav = (!filterFavoritesOnly) || likedTaskIds.contains(task.id)

            matchesSearch && matchesDifficulty && matchesDuration && matchesFav
        }
    }

    // Filter routines based on selected chip / search / goal status
    val routineChecks = remember(coreFive, searchQuery, selectedChip, goalStatusFilter) {
        val baseList = if (coreFive.isNotEmpty()) coreFive else listOf(
            ChoreItem(
                id = 9998L,
                text = "Organize workspace and review backlog",
                iconCategory = "ORGANIZING",
                postedBy = userName,
                difficulty = TaskDifficulty.EASY,
                isCompleted = false
            ),
            ChoreItem(
                id = 9999L,
                text = "Review daily objectives and blockers",
                iconCategory = "PLANNING",
                postedBy = userName,
                difficulty = TaskDifficulty.EASY,
                isCompleted = false
            )
        )

        baseList.filter { chore ->
            val matchesSearch = searchQuery.isBlank() ||
                chore.text.contains(searchQuery, ignoreCase = true) ||
                chore.iconCategory.contains(searchQuery, ignoreCase = true) ||
                chore.postedBy.contains(searchQuery, ignoreCase = true)

            val matchesStatus = when (goalStatusFilter) {
                "Active" -> !chore.isCompleted
                "Done" -> chore.isCompleted
                else -> true
            }

            matchesSearch && matchesStatus
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CorporateBg)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        flingBehavior = rememberZenSpringFlingBehavior()
    ) {
        // ==========================================
        // 1. THE NAME & HEADER (Matching Reference Design)
        // ==========================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Greeting & Avatar Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hi, $userName 👋",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Explore your focus & daily rhythm",
                            fontSize = 14.sp,
                            color = TextMuted
                        )
                    }

                    // Sleek Circular Profile Avatar
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(CorporatePrimaryLight)
                            .border(1.5.dp, CorporateCardBorder, CircleShape)
                            .clickable {
                                SoundEffectManager.playPop()
                                onGoToParadise()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userProfile?.avatarEmoji ?: "👤",
                            fontSize = 22.sp
                        )
                    }
                }

                // Ambient Zen Rhythm & Energy Status
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF131722), Color(0xFF19202E))
                            )
                        )
                        .border(
                            1.dp,
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00E5FF).copy(alpha = 0.35f), Color(0xFF8B5CF6).copy(alpha = 0.20f))
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CorporateAccentTeal)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Circadian Peak Flow ⚡",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Optimal energy zone • High mental clarity",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(CorporateAccentTeal.copy(alpha = 0.15f))
                                .border(1.dp, CorporateAccentTeal.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .clickable {
                                    SoundEffectManager.playPop()
                                    onOpenTutorial("zen_quick_breath")
                                }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Spa,
                                    contentDescription = null,
                                    tint = CorporateAccentTeal,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Zen Reset",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CorporateAccentTeal
                                )
                            }
                        }
                    }
                }

                // Rounded Search Bar with Live Clear & Filter Icon
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(SearchBarBg)
                        .border(
                            1.dp,
                            if (searchQuery.isNotEmpty()) CorporateAccentBlue.copy(alpha = 0.5f) else SearchBarBorder,
                            RoundedCornerShape(26.dp)
                        )
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (searchQuery.isNotEmpty()) CorporateAccentBlue else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        // Text input representation
                        Box(modifier = Modifier.weight(1f)) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search routines, goals, activities...",
                                    fontSize = 14.sp,
                                    color = TextMuted
                                )
                            }
                            androidx.compose.foundation.text.BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Clear "✕" button when typing
                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = TextMuted,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable {
                                        SoundEffectManager.playPop()
                                        searchQuery = ""
                                    }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        // Subtle Vertical Divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(CorporateCardBorder)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Tune / Filter Icon with Active Badge
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filter",
                                tint = if (isFilterActive) CorporateAccentAmber else TextSecondary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        SoundEffectManager.playPop()
                                        showFilterDialog = true
                                    }
                            )
                            if (isFilterActive) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(CorporateAccentAmber)
                                )
                            }
                        }
                    }
                }

                // Quick Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val chips = listOf("All", "Routines", "Squad Goals", "Zen")
                    chips.forEach { chip ->
                        val isSelected = (selectedChip == chip)
                        Box(
                            modifier = Modifier
                                .springPress(pressScale = 0.94f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) {
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF2E2E3E), Color(0xFF22222E))
                                        )
                                    } else {
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF181822), Color(0xFF13131A))
                                        )
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0x44FFFFFF) else Color(0x18FFFFFF),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    SoundEffectManager.playPop()
                                    selectedChip = chip
                                }
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chip,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextMuted
                            )
                        }
                    }
                }
            }
        }

        // Search Match Result Feedback Banner
        if (searchQuery.isNotBlank()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CorporatePrimaryLight)
                        .border(1.dp, CorporateCardBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Showing ${filteredFeaturedTasks.size} activities & ${routineChecks.size} goals for \"$searchQuery\"",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Clear",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CorporateAccentBlue,
                        modifier = Modifier.clickable { searchQuery = "" }
                    )
                }
            }
        }

        // ==========================================
        // 2. FEATURED ACTIVITIES & TASKS (Matching Screenshot Layout)
        // ==========================================
        if (selectedChip == "All" || selectedChip == "Zen") {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Activities & Tasks",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tap any activity to view steps & start timer",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        Text(
                            text = "${streak}d Streak 🔥",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CorporateAccentAmber
                        )
                    }

                    if (filteredFeaturedTasks.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(CorporateSurface)
                                .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "🔍", fontSize = 28.sp)
                                Text(
                                    text = "No activities matched your search or filters",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                                OutlinedButton(
                                    onClick = {
                                        searchQuery = ""
                                        selectedDifficultyFilter = "All"
                                        selectedDurationFilter = 0
                                        filterFavoritesOnly = false
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Reset Filters", fontSize = 12.sp)
                                }
                            }
                        }
                    } else {
                        // Horizontal Scrolling Visual Task Cards
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(end = 10.dp),
                            flingBehavior = rememberZenSpringFlingBehavior()
                        ) {
                            items(filteredFeaturedTasks, key = { it.id }) { task ->
                                Box(
                                    modifier = Modifier.animateItem(
                                        fadeInSpec = ZenSpringSpecs.ItemFade,
                                        fadeOutSpec = ZenSpringSpecs.ItemFade,
                                        placementSpec = ZenSpringSpecs.ItemPlacement
                                    )
                                ) {
                                    FeaturedActivityCard(
                                        task = task,
                                        isLiked = likedTaskIds.contains(task.id),
                                        onToggleLike = {
                                            SoundEffectManager.playPop()
                                            likedTaskIds = if (likedTaskIds.contains(task.id)) {
                                                likedTaskIds - task.id
                                            } else {
                                                likedTaskIds + task.id
                                            }
                                        },
                                        onClick = {
                                            SoundEffectManager.playPop()
                                            selectedTaskForDetail = task
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2b. QUICK MICRO-HABITS LAYER (1-Tap Add)
        // Below Daily Activities & Tasks
        // ==========================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Micro-Habits",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Text(
                        text = "1-Tap Add ⚡",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 8.dp),
                    flingBehavior = rememberZenSpringFlingBehavior()
                ) {
                    val quickHabits = listOf(
                        Triple("💧 Drink 500ml Water", "HEALTH", "Hydration"),
                        Triple("🧘 Posture Reset (5m)", "WELLNESS", "Posture"),
                        Triple("🚶 10m Outdoor Stroll", "FITNESS", "Walk"),
                        Triple("📥 Clear 5 Priority Emails", "ORGANIZING", "Focus"),
                        Triple("📖 Read 5 Book Pages", "LEARNING", "Mindset"),
                        Triple("🌿 3-Minute Box Breathing", "WELLNESS", "Breath")
                    )

                    items(quickHabits, key = { it.first }) { (habitText, category, _) ->
                        Box(
                            modifier = Modifier
                                .animateItem(
                                    fadeInSpec = ZenSpringSpecs.ItemFade,
                                    fadeOutSpec = ZenSpringSpecs.ItemFade,
                                    placementSpec = ZenSpringSpecs.ItemPlacement
                                )
                                .springPress(pressScale = 0.94f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF1E202B), Color(0xFF14151E))
                                    )
                                )
                                .border(
                                    1.dp,
                                    Brush.verticalGradient(
                                        listOf(Color(0x35FFFFFF), Color(0x0CFFFFFF))
                                    ),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    SoundEffectManager.playPop()
                                    onAddQuickHabit(habitText, category)
                                    completedTaskNotice = "Added '$habitText' to your goals!"
                                }
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                        ) {
                            Text(
                                text = habitText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. DAILY GOALS (Interactive Checklist)
        // ==========================================
        if (selectedChip == "All" || selectedChip == "Routines") {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Goals",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Core operational checklist for today",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val completedCount = coreFive.count { it.isCompleted }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (completedCount == coreFive.size && coreFive.isNotEmpty()) CorporateSuccessLight else CorporateSurface)
                                    .border(
                                        1.dp,
                                        if (completedCount == coreFive.size && coreFive.isNotEmpty()) CorporateSuccess.copy(alpha = 0.4f) else CorporateCardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$completedCount / ${coreFive.size} Done",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (completedCount == coreFive.size && coreFive.isNotEmpty()) CorporateSuccess else TextSecondary
                                )
                            }

                            // Add Action Button
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(CorporatePrimaryLight)
                                    .border(1.dp, CorporateCardBorder, CircleShape)
                                    .clickable {
                                        SoundEffectManager.playPop()
                                        onOpenThoughtBubble()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Goal",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Pairwise Rhythm Prioritizer Quick Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .springPress(pressScale = 0.98f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF141A29), Color(0xFF191F33))
                                )
                            )
                            .border(
                                1.dp,
                                Brush.horizontalGradient(
                                    listOf(CorporateAccentBlue.copy(alpha = 0.5f), Color(0x15FFFFFF))
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                SoundEffectManager.playPop()
                                onGoToPrioritize()
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
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
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(CorporateAccentBlue.copy(alpha = 0.2f))
                                        .border(1.dp, CorporateAccentBlue.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = CorporateAccentBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Prioritize Daily Rhythm",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(CorporateAccentTeal.copy(alpha = 0.2f))
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "Pairwise Elo",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CorporateAccentTeal
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Select between 2 options to organize today's schedule",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CorporateAccentBlueLight)
                                    .border(1.dp, CorporateAccentBlue.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 9.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "Sort ➔",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CorporateAccentBlue
                                )
                            }
                        }
                    }

                    // Goal Status Sub-Filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val activeCount = coreFive.count { !it.isCompleted }
                        val doneCount = coreFive.count { it.isCompleted }

                        listOf(
                            "All (${coreFive.size})" to "All",
                            "Active ($activeCount)" to "Active",
                            "Done ($doneCount)" to "Done"
                        ).forEach { (label, key) ->
                            val isSelected = goalStatusFilter == key
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) CorporatePrimaryLight else CorporateSurface)
                                    .border(
                                        1.dp,
                                        if (isSelected) CorporateAccentBlue.copy(alpha = 0.4f) else CorporateCardBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        SoundEffectManager.playPop()
                                        goalStatusFilter = key
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextMuted
                                )
                            }
                        }
                    }

                    // Checklist Cards
                    if (routineChecks.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(CorporateSurface)
                                .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (goalStatusFilter == "Done") "No completed goals yet. Check off items above!" else "No goals in this view.",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            if (routineChecks.isNotEmpty()) {
                items(routineChecks, key = { it.id }) { chore ->
                    val choreSteps = getStepsForGoal(chore.text, chore.iconCategory)
                    val completedIndices = choreStepsCompleted[chore.id]
                        ?: if (chore.isCompleted) choreSteps.indices.toSet() else emptySet()

                    Box(
                        modifier = Modifier.animateItem(
                            fadeInSpec = ZenSpringSpecs.ItemFade,
                            fadeOutSpec = ZenSpringSpecs.ItemFade,
                            placementSpec = ZenSpringSpecs.ItemPlacement
                        )
                    ) {
                        DarkRoutineCheckCard(
                            chore = chore,
                            completedStepIndices = completedIndices,
                            onToggleStep = { stepIdx, totalSteps ->
                                SoundEffectManager.playPop()
                                val current = choreStepsCompleted[chore.id]
                                    ?: if (chore.isCompleted) (0 until totalSteps).toSet() else emptySet()
                                val updated = if (current.contains(stepIdx)) current - stepIdx else current + stepIdx
                                choreStepsCompleted = choreStepsCompleted + (chore.id to updated)
                                if (updated.size == totalSteps && !chore.isCompleted) {
                                    onCompleteChore(chore, Offset.Zero, Offset.Zero)
                                    completedTaskNotice = "All steps complete! +${chore.difficulty.xp} XP 🌟"
                                } else if (updated.size < totalSteps && chore.isCompleted) {
                                    onCompleteChore(chore, Offset.Zero, Offset.Zero)
                                }
                            },
                            onToggle = {
                                val steps = getStepsForGoal(chore.text, chore.iconCategory)
                                if (!chore.isCompleted) {
                                    choreStepsCompleted = choreStepsCompleted + (chore.id to steps.indices.toSet())
                                } else {
                                    choreStepsCompleted = choreStepsCompleted + (chore.id to emptySet())
                                }
                                onCompleteChore(chore, Offset.Zero, Offset.Zero)
                            },
                            onOpenProof = { onOpenPhotoProof(chore) },
                            onOpenTutorial = { chore.tutorialId?.let { onOpenTutorial(it) } },
                            onDelete = {
                                onDeleteChore(chore.id)
                                completedTaskNotice = "Removed '${chore.text}'"
                            }
                        )
                    }
                }
            }
        }

        // ==========================================
        // 4. SQUAD GOALS (Shared Team Milestones)
        // ==========================================
        if (selectedChip == "All" || selectedChip == "Squad Goals") {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Squad Goals",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
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

                    // Sleek Dark Squad Goals Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .springPress(pressScale = 0.985f)
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF1C1E28), Color(0xFF13141C))
                                )
                            )
                            .border(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(Color(0x35FFFFFF), Color(0x0CFFFFFF))
                                ),
                                RoundedCornerShape(22.dp)
                            )
                            .padding(18.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // Overall Progress Bar
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
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { 0.75f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = CorporateAccentBlue,
                                    trackColor = CorporatePrimaryLight
                                )
                            }

                            Divider(color = CorporateCardBorder, thickness = 0.8.dp)

                            // Squad Milestone Rows
                            DarkSquadGoalRow(
                                title = "Complete daily routine checks across team",
                                target = "Team Target",
                                isDone = true
                            )
                            DarkSquadGoalRow(
                                title = "Execute deep work focus blocks",
                                target = "All Members",
                                isDone = true
                            )
                            DarkSquadGoalRow(
                                title = "Conduct peer operational review",
                                target = userName,
                                isDone = true
                            )
                            DarkSquadGoalRow(
                                title = "Align on sprint deliverables & backlog",
                                target = "Pending Sync",
                                isDone = false
                            )

                            // Sync / Collaborate Action
                            Button(
                                onClick = {
                                    SoundEffectManager.playPop()
                                    onOpenSquadRoom()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CorporatePrimaryLight
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Collaborate with Squad",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Filter Options Dialog Bottom Sheet
    if (showFilterDialog) {
        Dialog(onDismissRequest = { showFilterDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CorporateSurface)
                    .border(1.dp, CorporateCardBorder, RoundedCornerShape(24.dp))
                    .padding(22.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filter Activities",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { showFilterDialog = false }
                        )
                    }

                    // Difficulty Selection
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Difficulty Level",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("All", "Gentle", "Beginner", "Moderate", "Challenging").forEach { diff ->
                                val isSelected = selectedDifficultyFilter.equals(diff, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) CorporateAccentBlue.copy(alpha = 0.2f) else CorporateBg)
                                        .border(
                                            1.dp,
                                            if (isSelected) CorporateAccentBlue else CorporateCardBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            SoundEffectManager.playPop()
                                            selectedDifficultyFilter = diff
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = diff,
                                        fontSize = 10.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) CorporateAccentBlue else TextMuted
                                    )
                                }
                            }
                        }
                    }

                    // Duration Filter
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Max Duration",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(0 to "Any Time", 15 to "≤ 15 Mins", 30 to "≤ 30 Mins").forEach { (duration, label) ->
                                val isSelected = selectedDurationFilter == duration
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) CorporateAccentBlue.copy(alpha = 0.2f) else CorporateBg)
                                        .border(
                                            1.dp,
                                            if (isSelected) CorporateAccentBlue else CorporateCardBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            SoundEffectManager.playPop()
                                            selectedDurationFilter = duration
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) CorporateAccentBlue else TextMuted
                                    )
                                }
                            }
                        }
                    }

                    // Favorites Only Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Favorites Only ❤️",
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Switch(
                            checked = filterFavoritesOnly,
                            onCheckedChange = {
                                SoundEffectManager.playPop()
                                filterFavoritesOnly = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CoralIndicator
                            )
                        )
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                SoundEffectManager.playPop()
                                selectedDifficultyFilter = "All"
                                selectedDurationFilter = 0
                                filterFavoritesOnly = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Reset", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                SoundEffectManager.playPop()
                                showFilterDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CorporateAccentBlue)
                        ) {
                            Text("Apply Filters", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Task Detail View & Active Guided Session Modal (Matching Reference Design)
    selectedTaskForDetail?.let { task ->
        TaskDetailModal(
            task = task,
            onDismiss = { selectedTaskForDetail = null },
            onCompleteTask = { completed ->
                selectedTaskForDetail = null
                completedTaskNotice = "Completed ${completed.title}! +${completed.xpReward} XP awarded 🎉"
            }
        )
    }

    // Celebratory Achievement Banner
    completedTaskNotice?.let { notice ->
        LaunchedEffect(notice) {
            delay(3500L)
            completedTaskNotice = null
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp, start = 20.dp, end = 20.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CorporateSuccessLight)
                    .border(1.dp, CorporateSuccess, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = CorporateSuccess,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = notice,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Modern visual activity card with difficulty badge, rich photography, and glass overlay
 * Matches the user's reference design with cycling, walking, and stretching tasks
 */
@Composable
private fun FeaturedActivityCard(
    task: FeaturedTask,
    isLiked: Boolean,
    onToggleLike: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(225.dp)
            .height(310.dp)
            .springPress(pressScale = 0.97f)
            .clip(RoundedCornerShape(26.dp))
            .background(CorporateSurface)
            .clickable(onClick = onClick)
    ) {
        // Background Image
        Image(
            painter = painterResource(id = task.imageRes),
            contentDescription = task.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle gradient overlay from top to bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.25f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.75f)
                        )
                    )
                )
        )

        // Floating Glass Heart Button on Top Right (Matching Screenshot)
        val heartScale = remember { androidx.compose.animation.core.Animatable(1f) }
        val coroutineScope = rememberCoroutineScope()
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp)
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.50f))
                .clickable {
                    coroutineScope.launch {
                        heartScale.animateTo(1.35f, androidx.compose.animation.core.tween(100))
                        heartScale.animateTo(
                            1f,
                            androidx.compose.animation.core.spring(dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy)
                        )
                    }
                    onToggleLike()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isLiked) CoralIndicator else Color.White,
                modifier = Modifier
                    .size(18.dp)
                    .scale(heartScale.value)
            )
        }

        // Frosted Glass Content Container at the Bottom (Matching Screenshot)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF141419).copy(alpha = 0.90f))
                .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(18.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.title,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Difficulty Badge (Replacing stars as requested!)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CorporatePrimaryLight)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Difficulty",
                            tint = when (task.difficulty.lowercase()) {
                                "gentle", "beginner", "easy" -> CorporateSuccess
                                "moderate" -> CorporateAccentBlue
                                else -> CorporateAccentAmber
                            },
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = task.difficulty,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${task.locationOrTag} • ${task.durationLabel}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = task.shortDescription,
                    fontSize = 10.5.sp,
                    color = TextSecondary,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Generates small, nice, bite-sized actionable steps for daily goals
 */
private fun getStepsForGoal(text: String, category: String): List<String> {
    val clean = text.trim()
    return when {
        clean.contains("workspace", ignoreCase = true) || clean.contains("backlog", ignoreCase = true) -> listOf(
            "Clear desk surface & remove clutter",
            "Group open browser tabs & close unused windows",
            "Prioritize top 3 urgent backlog items"
        )
        clean.contains("objectives", ignoreCase = true) || clean.contains("blockers", ignoreCase = true) -> listOf(
            "Write top 3 outcomes for today",
            "Flag potential blockers early to team",
            "Timebox focus blocks on calendar"
        )
        clean.contains("review", ignoreCase = true) -> listOf(
            "Inspect requirements & task deliverables",
            "Leave clear, constructive feedback notes",
            "Sign off & approve milestone"
        )
        clean.contains("architecture", ignoreCase = true) || clean.contains("notes", ignoreCase = true) -> listOf(
            "Draft key decisions & technical trade-offs",
            "Update system architecture diagram",
            "Publish notes to squad knowledge base"
        )
        clean.contains("standup", ignoreCase = true) -> listOf(
            "Review progress since last check-in",
            "Prepare 2-min sync on blockers & next focus",
            "Align on team sprint deliverables"
        )
        clean.contains("Water", ignoreCase = true) || clean.contains("Hydrat", ignoreCase = true) -> listOf(
            "Fill glass or flask to 500ml",
            "Sip slowly while taking 3 deep breaths",
            "Log hydration milestone"
        )
        clean.contains("Posture", ignoreCase = true) -> listOf(
            "Roll shoulders backward 5 times",
            "Adjust monitor to eye level & sit tall",
            "Hold chest stretch and release jaw tension"
        )
        clean.contains("Stroll", ignoreCase = true) || clean.contains("Walk", ignoreCase = true) -> listOf(
            "Step outside into daylight",
            "Brisk 5-minute outbound walking pace",
            "5-minute mindful return loop & fresh air"
        )
        clean.contains("Email", ignoreCase = true) || clean.contains("Inbox", ignoreCase = true) -> listOf(
            "Scan and filter priority inbox messages",
            "Reply or archive top 5 urgent emails",
            "Mute non-essential notifications for focus"
        )
        clean.contains("Book", ignoreCase = true) || clean.contains("Read", ignoreCase = true) -> listOf(
            "Open selected book or chapter",
            "Read 5 pages with full immersion",
            "Jot down 1 actionable insight or reflection"
        )
        clean.contains("Breathing", ignoreCase = true) || clean.contains("Breath", ignoreCase = true) -> listOf(
            "Inhale deeply through nose for 4 seconds",
            "Hold breath gently for 4 seconds",
            "Exhale smoothly through mouth for 4 seconds"
        )
        clean.contains("Stretch", ignoreCase = true) -> listOf(
            "Neck tilts and shoulder rolls (1 min)",
            "Hamstring & lower back gentle stretch (2 mins)",
            "Torso twists and deep breathing (2 mins)"
        )
        clean.contains("Cycling", ignoreCase = true) || clean.contains("Bike", ignoreCase = true) -> listOf(
            "Check tire pressure and seat height",
            "5-minute easy warm-up cadence",
            "Steady tempo ride & cool-down"
        )
        else -> listOf(
            "Step 1: Set up focus environment & tools",
            "Step 2: Complete core action deliverables",
            "Step 3: Review quality & finalize task"
        )
    }
}

/**
 * Modern dark card for daily checklist items featuring small, nice steps
 */
@Composable
private fun DarkRoutineCheckCard(
    chore: ChoreItem,
    completedStepIndices: Set<Int>,
    onToggleStep: (Int, Int) -> Unit,
    onToggle: () -> Unit,
    onOpenProof: () -> Unit,
    onOpenTutorial: () -> Unit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val steps = remember(chore.text, chore.iconCategory) {
        getStepsForGoal(chore.text, chore.iconCategory)
    }
    val allStepsDone = steps.isNotEmpty() && steps.indices.all { completedStepIndices.contains(it) }
    val isDone = chore.isCompleted || allStepsDone
    val completedCount = if (chore.isCompleted) steps.size else completedStepIndices.size.coerceAtMost(steps.size)
    val progress = if (steps.isNotEmpty()) completedCount.toFloat() / steps.size else 0f

    var isExpanded by remember { mutableStateOf(true) }

    val checkBounce by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isDone) 1.20f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow
        ),
        label = "checkBounce"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .springPress(pressScale = 0.985f)
            .animateContentSize(animationSpec = ZenSpringSpecs.Expansion)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1C1E26), Color(0xFF14151B))
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        if (isDone) CorporateSuccess.copy(alpha = 0.5f) else Color(0x30FFFFFF),
                        Color(0x0CFFFFFF)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Main Header Row: Checkbox, Title, Category Badge, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Checkbox with Smooth Visual State
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .scale(checkBounce)
                        .clip(CircleShape)
                        .background(if (isDone) CorporateSuccess else Color.Transparent)
                        .border(
                            width = 1.5.dp,
                            color = if (isDone) CorporateSuccess else TextDisabled,
                            shape = CircleShape
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
                            contentDescription = "Done",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chore.text,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDone) TextMuted else Color.White,
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CorporatePrimaryLight)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = chore.iconCategory.lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }

                    if (onDelete != null) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove goal",
                            tint = TextMuted,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {
                                    SoundEffectManager.playPop()
                                    onDelete()
                                }
                        )
                    }
                }
            }

            // Sub-info Row: XP, Author, Proof / Guide tags, and Step Counter pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+${chore.difficulty.xp} XP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CorporateAccentAmber
                    )

                    Text(
                        text = "• ${chore.postedBy}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    if (chore.requiresPhotoProof) {
                        Text(
                            text = if (chore.isVerified) "• Verified" else "• Proof Needed",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (chore.isVerified) CorporateSuccess else CorporateAccentAmber,
                            modifier = Modifier.clickable { onOpenProof() }
                        )
                    }

                    if (chore.tutorialId != null) {
                        Text(
                            text = "• Guide",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = CorporateAccentBlue,
                            modifier = Modifier.clickable { onOpenTutorial() }
                        )
                    }
                }

                // Step count pill / Expand Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDone) CorporateSuccessLight else CorporatePrimaryLight)
                        .clickable { isExpanded = !isExpanded }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "$completedCount/${steps.size} steps",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDone) CorporateSuccess else CorporateAccentBlue
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = if (isDone) CorporateSuccess else TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Sleek Mini Linear Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.5.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (isDone) CorporateSuccess else CorporateAccentBlue,
                trackColor = CorporateCardBorder
            )

            // Small and Nice Steps Container
            if (isExpanded && steps.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CorporateBg.copy(alpha = 0.65f))
                        .border(1.dp, CorporateCardBorder.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    steps.forEachIndexed { index, stepText ->
                        val stepCompleted = chore.isCompleted || completedStepIndices.contains(index)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onToggleStep(index, steps.size)
                                }
                                .padding(vertical = 4.dp, horizontal = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Small Circular Checkbox for the Step
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(if (stepCompleted) CorporateSuccess else Color.Transparent)
                                    .border(
                                        width = 1.2.dp,
                                        color = if (stepCompleted) CorporateSuccess else TextDisabled,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (stepCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Done",
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = stepText,
                                fontSize = 12.5.sp,
                                fontWeight = if (stepCompleted) FontWeight.Normal else FontWeight.Medium,
                                color = if (stepCompleted) TextMuted else Color.White.copy(alpha = 0.9f),
                                textDecoration = if (stepCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Modern dark squad goal item
 */
@Composable
private fun DarkSquadGoalRow(
    title: String,
    target: String,
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
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isDone) CorporateSuccessLight else CorporatePrimaryLight)
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
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                color = if (isDone) TextMuted else Color.White,
                textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = target,
            fontSize = 11.sp,
            color = TextMuted
        )
    }
}
