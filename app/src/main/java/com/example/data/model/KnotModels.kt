package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskDifficulty(val label: String, val xp: Int, val emoji: String) {
    EASY("Easy", 100, "🟢"),
    MEDIUM("Medium", 200, "🟡"),
    HARD("Hard", 400, "🔴")
}

@Entity(tableName = "chores")
data class ChoreItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val iconCategory: String, // "COOKING", "CLEANING", "FIXING", "KITCHEN", "LAUNDRY", "PLANTS", "ORGANIZING", "WELLNESS", "PETS", "STUDY"
    val postedBy: String,
    val score: Int = 1000, // Bradley-Terry / Elo ranking score
    val difficulty: TaskDifficulty = TaskDifficulty.EASY,
    val isSoloTask: Boolean = false,
    val requiresPhotoProof: Boolean = false,
    val beforePhotoUri: String? = null,
    val afterPhotoUri: String? = null,
    val isVerified: Boolean = false,
    val tutorialId: String? = null,
    val seasonTag: String = "ALL", // "ALL", "WINTER", "SPRING", "SUMMER", "AUTUMN", "FESTIVAL"
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
    val avatarEmoji: String,
    val avatarColorHex: Long,
    val personalityArchetype: String = "The Cozy Cultivator",
    val primaryInterest: String = "Home & Organizing",
    val tasksCompletedCount: Int = 0,
    val isCurrentActiveUser: Boolean = false
)

enum class PetRarity {
    LEGENDARY,          // 100% group participation before timer
    SLACKER_DOWNGRADE,  // 5 tasks done, but at least 1 member did 0 tasks
    NORMAL              // standard solo/partial hatch
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
    val name: String,
    val species: String, // "Mini Dragon", "Cosmic Bunny", "Rainbow Gryphon", "Sparkle Cat", "Cloud Sloth", "Grey Lion"
    val rarity: PetRarity,
    val accessory: PetAccessory = PetAccessory.NONE,
    val hatchedTimestamp: Long = System.currentTimeMillis(),
    val streakAtHatch: Int = 1,
    val personality: String,
    val hungerLevel: Int = 80, // 0..100
    val happinessLevel: Int = 90, // 0..100
    val affectionLevel: Int = 1, // Level 1..10
    val isBuddy: Boolean = false,
    val xPosRatio: Float = 0.5f,
    val yPosRatio: Float = 0.5f
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1,
    val username: String = "Explorer",
    val email: String = "explorer@knot.app",
    val authProvider: String = "GOOGLE", // "GOOGLE", "APPLE", "EMAIL"
    val avatarEmoji: String = "🌸",
    val genderPronoun: String = "They/Them",
    val personalityArchetype: String = "The Cozy Cultivator",
    val selectedInterests: String = "Home & Organizing,Cooking & Culinary,Greenery & Plants",
    val selectedGoals: String = "Clothes Folding Mastery,15-Minute Meal Prep,Indoor Jungle Care",
    val activeGoalId: String = "goal_folding",
    val currentMode: String = "SQUAD", // "SOLO", "SQUAD"
    val squadCode: String = "KNOT-7X29",
    val squadName: String = "Pastel Cloud Squad",
    val level: Int = 1,
    val currentXp: Int = 240,
    val nextLevelXp: Int = 500,
    val badgesCount: Int = 4,
    val isOnboarded: Boolean = true,
    val nextDailyTaskUnlockTimestamp: Long = 0L,
    val seasonOverride: String = "AUTO", // "AUTO", "WINTER", "SPRING", "SUMMER", "AUTUMN", "FESTIVAL"
    val dayNightOverride: String = "AUTO", // "AUTO", "DAY", "SUNSET", "NIGHT"
    val animationDensity: String = "HIGH" // "HIGH", "LOW"
)

@Entity(tableName = "badges")
data class BadgeItem(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val category: String, // "STREAK", "SKILL", "COOP", "SEASONAL"
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
    val category: String,
    val estimatedTimeToMaster: String, // "1 Week", "2 Weeks", "1 Month"
    val masteryLevel: Int = 1, // 1: Beginner, 2: Apprentice, 3: Adept, 4: Master
    val stepsJson: String, // Step by step interactive instructions
    val blogResourcesJson: String, // Useful websites/blogs
    val iconEmoji: String
)

@Entity(tableName = "knot_meta")
data class KnotMeta(
    @PrimaryKey
    val id: Int = 1,
    val currentStreak: Int = 3,
    val timerSecondsRemaining: Int = 360,
    val initialTimerSeconds: Int = 360,
    val timerRunning: Boolean = false,
    val eggCracks: Int = 0,
    val eggState: String = "PULSING", // "PULSING", "HATCHED", "SLEEPING"
    val latestHatchedPetId: Long? = null,
    val seasonTheme: String = "SPRING", // "WINTER", "SPRING", "SUMMER", "AUTUMN", "FESTIVAL"
    val isNightMode: Boolean = false
)
