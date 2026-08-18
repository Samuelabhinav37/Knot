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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.SoundEffectManager
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
                PastelOasisApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PastelOasisApp(viewModel: OasisViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val meta by viewModel.oasisMeta.collectAsStateWithLifecycle()
    val coreFive by viewModel.coreFiveChores.collectAsStateWithLifecycle()
    val allChores by viewModel.allChores.collectAsStateWithLifecycle()
    val members by viewModel.allMembers.collectAsStateWithLifecycle()
    val pets by viewModel.allPets.collectAsStateWithLifecycle()
    val badges by viewModel.allBadges.collectAsStateWithLifecycle()
    val tutorials by viewModel.allTutorials.collectAsStateWithLifecycle()
    val matchup by viewModel.currentMatchup.collectAsStateWithLifecycle()
    val latestPet by viewModel.latestHatchedPet.collectAsStateWithLifecycle()
    val activeBuddyPet by viewModel.activeBuddyPet.collectAsStateWithLifecycle()
    val taskFilter by viewModel.taskFilter.collectAsStateWithLifecycle()

    val currentSeason by viewModel.currentSeason.collectAsStateWithLifecycle()
    val currentTimeTheme by viewModel.currentTimeOfDayTheme.collectAsStateWithLifecycle()

    val confettiTrigger by viewModel.confettiTrigger.collectAsStateWithLifecycle()
    val sparkTrigger by viewModel.sparkTrigger.collectAsStateWithLifecycle()
    val sparkStartPos by viewModel.sparkStartPos.collectAsStateWithLifecycle()
    val sparkTargetPos by viewModel.sparkTargetPos.collectAsStateWithLifecycle()

    val showThoughtBubbleDialog by viewModel.showThoughtBubbleDialog.collectAsStateWithLifecycle()
    val showSquadDialog by viewModel.showSquadDialog.collectAsStateWithLifecycle()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val showTourDialog by viewModel.showTourDialog.collectAsStateWithLifecycle()
    val showBadgeGalleryDialog by viewModel.showBadgeGalleryDialog.collectAsStateWithLifecycle()
    val showPhotoProofDialog by viewModel.showPhotoProofDialog.collectAsStateWithLifecycle()
    val activeProofChore by viewModel.activeProofChore.collectAsStateWithLifecycle()
    val showTutorialDialog by viewModel.showTutorialDialog.collectAsStateWithLifecycle()
    val activeTutorial by viewModel.activeTutorial.collectAsStateWithLifecycle()

    // 1. First-time Launch Onboarding Experience
    if (userProfile == null || !userProfile!!.isOnboarded) {
        OnboardingFlowScreen(
            onCompleteOnboarding = { username, gender, avatar, archetype, interests, squadCode, initialGoal ->
                viewModel.completeOnboarding(username, gender, avatar, archetype, interests, squadCode, initialGoal)
            }
        )
        return
    }

    // Main App with Neutral Corporate Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CorporateBg)
    ) {
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
                when (selectedTab) {
                    0 -> OasisHomeScreen(
                        meta = meta,
                        userProfile = userProfile,
                        currentSeason = currentSeason,
                        coreFive = coreFive,
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
                        onGoToParadise = { viewModel.setSelectedTab(3) }
                    )
                    1 -> PairwiseSortScreen(
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

        // Dialog: Squad & Co-op Room
        if (showSquadDialog) {
            SquadRoomDialog(
                members = members,
                userProfile = userProfile,
                onDismiss = { viewModel.closeSquadDialog() },
                onJoinSquad = { code -> viewModel.joinSquadByCode(code) },
                onToggleMode = { mode -> viewModel.toggleSquadSoloMode(mode) }
            )
        }

        // Dialog: Photo Proof of Work
        if (showPhotoProofDialog && activeProofChore != null) {
            PhotoProofDialog(
                chore = activeProofChore!!,
                onDismiss = { viewModel.closePhotoProofDialog() },
                onSubmitProof = { beforeUri, afterUri ->
                    viewModel.submitProofOfWork(activeProofChore!!.id, beforeUri, afterUri)
                }
            )
        }

        // Dialog: 1-Min Interactive Skill Tutorial
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
                }
            )
        }

        // Dialog: Interactive UI Tour
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
            .background(CorporateSurface)
            .border(1.dp, CorporateCardBorder)
            .padding(horizontal = 16.dp, vertical = 6.dp)
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
                onClick = { onSelectTab(0) }
            )
            CorporateNavTab(
                title = "Cadre",
                icon = Icons.Default.Groups,
                isSelected = (selectedTab == 1),
                onClick = { onSelectTab(1) }
            )
            CorporateNavTab(
                title = "Zen",
                icon = Icons.Default.SelfImprovement,
                isSelected = (selectedTab == 2),
                onClick = { onSelectTab(2) }
            )
            CorporateNavTab(
                title = "Account",
                icon = Icons.Default.Person,
                isSelected = (selectedTab == 3),
                onClick = { onSelectTab(3) }
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) CorporateAccentBlueLight else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) CorporateAccentBlue else TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) CorporateAccentBlue else TextMuted
        )
    }
}
