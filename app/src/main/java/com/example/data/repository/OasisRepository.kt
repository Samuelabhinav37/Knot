package com.example.data.repository

import com.example.data.local.OasisDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlin.math.pow

class OasisRepository(private val dao: OasisDao) {

    val allChores: Flow<List<ChoreItem>> = dao.getAllChores()
    val coreFiveChores: Flow<List<ChoreItem>> = dao.getCoreFiveActiveChores()
    val allMembers: Flow<List<GroupMember>> = dao.getAllMembers()
    val allPets: Flow<List<HatchedPet>> = dao.getAllPets()
    val activeBuddy: Flow<HatchedPet?> = dao.getActiveBuddy()
    val oasisMeta: Flow<OasisMeta?> = dao.getOasisMeta()
    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val allBadges: Flow<List<BadgeItem>> = dao.getAllBadges()
    val allTutorials: Flow<List<SkillTutorial>> = dao.getAllTutorials()

    suspend fun addChoreThought(
        text: String,
        category: String,
        postedBy: String,
        difficulty: TaskDifficulty = TaskDifficulty.EASY,
        requiresPhotoProof: Boolean = false,
        isSoloTask: Boolean = false,
        tutorialId: String? = null
    ): Long {
        val newChore = ChoreItem(
            text = text,
            iconCategory = category,
            postedBy = postedBy,
            score = when (difficulty) {
                TaskDifficulty.EASY -> 1000
                TaskDifficulty.MEDIUM -> 1150
                TaskDifficulty.HARD -> 1300
            },
            difficulty = difficulty,
            requiresPhotoProof = requiresPhotoProof,
            isSoloTask = isSoloTask,
            tutorialId = tutorialId
        )
        return dao.insertChore(newChore)
    }

    suspend fun recordPairwiseChoice(winnerId: Long, loserId: Long) {
        val winner = dao.getChoreById(winnerId) ?: return
        val loser = dao.getChoreById(loserId) ?: return

        val kFactor = 32.0
        val expWinner = 1.0 / (1.0 + 10.0.pow((loser.score - winner.score) / 400.0))
        val expLoser = 1.0 / (1.0 + 10.0.pow((winner.score - loser.score) / 400.0))

        val newWinnerScore = (winner.score + kFactor * (1.0 - expWinner)).toInt()
        val newLoserScore = (loser.score + kFactor * (0.0 - expLoser)).toInt()

        dao.updateMatchupScore(winnerId, newWinnerScore, 1)
        dao.updateMatchupScore(loserId, newLoserScore, 0)
    }

    suspend fun completeChore(choreId: Long, completedBy: String): Pair<Int, Boolean> {
        return dao.completeChoreTx(choreId, completedBy)
    }

    suspend fun attachProofOfWork(choreId: Long, beforeUri: String?, afterUri: String?) {
        dao.attachProofOfWork(choreId, beforeUri, afterUri)
        // Bonus XP for photo verified proof
        dao.addXp(150)
        dao.unlockBadge("badge_origami_fold")
    }

    suspend fun hatchEgg(petName: String? = null): HatchedPet {
        val members = dao.getMembersList()
        val meta = dao.getOasisMetaSync() ?: OasisMeta()

        // Evaluate participation
        val totalMembers = members.size.coerceAtLeast(1)
        val participatingMembers = members.count { it.tasksCompletedCount > 0 }
        val is100PercentParticipation = (participatingMembers == totalMembers)

        val rarity = if (is100PercentParticipation) {
            PetRarity.LEGENDARY
        } else {
            PetRarity.SLACKER_DOWNGRADE
        }

        val legendaryPool = listOf(
            Pair("Starling Dragon", "A magnificent pastel dragon that breathes sparkling cotton-candy stardust."),
            Pair("Cosmic Bunny", "Bounces higher than clouds and brings good fortune to team goals."),
            Pair("Celestial Gryphon", "Shimmers with iridescent feathers and guards the oasis harmony."),
            Pair("Rainbow Phoenix", "Flaps glowing wings that leave trails of warm confetti."),
            Pair("Starlight Kitty", "Purrs with melodic chimes and radiates pure team energy.")
        )

        val slackerPool = listOf(
            Pair("Sleepy Cloud Sloth", "A very cute monochrome sloth that yawns and gently asks friends to pitch in."),
            Pair("Melancholy Grey Lion", "Has big cute sad puppy eyes because one friend forgot to help."),
            Pair("Tired Cloud Pup", "Loves the team still, but needs everyone's help next time to get its colors back!"),
            Pair("Drowsy Koala", "Sleeps peacefully on a bamboo branch waiting for full squad co-op.")
        )

        val choice = if (rarity == PetRarity.LEGENDARY) {
            legendaryPool.random()
        } else {
            slackerPool.random()
        }

        val name = petName ?: choice.first.split(" ").last()
        val newPet = HatchedPet(
            name = name,
            species = choice.first,
            rarity = rarity,
            accessory = if (rarity == PetRarity.LEGENDARY) PetAccessory.values().filter { it != PetAccessory.NONE }.random() else PetAccessory.NONE,
            streakAtHatch = if (rarity == PetRarity.LEGENDARY) meta.currentStreak + 1 else meta.currentStreak,
            personality = choice.second,
            hungerLevel = 80,
            happinessLevel = 90,
            affectionLevel = 1,
            isBuddy = false,
            xPosRatio = (0.2f + Math.random().toFloat() * 0.6f),
            yPosRatio = (0.35f + Math.random().toFloat() * 0.45f)
        )

        val petId = dao.insertPet(newPet)

        val nextStreak = if (rarity == PetRarity.LEGENDARY) meta.currentStreak + 1 else meta.currentStreak

        dao.updateOasisMeta { current ->
            current.copy(
                currentStreak = nextStreak,
                eggCracks = 5,
                eggState = "HATCHED",
                latestHatchedPetId = petId
            )
        }

        // Check badge unlocks
        dao.unlockBadge("badge_squad_legend")

        return newPet.copy(id = petId)
    }

    suspend fun startNewIncubation() {
        dao.resetMemberTaskCounts()
        dao.updateOasisMeta { meta ->
            meta.copy(
                eggCracks = 0,
                eggState = "PULSING",
                timerSecondsRemaining = meta.initialTimerSeconds,
                timerRunning = true,
                latestHatchedPetId = null
            )
        }
    }

    suspend fun markTimerExpired() {
        dao.updateOasisMeta { meta ->
            if (meta.eggCracks < 5 && meta.eggState != "HATCHED") {
                meta.copy(
                    eggState = "SLEEPING",
                    timerRunning = false,
                    timerSecondsRemaining = 0
                )
            } else meta
        }
    }

    suspend fun updateTimerSeconds(remaining: Int) {
        dao.updateOasisMeta { meta -> meta.copy(timerSecondsRemaining = remaining) }
    }

    suspend fun toggleTimerRunning(isRunning: Boolean) {
        dao.updateOasisMeta { meta -> meta.copy(timerRunning = isRunning) }
    }

    suspend fun setActiveUser(memberName: String) {
        dao.setActiveUser(memberName)
    }

    // --- Pet Buddy Mode ---
    suspend fun setPetAsBuddy(petId: Long) {
        dao.setPetAsBuddy(petId)
    }

    suspend fun feedBuddy(petId: Long, treatName: String) {
        val hungerGain = when (treatName) {
            "Berry" -> 15
            "Croissant" -> 30
            "Star Candy" -> 50
            else -> 20
        }
        dao.feedPet(petId, hungerGain)
        dao.addXp(25)
    }

    suspend fun petBuddy(petId: Long) {
        dao.petAndPlay(petId, 15)
        dao.addXp(20)
    }

    suspend fun updatePetAccessory(petId: Long, accessory: PetAccessory) {
        dao.updatePetAccessory(petId, accessory.name)
    }

    suspend fun updatePetPosition(petId: Long, x: Float, y: Float) {
        dao.updatePetPosition(petId, x, y)
    }

    // --- User Profile & Onboarding ---
    suspend fun saveProfile(profile: UserProfile) {
        dao.saveUserProfile(profile)
    }

    suspend fun completeOnboarding(
        username: String,
        email: String,
        authProvider: String,
        avatarEmoji: String,
        genderPronoun: String,
        selectedInterests: List<String>,
        selectedGoals: List<String>,
        primaryGoalId: String
    ) {
        val archetype = determineArchetype(selectedInterests)
        val profile = UserProfile(
            id = 1,
            username = username,
            email = email,
            authProvider = authProvider,
            avatarEmoji = avatarEmoji,
            genderPronoun = genderPronoun,
            personalityArchetype = archetype,
            selectedInterests = selectedInterests.joinToString(","),
            selectedGoals = selectedGoals.joinToString(","),
            activeGoalId = primaryGoalId,
            currentMode = "SQUAD",
            squadCode = "OASIS-" + (1000..9999).random().toString(),
            squadName = "Pastel Blossom Squad",
            level = 1,
            currentXp = 100,
            nextLevelXp = 500,
            badgesCount = 1,
            isOnboarded = true,
            seasonOverride = "AUTO",
            dayNightOverride = "AUTO",
            animationDensity = "HIGH"
        )
        dao.saveUserProfile(profile)

        // Also add user as squad member
        val members = dao.getMembersList().toMutableList()
        val existingIndex = members.indexOfFirst { it.name == username }
        if (existingIndex >= 0) {
            members[existingIndex] = members[existingIndex].copy(
                avatarEmoji = avatarEmoji,
                personalityArchetype = archetype,
                primaryInterest = selectedInterests.firstOrNull() ?: "General",
                isCurrentActiveUser = true
            )
        } else {
            members.add(
                0,
                GroupMember(
                    name = username,
                    avatarEmoji = avatarEmoji,
                    avatarColorHex = 0xFFFFB6C1,
                    personalityArchetype = archetype,
                    primaryInterest = selectedInterests.firstOrNull() ?: "General",
                    tasksCompletedCount = 0,
                    isCurrentActiveUser = true
                )
            )
        }
        dao.insertMembers(members)
    }

    private fun determineArchetype(interests: List<String>): String {
        return when {
            interests.any { it.contains("Cooking", ignoreCase = true) } && interests.any { it.contains("Plants", ignoreCase = true) } -> "The Cozy Cultivator"
            interests.any { it.contains("Organizing", ignoreCase = true) } -> "The Energetic Organizer"
            interests.any { it.contains("Cooking", ignoreCase = true) } -> "The Culinary Artisan"
            interests.any { it.contains("Fixing", ignoreCase = true) || it.contains("DIY", ignoreCase = true) } -> "The Handy Crafter"
            interests.any { it.contains("Plants", ignoreCase = true) } -> "The Plant Whisperer"
            interests.any { it.contains("Wellness", ignoreCase = true) || it.contains("Mindful", ignoreCase = true) } -> "The Mindful Minimalist"
            else -> "The Harmonious Explorer"
        }
    }

    suspend fun joinSquadWithCode(squadCode: String) {
        val profile = dao.getUserProfileSync() ?: return
        dao.saveUserProfile(
            profile.copy(
                squadCode = squadCode.uppercase(),
                squadName = "Squad " + squadCode.uppercase(),
                currentMode = "SQUAD"
            )
        )
    }

    suspend fun joinMatchedSquad(squadName: String, squadCode: String, matchedMembers: List<GroupMember>) {
        val profile = dao.getUserProfileSync() ?: return
        dao.saveUserProfile(
            profile.copy(
                squadCode = squadCode.uppercase(),
                squadName = squadName,
                currentMode = "SQUAD"
            )
        )
        val currentMember = GroupMember(
            name = profile.username,
            avatarEmoji = profile.avatarEmoji,
            avatarColorHex = 0xFFFFB6C1,
            personalityArchetype = profile.personalityArchetype,
            primaryInterest = "Home & Routine",
            tasksCompletedCount = 0,
            isCurrentActiveUser = true
        )
        val membersList = mutableListOf(currentMember)
        membersList.addAll(matchedMembers)
        dao.insertMembers(membersList)
    }

    suspend fun setMode(mode: String) {
        val profile = dao.getUserProfileSync() ?: return
        dao.saveUserProfile(profile.copy(currentMode = mode))
    }

    suspend fun updateSettings(
        seasonOverride: String,
        dayNightOverride: String,
        animationDensity: String,
        personalityArchetype: String,
        username: String,
        avatarEmoji: String
    ) {
        val profile = dao.getUserProfileSync() ?: return
        dao.saveUserProfile(
            profile.copy(
                seasonOverride = seasonOverride,
                dayNightOverride = dayNightOverride,
                animationDensity = animationDensity,
                personalityArchetype = personalityArchetype,
                username = username,
                avatarEmoji = avatarEmoji
            )
        )
    }

    suspend fun unlockBadge(badgeId: String) {
        dao.unlockBadge(badgeId)
    }

    suspend fun levelUpTutorial(tutorialId: String) {
        dao.levelUpSkill(tutorialId)
        dao.addXp(75)
        dao.unlockBadge("badge_mastery_apprentice")
    }

    suspend fun getActiveChoresList() = dao.getActiveChoresList()
}
