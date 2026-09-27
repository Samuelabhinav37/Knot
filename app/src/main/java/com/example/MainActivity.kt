package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.animation.core.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.SoundEffectManager
import com.example.data.model.TaskDifficulty
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.OasisViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: OasisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CorporateBg
                ) {
                    PastelOasisApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun PastelOasisApp(viewModel: OasisViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val meta by viewModel.oasisMeta.collectAsStateWithLifecycle()
    val allChores by viewModel.allChores.collectAsStateWithLifecycle()
    val coreFive by viewModel.coreFiveChores.collectAsStateWithLifecycle()
    val members by viewModel.allMembers.collectAsStateWithLifecycle()
    val pets by viewModel.allPets.collectAsStateWithLifecycle()
    val badges by viewModel.allBadges.collectAsStateWithLifecycle()
    val tutorials by viewModel.allTutorials.collectAsStateWithLifecycle()
    val latestPet by viewModel.latestHatchedPet.collectAsStateWithLifecycle()
    val activeBuddyPet by viewModel.activeBuddyPet.collectAsStateWithLifecycle()
    val taskFilter by viewModel.taskFilter.collectAsStateWithLifecycle()
    val currentSeason by viewModel.currentSeason.collectAsStateWithLifecycle()

    val showThoughtBubbleDialog by viewModel.showThoughtBubbleDialog.collectAsStateWithLifecycle()
    val showSquadDialog by viewModel.showSquadDialog.collectAsStateWithLifecycle()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val showTourDialog by viewModel.showTourDialog.collectAsStateWithLifecycle()
    val showBadgeGalleryDialog by viewModel.showBadgeGalleryDialog.collectAsStateWithLifecycle()
    val showPhotoProofDialog by viewModel.showPhotoProofDialog.collectAsStateWithLifecycle()
    val activeProofChore by viewModel.activeProofChore.collectAsStateWithLifecycle()
    val showTutorialDialog by viewModel.showTutorialDialog.collectAsStateWithLifecycle()
    val activeTutorial by viewModel.activeTutorial.collectAsStateWithLifecycle()
    val currentMatchup by viewModel.currentMatchup.collectAsStateWithLifecycle()

    var showIntroAnimation by rememberSaveable { mutableStateOf(true) }

    // 0. Cinematic Zen Intro Animation
    if (showIntroAnimation) {
        OasisIntroScreen(
            onFinish = { showIntroAnimation = false }
        )
        return
    }

    // 1. Explicit Onboarding only if explicitly not onboarded
    if (userProfile != null && !userProfile!!.isOnboarded) {
        OnboardingFlowScreen(
            onCompleteOnboarding = { username, gender, avatar, archetype, interests, squadCode, initialGoal ->
                viewModel.completeOnboarding(username, gender, avatar, archetype, interests, squadCode, initialGoal)
            }
        )
        return
    }

    // Dynamic Ambient Atmosphere matching current tab and time of day
    val ambientGlowColor by animateColorAsState(
        targetValue = when (selectedTab) {
            0 -> Color(0xFF0C243B) // Oasis Ocean & Sky Glow
            1 -> Color(0xFF281845) // Twilight Lilac for Pairwise Prioritizer
            2 -> Color(0xFF092E22) // Zen Sage & Mint
            3 -> Color(0xFF321E14) // Warm Sunset Terracotta
            else -> Color(0xFF131522)
        },
        animationSpec = tween(durationMillis = 650, easing = LinearOutSlowInEasing),
        label = "ambientGlowColor"
    )

    // Main App with Dynamic Atmospheric Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CorporateBg)
    ) {
        // Ethereal ambient background glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ambientGlowColor.copy(alpha = 0.5f),
                            ambientGlowColor.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        radius = 1600f
                    )
                )
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                CorporateBottomNavigationBar(
                    selectedTab = selectedTab,
                    onSelectTab = {
                        viewModel.setSelectedTab(it)
                        SoundEffectManager.playPop()
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Fluid Screen Transition with Spring Momentum
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        val isForward = targetState > initialState
                        (slideInHorizontally(
                            initialOffsetX = { width -> if (isForward) (width * 0.28f).toInt() else -(width * 0.28f).toInt() },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeIn(animationSpec = tween(240)))
                        .togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { width -> if (isForward) -(width * 0.22f).toInt() else (width * 0.22f).toInt() },
                                animationSpec = tween(180)
                            ) + fadeOut(animationSpec = tween(180))
                        )
                    },
                    label = "mainTabScreenTransition"
                ) { currentTab ->
                    when (currentTab) {
                        0 -> OasisHomeScreen(
                            meta = meta,
                            userProfile = userProfile,
                            currentSeason = currentSeason,
                            coreFive = allChores,
                            members = members,
                            latestPet = latestPet ?: activeBuddyPet,
                            taskFilter = taskFilter,
                            onSelectTaskFilter = { viewModel.setTaskFilter(it) },
                            onTapEgg = { viewModel.onEggTapped() },
                            onCompleteChore = { chore, clickPos, eggPos ->
                                viewModel.completeCoreChore(chore, clickPos, eggPos)
                            },
                            onOpenPhotoProof = { chore -> viewModel.openPhotoProofDialog(chore) },
                            onOpenTutorial = { tutId -> viewModel.openTutorialDialog(tutId) },
                            onSwitchMember = { viewModel.switchActiveMember(it) },
                            onOpenThoughtBubble = { viewModel.openThoughtBubbleDialog() },
                            onOpenSquadRoom = { viewModel.openSquadDialog() },
                            onOpenBadges = { viewModel.openBadgeGalleryDialog() },
                            onOpenSettings = { viewModel.openSettingsDialog() },
                            onStartNewEgg = { viewModel.startNewEggIncubation() },
                            onToggleTimer = { viewModel.toggleTimer() },
                            onResetTimer = { viewModel.resetTimer() },
                            onGoToParadise = { viewModel.setSelectedTab(3) },
                            onGoToPrioritize = { viewModel.setSelectedTab(1) },
                            onDeleteChore = { choreId -> viewModel.deleteChore(choreId) },
                            onAddQuickHabit = { text, category ->
                                val activeUser = userProfile?.username?.split(" ")?.firstOrNull() ?: "Alex"
                                viewModel.addCustomChore(text, category, activeUser, TaskDifficulty.EASY, false, true)
                            }
                        )
                        1 -> PairwiseSortScreen(
                            chores = allChores,
                            currentMatchup = currentMatchup,
                            onChooseMatchup = { chosenA -> viewModel.choosePairwiseCard(chosenA) },
                            onTieMatchup = { viewModel.choosePairwiseTie() },
                            onSkipMatchup = { viewModel.skipPairwiseMatchup() },
                            onResetScores = { viewModel.resetPairwiseScores() },
                            onAddChore = { text, category, difficulty ->
                                val activeUser = userProfile?.username?.split(" ")?.firstOrNull() ?: "Alex"
                                viewModel.addCustomChore(text, category, activeUser, difficulty, false, true)
                            },
                            onCompleteChore = { chore ->
                                viewModel.completeCoreChore(chore, androidx.compose.ui.geometry.Offset.Zero, androidx.compose.ui.geometry.Offset.Zero)
                            },
                            userProfile = userProfile,
                            members = members,
                            onJoinSquadWithCode = { code -> viewModel.joinSquadByCode(code) },
                            onJoinMatchedSquad = { squadName, squadCode, matchedMembers ->
                                viewModel.joinMatchedSquad(squadName, squadCode, matchedMembers)
                            }
                        )
                        2 -> SkillsTutorialsScreen(
                            tutorials = tutorials,
                            userProfile = userProfile,
                            onOpenTutorial = { tut -> viewModel.openTutorialDialog(tut.id) }
                        )
                        3 -> PetParadiseScreen(
                            userProfile = userProfile,
                            members = members,
                            pets = pets,
                            badges = badges,
                            currentStreak = meta?.currentStreak ?: 5,
                            onEquipAccessory = { petId, acc -> viewModel.equipPetAccessory(petId, acc) },
                            onFeedTreat = { treat -> viewModel.feedBuddy(treat) },
                            onPetBuddy = { viewModel.petBuddy() },
                            onOpenBadgesGallery = { viewModel.openBadgeGalleryDialog() },
                            onOpenSettings = { viewModel.openSettingsDialog() }
                        )
                    }
                }
            }
        }

        // Dialog: Add Daily Life Task
        if (showThoughtBubbleDialog) {
            AddChoreDialog(
                members = members,
                onDismiss = { viewModel.closeThoughtBubbleDialog() },
                onSubmit = { text, category, postedBy, difficulty, requiresProof, isSolo ->
                    viewModel.addCustomChore(text, category, postedBy, difficulty, requiresProof, isSolo)
                }
            )
        }

        // Dialog: Squad Room
        if (showSquadDialog) {
            SquadRoomDialog(
                members = members,
                userProfile = userProfile,
                onDismiss = { viewModel.closeSquadDialog() },
                onJoinSquad = { code -> viewModel.joinSquadByCode(code) },
                onToggleMode = { mode -> viewModel.toggleSquadSoloMode(mode) }
            )
        }

        // Dialog: Verification Proof of Work
        if (showPhotoProofDialog && activeProofChore != null) {
            PhotoProofDialog(
                chore = activeProofChore!!,
                onDismiss = { viewModel.closePhotoProofDialog() },
                onSubmitProof = { beforeUri, afterUri ->
                    viewModel.submitProofOfWork(activeProofChore!!.id, beforeUri, afterUri)
                }
            )
        }

        // Dialog: Standard Procedure Guide
        if (showTutorialDialog && activeTutorial != null) {
            TutorialDialog(
                tutorial = activeTutorial!!,
                onDismiss = { viewModel.closeTutorialDialog() },
                onCompleteStep = {
                    viewModel.completeTutorialStep(activeTutorial!!.id)
                }
            )
        }

        // Dialog: Badges Showcase & Milestones
        if (showBadgeGalleryDialog) {
            BadgeShowcaseDialog(
                badges = badges,
                onDismiss = { viewModel.closeBadgeGalleryDialog() }
            )
        }

        // Dialog: Settings & Profile Controls
        if (showSettingsDialog) {
            SettingsDialog(
                userProfile = userProfile,
                onDismiss = { viewModel.closeSettingsDialog() },
                onSaveSettings = { season, dayNight, density, archetype, name, avatar ->
                    viewModel.updateSettings(season, dayNight, density, archetype, name, avatar)
                },
                onReplayIntro = { showIntroAnimation = true }
            )
        }

        // Dialog: UI Walkthrough Tour
        if (showTourDialog) {
            UITourDialog(
                onDismiss = { viewModel.closeTourDialog() }
            )
        }
    }
}

@Composable
private fun CorporateBottomNavigationBar(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xF21C1C26), Color(0xF8111116))
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color(0x35FFFFFF), Color(0x0CFFFFFF))
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CorporateNavTab(
                title = "Home",
                icon = Icons.Default.Home,
                isSelected = (selectedTab == 0),
                onClick = {
                    SoundEffectManager.playPop()
                    onSelectTab(0)
                }
            )
            CorporateNavTab(
                title = "Prioritize",
                icon = Icons.Default.SwapVert,
                isSelected = (selectedTab == 1),
                onClick = {
                    SoundEffectManager.playPop()
                    onSelectTab(1)
                }
            )
            CorporateNavTab(
                title = "Zen",
                icon = Icons.Default.SelfImprovement,
                isSelected = (selectedTab == 2),
                onClick = {
                    SoundEffectManager.playPop()
                    onSelectTab(2)
                }
            )
            CorporateNavTab(
                title = "Profile",
                icon = Icons.Default.Person,
                isSelected = (selectedTab == 3),
                onClick = {
                    SoundEffectManager.playPop()
                    onSelectTab(3)
                }
            )
        }
    }
}

@Composable
private fun CorporateNavTab(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "navIconScale"
    )

    val dotScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "dotScale"
    )

    Box(
        modifier = Modifier
            .springPress(pressScale = 0.90f)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) {
                    Brush.verticalGradient(
                        listOf(Color(0xFF2E2E3E), Color(0xFF22222E))
                    )
                } else {
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                }
            )
            .border(
                1.dp,
                if (isSelected) Color(0x33FFFFFF) else Color.Transparent,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else TextMuted,
                modifier = Modifier
                    .size(22.dp)
                    .scale(iconScale)
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Active Glowing Coral/Teal Dot Indicator
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .scale(dotScale)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(if (isSelected) CoralIndicator else Color.Transparent)
            )
        }
    }
}
