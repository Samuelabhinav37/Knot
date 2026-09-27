package com.example.data.local

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromPetRarity(value: PetRarity): String = value.name

    @TypeConverter
    fun toPetRarity(value: String): PetRarity = try {
        PetRarity.valueOf(value)
    } catch (e: Exception) {
        PetRarity.NORMAL
    }

    @TypeConverter
    fun fromPetAccessory(value: PetAccessory): String = value.name

    @TypeConverter
    fun toPetAccessory(value: String): PetAccessory = try {
        PetAccessory.valueOf(value)
    } catch (e: Exception) {
        PetAccessory.NONE
    }

    @TypeConverter
    fun fromTaskDifficulty(value: TaskDifficulty): String = value.name

    @TypeConverter
    fun toTaskDifficulty(value: String): TaskDifficulty = try {
        TaskDifficulty.valueOf(value)
    } catch (e: Exception) {
        TaskDifficulty.EASY
    }
}

@Database(
    entities = [
        ChoreItem::class,
        GroupMember::class,
        HatchedPet::class,
        OasisMeta::class,
        UserProfile::class,
        BadgeItem::class,
        SkillTutorial::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun oasisDao(): OasisDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cadre_oasis_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedIfEmpty(dao: OasisDao) {
            // Seed User Profile if missing
            if (dao.getUserProfileSync() == null) {
                dao.saveUserProfile(
                    UserProfile(
                        id = 1,
                        username = "Alex Morgan",
                        email = "alex.morgan@squad.app",
                        authProvider = "GOOGLE",
                        avatarEmoji = "👤",
                        genderPronoun = "They/Them",
                        personalityArchetype = "Operations Strategist",
                        selectedInterests = "Workflow Optimization,Strategic Planning,System Operations",
                        selectedGoals = "Daily Operational Checks,Milestone Execution,Sprint Alignment",
                        activeGoalId = "goal_ops",
                        currentMode = "SQUAD",
                        squadCode = "SQUAD-8X9",
                        squadName = "Alpha Squad",
                        level = 3,
                        currentXp = 420,
                        nextLevelXp = 800,
                        badgesCount = 4,
                        isOnboarded = true,
                        seasonOverride = "AUTO",
                        dayNightOverride = "AUTO",
                        animationDensity = "LOW"
                    )
                )
            }

            // Seed Squad Team Members if missing
            if (dao.getMembersList().isEmpty()) {
                val defaultMembers = listOf(
                    GroupMember(
                        name = "Alex Morgan",
                        avatarEmoji = "👤",
                        avatarColorHex = 0xFF0F172A,
                        personalityArchetype = "Operations Strategist",
                        primaryInterest = "Workflow Optimization",
                        tasksCompletedCount = 4,
                        isCurrentActiveUser = true
                    ),
                    GroupMember(
                        name = "Marcus Cole",
                        avatarEmoji = "👤",
                        avatarColorHex = 0xFF1E293B,
                        personalityArchetype = "Execution Specialist",
                        primaryInterest = "Execution",
                        tasksCompletedCount = 3,
                        isCurrentActiveUser = false
                    ),
                    GroupMember(
                        name = "Elena Vance",
                        avatarEmoji = "👤",
                        avatarColorHex = 0xFF334155,
                        personalityArchetype = "Systems Architect",
                        primaryInterest = "Strategy",
                        tasksCompletedCount = 3,
                        isCurrentActiveUser = false
                    ),
                    GroupMember(
                        name = "Priya Nair",
                        avatarEmoji = "👤",
                        avatarColorHex = 0xFF475569,
                        personalityArchetype = "Quality Reviewer",
                        primaryInterest = "Quality Control",
                        tasksCompletedCount = 2,
                        isCurrentActiveUser = false
                    ),
                    GroupMember(
                        name = "David Chen",
                        avatarEmoji = "👤",
                        avatarColorHex = 0xFF64748B,
                        personalityArchetype = "Sprint Manager",
                        primaryInterest = "Agile Coordination",
                        tasksCompletedCount = 2,
                        isCurrentActiveUser = false
                    )
                )
                dao.insertMembers(defaultMembers)
            }

            // Seed Day-to-Day Routine Tasks if missing
            if (dao.getActiveChoresList().isEmpty()) {
                val defaultChores = listOf(
                    ChoreItem(
                        text = "Organize workspace and review backlog",
                        iconCategory = "ORGANIZING",
                        postedBy = "Alex Morgan",
                        score = 1250,
                        difficulty = TaskDifficulty.EASY,
                        requiresPhotoProof = false,
                        tutorialId = "goal_ops",
                        seasonTag = "ALL",
                        matchupWins = 4,
                        matchupTotal = 4
                    ),
                    ChoreItem(
                        text = "Review daily objectives and blockers",
                        iconCategory = "PLANNING",
                        postedBy = "Alex Morgan",
                        score = 1210,
                        difficulty = TaskDifficulty.EASY,
                        requiresPhotoProof = false,
                        tutorialId = "goal_standup",
                        seasonTag = "ALL",
                        matchupWins = 3,
                        matchupTotal = 4
                    ),
                    ChoreItem(
                        text = "Conduct peer operational review",
                        iconCategory = "REVIEW",
                        postedBy = "Marcus Cole",
                        score = 1170,
                        difficulty = TaskDifficulty.MEDIUM,
                        requiresPhotoProof = true,
                        tutorialId = "goal_review",
                        seasonTag = "ALL",
                        matchupWins = 2,
                        matchupTotal = 3
                    ),
                    ChoreItem(
                        text = "Document sprint architecture notes",
                        iconCategory = "DEVELOPMENT",
                        postedBy = "Elena Vance",
                        score = 1140,
                        difficulty = TaskDifficulty.MEDIUM,
                        requiresPhotoProof = false,
                        tutorialId = "goal_docs",
                        seasonTag = "ALL",
                        matchupWins = 1,
                        matchupTotal = 3
                    )
                )
                dao.insertChores(defaultChores)
            }

            // Seed Meta if missing
            if (dao.getOasisMetaSync() == null) {
                dao.saveOasisMeta(
                    OasisMeta(
                        id = 1,
                        currentStreak = 5,
                        timerSecondsRemaining = 1500,
                        initialTimerSeconds = 1500,
                        timerRunning = false,
                        eggCracks = 0,
                        eggState = "PULSING",
                        seasonTheme = "SPRING",
                        isNightMode = false
                    )
                )
            }

            // Seed Badges if missing
            if (dao.getUnlockedBadgeCount() == 0) {
                val defaultBadges = listOf(
                    BadgeItem(
                        id = "badge_consistency",
                        title = "Consistency Master",
                        description = "Maintain a 5-day continuous operational routine check.",
                        iconEmoji = "🎯",
                        category = "STREAK",
                        unlocked = true,
                        unlockedTimestamp = System.currentTimeMillis() - 86400000L * 2,
                        badgeTier = 2
                    ),
                    BadgeItem(
                        id = "badge_squad_sync",
                        title = "Squad Synchronization",
                        description = "Collaborate with squad members to reach 100% daily goals.",
                        iconEmoji = "🤝",
                        category = "COOP",
                        unlocked = true,
                        unlockedTimestamp = System.currentTimeMillis() - 86400000L,
                        badgeTier = 1
                    ),
                    BadgeItem(
                        id = "badge_deep_focus",
                        title = "Deep Focus Protocol",
                        description = "Complete 10 focused sprint blocks without distraction.",
                        iconEmoji = "⚡",
                        category = "SKILL",
                        unlocked = true,
                        unlockedTimestamp = System.currentTimeMillis(),
                        badgeTier = 1
                    ),
                    BadgeItem(
                        id = "badge_arch_audit",
                        title = "Architecture Reviewer",
                        description = "Verify and audit 5 peer deliverable specifications.",
                        iconEmoji = "📋",
                        category = "SKILL",
                        unlocked = false,
                        badgeTier = 2
                    )
                )
                dao.insertBadges(defaultBadges)
            }

            // Seed Skill Tutorials if missing
            val defaultTutorials = listOf(
                SkillTutorial(
                    id = "goal_ops",
                    title = "Deep Work Protocol",
                    subtitle = "25-minute focused execution intervals with structured cooldowns.",
                    category = "OPERATIONS",
                    estimatedTimeToMaster = "3 Days",
                    masteryLevel = 2,
                    stepsJson = "[{\"step\":1,\"instruction\":\"Silence all non-critical notifications.\"},{\"step\":2,\"instruction\":\"Define a single measurable deliverable for the block.\"},{\"step\":3,\"instruction\":\"Execute uninterrupted for 25 minutes.\"}]",
                    blogResourcesJson = "[\"https://focus.work/principles\",\"https://calm.so/workflow\"]",
                    iconEmoji = "⚡"
                ),
                SkillTutorial(
                    id = "goal_standup",
                    title = "Asynchronous Daily Standup",
                    subtitle = "Clear written documentation of achievements and dependencies.",
                    category = "COMMUNICATION",
                    estimatedTimeToMaster = "2 Days",
                    masteryLevel = 2,
                    stepsJson = "[{\"step\":1,\"instruction\":\"Log completed deliverables from the prior cycle.\"},{\"step\":2,\"instruction\":\"Outline top 2 immediate priorities for today.\"},{\"step\":3,\"instruction\":\"Flag any blockers requiring cross-functional input.\"}]",
                    blogResourcesJson = "[\"https://async.work/cadence\"]",
                    iconEmoji = "📝"
                ),
                SkillTutorial(
                    id = "goal_review",
                    title = "Peer Deliverable Review",
                    subtitle = "Systematic verification of outputs against specifications.",
                    category = "REVIEW",
                    estimatedTimeToMaster = "1 Week",
                    masteryLevel = 1,
                    stepsJson = "[{\"step\":1,\"instruction\":\"Review acceptance criteria thoroughly.\"},{\"step\":2,\"instruction\":\"Validate deliverable functionality and edge cases.\"},{\"step\":3,\"instruction\":\"Provide actionable, constructive feedback notes.\"}]",
                    blogResourcesJson = "[\"https://review.dev/checklist\"]",
                    iconEmoji = "🔍"
                ),
                SkillTutorial(
                    id = "goal_docs",
                    title = "Structured Knowledge Management",
                    subtitle = "Creating accessible, reusable standard operating procedures.",
                    category = "DOCUMENTATION",
                    estimatedTimeToMaster = "4 Days",
                    masteryLevel = 1,
                    stepsJson = "[{\"step\":1,\"instruction\":\"Capture clear prerequisites and objective summary.\"},{\"step\":2,\"instruction\":\"Enumerate steps in chronological imperative order.\"},{\"step\":3,\"instruction\":\"Verify reproducibility with a team colleague.\"}]",
                    blogResourcesJson = "[\"https://docs.standard/best-practices\"]",
                    iconEmoji = "📚"
                )
            )
            dao.insertTutorials(defaultTutorials)
        }
    }
}
