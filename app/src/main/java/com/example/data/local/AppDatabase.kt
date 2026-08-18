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
    version = 2,
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
                    "pastel_oasis_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedIfEmpty(dao: OasisDao) {
            val existing = dao.getUserProfileSync()
            if (existing != null) return
            populateInitialData(dao)
        }

        private suspend fun populateInitialData(dao: OasisDao) {
                // Seed User Profile
                dao.saveUserProfile(
                    UserProfile(
                        id = 1,
                        username = "Mia",
                        email = "mia@pastel-oasis.app",
                        authProvider = "GOOGLE",
                        avatarEmoji = "🌸",
                        genderPronoun = "She/Her",
                        personalityArchetype = "The Cozy Cultivator",
                        selectedInterests = "Home & Organizing,Cooking & Culinary,Greenery & Plants,Mindful Living,DIY & Fixing",
                        selectedGoals = "Clothes Folding Mastery,15-Minute Meal Prep,Indoor Jungle Care",
                        activeGoalId = "goal_folding",
                        currentMode = "SQUAD",
                        squadCode = "OASIS-7X29",
                        squadName = "Pastel Cloud Squad",
                        level = 2,
                        currentXp = 340,
                        nextLevelXp = 600,
                        badgesCount = 4,
                        isOnboarded = true,
                        seasonOverride = "AUTO",
                        dayNightOverride = "AUTO",
                        animationDensity = "HIGH"
                    )
                )

                // Seed Group Members (5 diverse friends with different personalities)
                val defaultMembers = listOf(
                    GroupMember(
                        name = "Mia",
                        avatarEmoji = "🌸",
                        avatarColorHex = 0xFFFFB6C1,
                        personalityArchetype = "The Cozy Cultivator",
                        primaryInterest = "Home & Organizing",
                        tasksCompletedCount = 1,
                        isCurrentActiveUser = true
                    ),
                    GroupMember(
                        name = "Leo",
                        avatarEmoji = "🌿",
                        avatarColorHex = 0xFFA8E6CF,
                        personalityArchetype = "The Plant Whisperer",
                        primaryInterest = "Greenery & Plants",
                        tasksCompletedCount = 1,
                        isCurrentActiveUser = false
                    ),
                    GroupMember(
                        name = "Kai",
                        avatarEmoji = "🍳",
                        avatarColorHex = 0xFFFFF9A6,
                        personalityArchetype = "The Culinary Artisan",
                        primaryInterest = "Cooking & Culinary",
                        tasksCompletedCount = 1,
                        isCurrentActiveUser = false
                    ),
                    GroupMember(
                        name = "Chloe",
                        avatarEmoji = "🎀",
                        avatarColorHex = 0xFFE8D7FF,
                        personalityArchetype = "The Mindful Minimalist",
                        primaryInterest = "Mindful Living",
                        tasksCompletedCount = 1,
                        isCurrentActiveUser = false
                    ),
                    GroupMember(
                        name = "Sam",
                        avatarEmoji = "🛠️",
                        avatarColorHex = 0xFFFFD1DC,
                        personalityArchetype = "The Handy Crafter",
                        primaryInterest = "DIY & Fixing",
                        tasksCompletedCount = 0,
                        isCurrentActiveUser = false
                    )
                )
                dao.insertMembers(defaultMembers)

                // Seed Day-to-Day Life Tasks & Chores across common human routines + difficulty tiers
                val defaultChores = listOf(
                    ChoreItem(
                        text = "Fold clean clothes neatly",
                        iconCategory = "ORGANIZING",
                        postedBy = "Mia",
                        score = 1250,
                        difficulty = TaskDifficulty.MEDIUM,
                        requiresPhotoProof = true,
                        tutorialId = "goal_folding",
                        seasonTag = "ALL",
                        matchupWins = 4,
                        matchupTotal = 4
                    ),
                    ChoreItem(
                        text = "Prepare a healthy fresh lunch",
                        iconCategory = "COOKING",
                        postedBy = "Kai",
                        score = 1210,
                        difficulty = TaskDifficulty.MEDIUM,
                        requiresPhotoProof = true,
                        tutorialId = "goal_mealprep",
                        seasonTag = "ALL",
                        matchupWins = 3,
                        matchupTotal = 4
                    ),
                    ChoreItem(
                        text = "Water indoor houseplants",
                        iconCategory = "PLANTS",
                        postedBy = "Leo",
                        score = 1170,
                        difficulty = TaskDifficulty.EASY,
                        requiresPhotoProof = false,
                        tutorialId = "goal_plants",
                        seasonTag = "ALL",
                        matchupWins = 2,
                        matchupTotal = 3
                    ),
                    ChoreItem(
                        text = "Wipe kitchen countertops",
                        iconCategory = "CLEANING",
                        postedBy = "Mia",
                        score = 1110,
                        difficulty = TaskDifficulty.EASY,
                        requiresPhotoProof = true,
                        matchupWins = 2,
                        matchupTotal = 3
                    ),
                    ChoreItem(
                        text = "Tidy up your work desk",
                        iconCategory = "ORGANIZING",
                        postedBy = "Chloe",
                        score = 1060,
                        difficulty = TaskDifficulty.EASY,
                        requiresPhotoProof = true,
                        matchupWins = 1,
                        matchupTotal = 2
                    ),
                    ChoreItem(
                        text = "Tighten loose door handles",
                        iconCategory = "FIXING",
                        postedBy = "Sam",
                        score = 1020,
                        difficulty = TaskDifficulty.HARD,
                        requiresPhotoProof = true,
                        matchupWins = 1,
                        matchupTotal = 2
                    ),
                    ChoreItem(
                        text = "Bake fresh honey bread",
                        iconCategory = "COOKING",
                        postedBy = "Kai",
                        score = 990,
                        difficulty = TaskDifficulty.HARD,
                        requiresPhotoProof = true,
                        tutorialId = "goal_sourdough",
                        seasonTag = "ALL",
                        matchupWins = 0,
                        matchupTotal = 2
                    ),
                    ChoreItem(
                        text = "Take 5 deep mindful breaths",
                        iconCategory = "WELLNESS",
                        postedBy = "Chloe",
                        score = 950,
                        difficulty = TaskDifficulty.EASY,
                        requiresPhotoProof = false,
                        matchupWins = 0,
                        matchupTotal = 1
                    ),
                    ChoreItem(
                        text = "Wash and change bed sheets",
                        iconCategory = "LAUNDRY",
                        postedBy = "Mia",
                        score = 920,
                        difficulty = TaskDifficulty.MEDIUM,
                        requiresPhotoProof = false,
                        matchupWins = 0,
                        matchupTotal = 1
                    ),
                    ChoreItem(
                        text = "Take out the recycling bin",
                        iconCategory = "CLEANING",
                        postedBy = "Sam",
                        score = 900,
                        difficulty = TaskDifficulty.EASY,
                        requiresPhotoProof = false,
                        matchupWins = 0,
                        matchupTotal = 0
                    )
                )
                dao.insertChores(defaultChores)

                // Seed Skill Tutorials (Interactive 1-min Guides & Curated Blogs)
                val defaultTutorials = listOf(
                    SkillTutorial(
                        id = "goal_folding",
                        title = "Japanese KonMari Folding",
                        subtitle = "Fold shirts, pants, and socks into freestanding origami rectangles",
                        category = "Home & Organizing",
                        estimatedTimeToMaster = "1 Week",
                        masteryLevel = 2,
                        iconEmoji = "🧺",
                        stepsJson = """[
                            "1. Lay the shirt face-up on a smooth, clean surface.",
                            "2. Fold the right side inward by one third, then fold the sleeve flat against the body.",
                            "3. Repeat with the left side to create a neat tall rectangle.",
                            "4. Fold in half lengthwise leaving a 1-inch gap at the hem.",
                            "5. Fold into thirds until the garment stands upright on its own!"
                        ]""",
                        blogResourcesJson = """[
                            {"name": "KonMari Method Official Guide", "url": "konmari.com", "desc": "Step-by-step wardrobe transformation tips"},
                            {"name": "Cozy Minimalist Wardrobes", "url": "thespruce.com/folding", "desc": "How drawer filing creates 50% more space"}
                        ]"""
                    ),
                    SkillTutorial(
                        id = "goal_mealprep",
                        title = "15-Minute Wholesome Meal Prep",
                        subtitle = "Batch-chop, sauté, and create delicious 3-day grain & veggie bowls",
                        category = "Cooking & Culinary",
                        estimatedTimeToMaster = "2 Weeks",
                        masteryLevel = 1,
                        iconEmoji = "🍳",
                        stepsJson = """[
                            "1. Wash and chop bell peppers, zucchini, and carrots into bite-sized batons.",
                            "2. Heat a tablespoon of sesame or olive oil in a wide skillet over medium-high.",
                            "3. Sizzle aromatics (minced garlic & ginger) for 30 seconds until fragrant.",
                            "4. Toss in firmer vegetables first, tossing for 3 minutes until crisp-tender.",
                            "5. Drizzle soy-tamari and sesame seeds, portion into airtight glass containers."
                        ]""",
                        blogResourcesJson = """[
                            {"name": "Serious Eats: Quick Knife Skills", "url": "seriouseats.com/prep", "desc": "Master the claw grip for lightning-fast chopping"},
                            {"name": "Minimalist Baker Quick Bowls", "url": "minimalistbaker.com", "desc": "Nutrient-packed dressings & 15-min combinations"}
                        ]"""
                    ),
                    SkillTutorial(
                        id = "goal_plants",
                        title = "Indoor Jungle Hydration & Care",
                        subtitle = "Know exactly when your pothos, monstera, and succulents need love",
                        category = "Greenery & Plants",
                        estimatedTimeToMaster = "1 Month",
                        masteryLevel = 3,
                        iconEmoji = "🌿",
                        stepsJson = """[
                            "1. Insert your wooden skewer or finger 2 inches into the soil before watering.",
                            "2. If it comes out dry, carry the pot to the sink for bottom-watering.",
                            "3. Allow water to soak upward through drainage holes for 15 minutes.",
                            "4. Gently wipe dust off broad leaves with a damp microfiber cloth.",
                            "5. Rotate the pot 90 degrees every week for balanced, lush sun exposure!"
                        ]""",
                        blogResourcesJson = """[
                            {"name": "Houseplant Journal Masterclass", "url": "houseplantjournal.com", "desc": "Understanding indirect light vs foot-candles"},
                            {"name": "The Sill Plant Doctor Tips", "url": "thesill.com/care", "desc": "Diagnosing yellow leaves and humidity needs"}
                        ]"""
                    ),
                    SkillTutorial(
                        id = "goal_sourdough",
                        title = "Artisan Sourdough & Baking",
                        subtitle = "Cultivate wild yeast, master autolyse, and score crusty ear loaves",
                        category = "Cooking & Culinary",
                        estimatedTimeToMaster = "1 Month",
                        masteryLevel = 1,
                        iconEmoji = "🍞",
                        stepsJson = """[
                            "1. Feed starter at 1:1:1 ratio (flour, water, starter) and wait 4 hours until bubbling.",
                            "2. Mix flour and lukewarm water for a 45-minute gentle autolyse rest.",
                            "3. Fold in active starter and fine salt with wet hands.",
                            "4. Perform 4 sets of stretch-and-folds every 30 minutes.",
                            "5. Shape into a tight round boule, cold ferment overnight in the fridge!"
                        ]""",
                        blogResourcesJson = """[
                            {"name": "The Perfect Loaf Guide", "url": "theperfectloaf.com", "desc": "Visual sourdough fermentation schedules"},
                            {"name": "King Arthur Baking Community", "url": "kingarthurbaking.com", "desc": "Troubleshooting oven spring and crumb structure"}
                        ]"""
                    )
                )
                dao.insertTutorials(defaultTutorials)

                // Seed Badges
                val defaultBadges = listOf(
                    BadgeItem(
                        id = "badge_streak_3",
                        title = "Cozy Spark 3-Day",
                        description = "Maintained a 3-day continuous habit momentum",
                        iconEmoji = "🔥",
                        category = "STREAK",
                        unlocked = true,
                        unlockedTimestamp = System.currentTimeMillis() - 86400000L * 2,
                        badgeTier = 1
                    ),
                    BadgeItem(
                        id = "badge_origami_fold",
                        title = "Origami Master",
                        description = "Folded 10 wardrobe items using the Japanese vertical method",
                        iconEmoji = "🧺",
                        category = "SKILL",
                        unlocked = true,
                        unlockedTimestamp = System.currentTimeMillis() - 86400000L,
                        badgeTier = 1
                    ),
                    BadgeItem(
                        id = "badge_chef_whisk",
                        title = "Sizzle & Chop",
                        description = "Completed 5 culinary prep tasks with proof verification",
                        iconEmoji = "🍳",
                        category = "SKILL",
                        unlocked = true,
                        unlockedTimestamp = System.currentTimeMillis() - 40000000L,
                        badgeTier = 2
                    ),
                    BadgeItem(
                        id = "badge_green_thumb",
                        title = "Sprout Guardian",
                        description = "Nurtured 3 plants through bottom-watering rituals",
                        iconEmoji = "🌿",
                        category = "SKILL",
                        unlocked = true,
                        unlockedTimestamp = System.currentTimeMillis() - 10000000L,
                        badgeTier = 1
                    ),
                    BadgeItem(
                        id = "badge_squad_legend",
                        title = "Squad Harmony",
                        description = "Finished a 5-member co-op task sync in record time",
                        iconEmoji = "👑",
                        category = "COOP",
                        unlocked = false,
                        badgeTier = 3
                    ),
                    BadgeItem(
                        id = "badge_monthly_artisan",
                        title = "1-Month Master",
                        description = "Mastered an entire life skill track for over 30 days",
                        iconEmoji = "🏆",
                        category = "SKILL",
                        unlocked = false,
                        badgeTier = 3
                    )
                )
                dao.insertBadges(defaultBadges)

                // Seed Hatched Pets in Paradise
                val defaultPets = listOf(
                    HatchedPet(
                        name = "Sparky",
                        species = "Mini Dragon",
                        rarity = PetRarity.LEGENDARY,
                        accessory = PetAccessory.FLOWER_CROWN,
                        personality = "Loves toasted marshmallows and gentle flight spins.",
                        hungerLevel = 85,
                        happinessLevel = 95,
                        affectionLevel = 3,
                        isBuddy = true,
                        xPosRatio = 0.28f,
                        yPosRatio = 0.42f,
                        streakAtHatch = 3
                    ),
                    HatchedPet(
                        name = "Mochi",
                        species = "Cosmic Bunny",
                        rarity = PetRarity.LEGENDARY,
                        accessory = PetAccessory.STAR_GLASSES,
                        personality = "Bounces on fluffy moon clouds and eats starry clover.",
                        hungerLevel = 70,
                        happinessLevel = 88,
                        affectionLevel = 2,
                        isBuddy = false,
                        xPosRatio = 0.65f,
                        yPosRatio = 0.55f,
                        streakAtHatch = 2
                    ),
                    HatchedPet(
                        name = "Barnaby",
                        species = "Cloud Sloth",
                        rarity = PetRarity.SLACKER_DOWNGRADE,
                        accessory = PetAccessory.COZY_SCARF,
                        personality = "Takes adorable 4-hour naps while cheering the team on softly.",
                        hungerLevel = 90,
                        happinessLevel = 65,
                        affectionLevel = 1,
                        isBuddy = false,
                        xPosRatio = 0.45f,
                        yPosRatio = 0.72f,
                        streakAtHatch = 1
                    )
                )
                dao.insertPets(defaultPets)

                // Seed Oasis Meta
                dao.saveOasisMeta(
                    OasisMeta(
                        id = 1,
                        currentStreak = 4,
                        timerSecondsRemaining = 240,
                        initialTimerSeconds = 300,
                        timerRunning = true,
                        eggCracks = 2,
                        eggState = "PULSING",
                        seasonTheme = "SPRING",
                        isNightMode = false
                    )
                )
            }
        }
}
