package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskDifficulty(val label: String, val xp: Int) {
    EASY("Easy", 100),
    MEDIUM("Medium", 200),
    HARD("Hard", 400)
}

@Entity(tableName = "chores")
data class ChoreItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val iconCategory: String = "OPERATIONS",
    val postedBy: String = "Alex Morgan",
    val score: Int = 1000,
    val difficulty: TaskDifficulty = TaskDifficulty.EASY,
    val isSoloTask: Boolean = false,
    val requiresPhotoProof: Boolean = false,
    val beforePhotoUri: String? = null,
    val afterPhotoUri: String? = null,
    val isVerified: Boolean = false,
    val tutorialId: String? = null,
    val seasonTag: String = "ALL",
    val matchupWins: Int = 0,
    val matchupTotal: Int = 0,
    val isCompleted: Boolean = false,
    val completedBy: String? = null,
    val completedTimestamp: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "group_members")
data class GroupMember(
    @PrimaryKey
    val name: String,
    val avatarEmoji: String = "👤",
    val avatarColorHex: Long = 0xFF0F172A,
    val personalityArchetype: String = "Operations Strategist",
    val primaryInterest: String = "Operations",
    val tasksCompletedCount: Int = 0,
    val isCurrentActiveUser: Boolean = false
)

enum class PetRarity {
    LEGENDARY,
    SLACKER_DOWNGRADE,
    NORMAL
}

enum class PetAccessory(val displayName: String, val iconEmoji: String) {
    NONE("None", "✨"),
    FLOWER_CROWN("Flower Crown", "🌸"),
    WIZARD_HAT("Wizard Hat", "🧙"),
    STAR_GLASSES("Star Glasses", "⭐"),
    COZY_SCARF("Cozy Scarf", "🧣"),
    SPARKLE_AURA("Cosmic Sparkles", "✨"),
    GOLDEN_HALO("Golden Halo", "👑")
}

@Entity(tableName = "hatched_pets")
data class HatchedPet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "Buddy",
    val species: String = "Assistant",
    val rarity: PetRarity = PetRarity.NORMAL,
    val accessory: PetAccessory = PetAccessory.NONE,
    val hatchedTimestamp: Long = System.currentTimeMillis(),
    val streakAtHatch: Int = 1,
    val personality: String = "Helpful",
    val hungerLevel: Int = 100,
    val happinessLevel: Int = 100,
    val affectionLevel: Int = 1,
    val isBuddy: Boolean = false,
    val xPosRatio: Float = 0.5f,
    val yPosRatio: Float = 0.5f
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1,
    val username: String = "Alex Morgan",
    val email: String = "alex.morgan@squad.app",
    val authProvider: String = "GOOGLE",
    val avatarEmoji: String = "👤",
    val genderPronoun: String = "They/Them",
    val personalityArchetype: String = "Operations Strategist",
    val selectedInterests: String = "Workflow Optimization,Strategic Planning,System Operations",
    val selectedGoals: String = "Daily Operational Checks,Milestone Execution,Sprint Alignment",
    val activeGoalId: String = "goal_ops",
    val currentMode: String = "SQUAD",
    val squadCode: String = "SQUAD-8X9",
    val squadName: String = "Alpha Squad",
    val level: Int = 3,
    val currentXp: Int = 420,
    val nextLevelXp: Int = 800,
    val badgesCount: Int = 4,
    val isOnboarded: Boolean = true,
    val nextDailyTaskUnlockTimestamp: Long = 0L,
    val seasonOverride: String = "AUTO",
    val dayNightOverride: String = "AUTO",
    val animationDensity: String = "LOW"
)

@Entity(tableName = "badges")
data class BadgeItem(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String = "🎯",
    val category: String = "STREAK",
    val unlocked: Boolean = false,
    val unlockedTimestamp: Long = 0L,
    val badgeTier: Int = 1
)

@Entity(tableName = "skill_tutorials")
data class SkillTutorial(
    @PrimaryKey
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String = "OPERATIONS",
    val estimatedTimeToMaster: String = "3 Days",
    val masteryLevel: Int = 1,
    val stepsJson: String = "",
    val blogResourcesJson: String = "",
    val iconEmoji: String = "⚡"
)

@Entity(tableName = "oasis_meta")
data class OasisMeta(
    @PrimaryKey
    val id: Int = 1,
    val currentStreak: Int = 5,
    val timerSecondsRemaining: Int = 1500,
    val initialTimerSeconds: Int = 1500,
    val timerRunning: Boolean = false,
    val eggCracks: Int = 0,
    val eggState: String = "PULSING",
    val latestHatchedPetId: Long? = null,
    val seasonTheme: String = "SPRING",
    val isNightMode: Boolean = false
)
