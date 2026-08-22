package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundEffectManager
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.KnotRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class PairwiseMatchup(
    val choreA: ChoreItem,
    val choreB: ChoreItem
)

enum class AppSeason(val displayName: String, val iconEmoji: String) {
    SPRING("Pastel Spring", "🌸"),
    SUMMER("Lush Summer", "☀️"),
    AUTUMN("Golden Autumn", "🍂"),
    WINTER("Cozy Winter", "❄️"),
    FESTIVAL("Knot Gala", "🎉")
}

enum class TimeOfDayTheme(val backgroundColor: Color) {
    DAY(Color(0xFFFFFBEB)),
    SUNSET(Color(0xFFFFF7ED)),
    NIGHT(Color(0xFFF1F5F9))
}

class KnotViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KnotRepository
    private var timerJob: Job? = null

    // Flows from DB
    val allChores: StateFlow<List<ChoreItem>>
    val coreFiveChores: StateFlow<List<ChoreItem>>
    val allMembers: StateFlow<List<GroupMember>>
    val allPets: StateFlow<List<HatchedPet>>
    val activeBuddyPet: StateFlow<HatchedPet?>
    val knotMeta: StateFlow<KnotMeta?>
    val userProfile: StateFlow<UserProfile?>
    val allBadges: StateFlow<List<BadgeItem>>
    val allTutorials: StateFlow<List<SkillTutorial>>

    // UI Transient States
    private val _currentMatchup = MutableStateFlow<PairwiseMatchup?>(null)
    val currentMatchup: StateFlow<PairwiseMatchup?> = _currentMatchup.asStateFlow()

    private val _confettiTrigger = MutableStateFlow(0)
    val confettiTrigger: StateFlow<Int> = _confettiTrigger.asStateFlow()

    private val _sparkTrigger = MutableStateFlow(0)
    val sparkTrigger: StateFlow<Int> = _sparkTrigger.asStateFlow()

    private val _sparkStartPos = MutableStateFlow(Offset.Zero)
    val sparkStartPos: StateFlow<Offset> = _sparkStartPos.asStateFlow()

    private val _sparkTargetPos = MutableStateFlow(Offset.Zero)
    val sparkTargetPos: StateFlow<Offset> = _sparkTargetPos.asStateFlow()

    private val _showThoughtBubbleDialog = MutableStateFlow(false)
    val showThoughtBubbleDialog: StateFlow<Boolean> = _showThoughtBubbleDialog.asStateFlow()

    private val _showPhotoProofDialog = MutableStateFlow(false)
    val showPhotoProofDialog: StateFlow<Boolean> = _showPhotoProofDialog.asStateFlow()

    private val _activeProofChore = MutableStateFlow<ChoreItem?>(null)
    val activeProofChore: StateFlow<ChoreItem?> = _activeProofChore.asStateFlow()

    private val _showTutorialDialog = MutableStateFlow(false)
    val showTutorialDialog: StateFlow<Boolean> = _showTutorialDialog.asStateFlow()

    private val _activeTutorial = MutableStateFlow<SkillTutorial?>(null)
    val activeTutorial: StateFlow<SkillTutorial?> = _activeTutorial.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showSquadDialog = MutableStateFlow(false)
    val showSquadDialog: StateFlow<Boolean> = _showSquadDialog.asStateFlow()

    private val _showBadgeGalleryDialog = MutableStateFlow(false)
    val showBadgeGalleryDialog: StateFlow<Boolean> = _showBadgeGalleryDialog.asStateFlow()

    private val _showTourDialog = MutableStateFlow(false)
    val showTourDialog: StateFlow<Boolean> = _showTourDialog.asStateFlow()

    // 0: Knot Hub, 1: Pairwise Sort, 2: Skills & Guides, 3: Pet Paradise & Buddy
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _latestHatchedPet = MutableStateFlow<HatchedPet?>(null)
    val latestHatchedPet: StateFlow<HatchedPet?> = _latestHatchedPet.asStateFlow()

    // Filter for Home screen (All, Shared Squad Tasks, My Solo Goals)
    private val _taskFilter = MutableStateFlow("ALL")
    val taskFilter: StateFlow<String> = _taskFilter.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = KnotRepository(db.knotDao())

        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.seedIfEmpty(db.knotDao())
        }

        allChores = repository.allChores.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        coreFiveChores = repository.coreFiveChores.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allMembers = repository.allMembers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allPets = repository.allPets.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        activeBuddyPet = repository.activeBuddy.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        knotMeta = repository.knotMeta.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        userProfile = repository.userProfile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        allBadges = repository.allBadges.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allTutorials = repository.allTutorials.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        startTimerCountdownLoop()
        refreshPairwiseMatchup()
    }

    // Dynamic Season and Time of Day Resolver
    val currentSeason: StateFlow<AppSeason> = userProfile.map { profile ->
        when (profile?.seasonOverride) {
            "WINTER" -> AppSeason.WINTER
            "SPRING" -> AppSeason.SPRING
            "SUMMER" -> AppSeason.SUMMER
            "AUTUMN" -> AppSeason.AUTUMN
            "FESTIVAL" -> AppSeason.FESTIVAL
            else -> {
                val month = Calendar.getInstance().get(Calendar.MONTH) // 0-based: 0 = Jan
                when (month) {
                    11, 0, 1 -> AppSeason.WINTER
                    2, 3, 4 -> AppSeason.SPRING
                    5, 6, 7 -> AppSeason.SUMMER
                    8, 9, 10 -> AppSeason.AUTUMN
                    else -> AppSeason.SPRING
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSeason.SPRING)

    val currentTimeOfDayTheme: StateFlow<TimeOfDayTheme> = userProfile.map { profile ->
        when (profile?.dayNightOverride) {
            "DAY" -> TimeOfDayTheme.DAY
            "SUNSET" -> TimeOfDayTheme.SUNSET
            "NIGHT" -> TimeOfDayTheme.NIGHT
            else -> {
                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                when {
                    hour in 6..17 -> TimeOfDayTheme.DAY
                    hour in 18..20 -> TimeOfDayTheme.SUNSET
                    else -> TimeOfDayTheme.NIGHT
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimeOfDayTheme.DAY)

    private fun startTimerCountdownLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val meta = knotMeta.value ?: continue
                if (meta.timerRunning && meta.eggState == "PULSING") {
                    val remaining = meta.timerSecondsRemaining - 1
                    if (remaining <= 0) {
                        repository.markTimerExpired()
                        SoundEffectManager.playCrack()
                    } else {
                        repository.updateTimerSeconds(remaining)
                    }
                }
            }
        }
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
        if (tab == 1) {
            refreshPairwiseMatchup()
        }
    }

    fun setTaskFilter(filter: String) {
        _taskFilter.value = filter
        SoundEffectManager.playPop()
    }

    fun openThoughtBubbleDialog() {
        _showThoughtBubbleDialog.value = true
        SoundEffectManager.playPop()
    }

    fun closeThoughtBubbleDialog() {
        _showThoughtBubbleDialog.value = false
    }

    fun openSettingsDialog() {
        _showSettingsDialog.value = true
        SoundEffectManager.playPop()
    }

    fun closeSettingsDialog() {
        _showSettingsDialog.value = false
    }

    fun openSquadDialog() {
        _showSquadDialog.value = true
        SoundEffectManager.playPop()
    }

    fun closeSquadDialog() {
        _showSquadDialog.value = false
    }

    fun openBadgeGalleryDialog() {
        _showBadgeGalleryDialog.value = true
        SoundEffectManager.playPop()
    }

    fun closeBadgeGalleryDialog() {
        _showBadgeGalleryDialog.value = false
    }

    fun openTourDialog() {
        _showTourDialog.value = true
        SoundEffectManager.playPop()
    }

    fun closeTourDialog() {
        _showTourDialog.value = false
    }

    fun openPhotoProofDialog(chore: ChoreItem) {
        _activeProofChore.value = chore
        _showPhotoProofDialog.value = true
        SoundEffectManager.playPop()
    }

    fun closePhotoProofDialog() {
        _showPhotoProofDialog.value = false
        _activeProofChore.value = null
    }

    fun openTutorialDialog(tutorialId: String) {
        val tutorial = allTutorials.value.firstOrNull { it.id == tutorialId }
            ?: allTutorials.value.firstOrNull()
        if (tutorial != null) {
            _activeTutorial.value = tutorial
            _showTutorialDialog.value = true
            SoundEffectManager.playPop()
        }
    }

    fun closeTutorialDialog() {
        _showTutorialDialog.value = false
        _activeTutorial.value = null
    }

    fun addCustomChore(
        rawText: String,
        category: String,
        memberName: String,
        difficulty: TaskDifficulty,
        requiresProof: Boolean,
        isSoloTask: Boolean
    ) {
        viewModelScope.launch {
            val standardized = rawText.trim().replaceFirstChar { it.uppercase() }
            repository.addChoreThought(
                text = if (standardized.isEmpty()) "Organize Daily Space" else standardized,
                category = category,
                postedBy = memberName,
                difficulty = difficulty,
                requiresPhotoProof = requiresProof,
                isSoloTask = isSoloTask
            )
            _confettiTrigger.value += 1
            SoundEffectManager.playPop()
            closeThoughtBubbleDialog()
            refreshPairwiseMatchup()
        }
    }

    fun refreshPairwiseMatchup() {
        viewModelScope.launch {
            val list = repository.getActiveChoresList()
            if (list.size >= 2) {
                val shuffled = list.shuffled()
                _currentMatchup.value = PairwiseMatchup(shuffled[0], shuffled[1])
            } else {
                _currentMatchup.value = null
            }
        }
    }

    fun choosePairwiseCard(chosenA: Boolean) {
        val matchup = _currentMatchup.value ?: return
        viewModelScope.launch {
            SoundEffectManager.playPop()
            val winner = if (chosenA) matchup.choreA else matchup.choreB
            val loser = if (chosenA) matchup.choreB else matchup.choreA
            repository.recordPairwiseChoice(winner.id, loser.id)
            refreshPairwiseMatchup()
        }
    }

    fun completeCoreChore(chore: ChoreItem, clickPosition: Offset, eggCenterPosition: Offset) {
        val activeUser = allMembers.value.firstOrNull { it.isCurrentActiveUser }?.name ?: (userProfile.value?.username ?: "Mia")
        viewModelScope.launch {
            SoundEffectManager.playChime()
            _confettiTrigger.value += 1

            _sparkStartPos.value = clickPosition
            _sparkTargetPos.value = eggCenterPosition
            _sparkTrigger.value += 1

            val (updatedCracks, shouldHatch) = repository.completeChore(chore.id, activeUser)

            delay(400)
            SoundEffectManager.playCrack()

            if (shouldHatch) {
                delay(300)
                val hatched = repository.hatchEgg()
                _latestHatchedPet.value = hatched
                SoundEffectManager.playFanfare()
                _confettiTrigger.value += 2
            }
        }
    }

    fun submitProofOfWork(choreId: Long, beforeUri: String?, afterUri: String?) {
        viewModelScope.launch {
            repository.attachProofOfWork(choreId, beforeUri, afterUri)
            SoundEffectManager.playFanfare()
            _confettiTrigger.value += 1
            closePhotoProofDialog()
        }
    }

    fun completeTutorialStep(tutorialId: String) {
        viewModelScope.launch {
            repository.levelUpTutorial(tutorialId)
            SoundEffectManager.playFanfare()
            _confettiTrigger.value += 1
            closeTutorialDialog()
        }
    }

    fun onEggTapped() {
        viewModelScope.launch {
            val meta = knotMeta.value ?: return@launch
            if (meta.eggState == "PULSING") {
                SoundEffectManager.playPop()
                _confettiTrigger.value += 1
            } else if (meta.eggState == "HATCHED") {
                SoundEffectManager.playFanfare()
            } else if (meta.eggState == "SLEEPING") {
                SoundEffectManager.playCrack()
            }
        }
    }

    fun startNewEggIncubation() {
        viewModelScope.launch {
            repository.startNewIncubation()
            _latestHatchedPet.value = null
            SoundEffectManager.playPop()
        }
    }

    fun toggleTimer() {
        val meta = knotMeta.value ?: return
        viewModelScope.launch {
            repository.toggleTimerRunning(!meta.timerRunning)
            SoundEffectManager.playPop()
        }
    }

    fun resetTimer() {
        viewModelScope.launch {
            repository.startNewIncubation()
            SoundEffectManager.playPop()
        }
    }

    fun switchActiveMember(memberName: String) {
        viewModelScope.launch {
            repository.setActiveUser(memberName)
            SoundEffectManager.playPop()
        }
    }

    fun equipPetAccessory(petId: Long, accessory: PetAccessory) {
        viewModelScope.launch {
            repository.updatePetAccessory(petId, accessory)
            SoundEffectManager.playPop()
            _confettiTrigger.value += 1
        }
    }

    fun feedBuddy(treatName: String) {
        val buddy = activeBuddyPet.value ?: allPets.value.firstOrNull() ?: return
        viewModelScope.launch {
            repository.feedBuddy(buddy.id, treatName)
            SoundEffectManager.playPop()
            _confettiTrigger.value += 1
        }
    }

    fun petBuddy() {
        val buddy = activeBuddyPet.value ?: allPets.value.firstOrNull() ?: return
        viewModelScope.launch {
            repository.petBuddy(buddy.id)
            SoundEffectManager.playChime()
            _confettiTrigger.value += 1
        }
    }

    fun joinSquadByCode(code: String) {
        viewModelScope.launch {
            repository.joinSquadWithCode(code)
            SoundEffectManager.playFanfare()
            _confettiTrigger.value += 1
            closeSquadDialog()
        }
    }

    fun joinMatchedSquad(squadName: String, squadCode: String, matchedMembers: List<GroupMember>) {
        viewModelScope.launch {
            repository.joinMatchedSquad(squadName, squadCode, matchedMembers)
            SoundEffectManager.playFanfare()
            _confettiTrigger.value += 2
        }
    }

    fun toggleSquadSoloMode(mode: String) {
        viewModelScope.launch {
            repository.setMode(mode)
            SoundEffectManager.playPop()
        }
    }

    fun updateSettings(
        season: String,
        dayNight: String,
        animationDensity: String,
        archetype: String,
        username: String,
        avatarEmoji: String
    ) {
        viewModelScope.launch {
            repository.updateSettings(
                seasonOverride = season,
                dayNightOverride = dayNight,
                animationDensity = animationDensity,
                personalityArchetype = archetype,
                username = username,
                avatarEmoji = avatarEmoji
            )
            SoundEffectManager.playPop()
            closeSettingsDialog()
        }
    }

    fun completeOnboarding(
        username: String,
        gender: String,
        avatar: String,
        archetype: String,
        interests: List<String>,
        squadCode: String,
        initialGoal: String
    ) {
        viewModelScope.launch {
            repository.completeOnboarding(
                username = username,
                email = "explorer@knot.app",
                authProvider = "GOOGLE",
                avatarEmoji = avatar,
                genderPronoun = gender,
                selectedInterests = interests,
                selectedGoals = listOf(initialGoal),
                primaryGoalId = initialGoal
            )
            SoundEffectManager.playFanfare()
            _confettiTrigger.value += 2
            _showTourDialog.value = true
        }
    }
}
