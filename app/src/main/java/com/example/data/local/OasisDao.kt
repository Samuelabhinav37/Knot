package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OasisDao {

    // --- Chores ---
    @Query("SELECT * FROM chores ORDER BY score DESC, createdAt ASC")
    fun getAllChores(): Flow<List<ChoreItem>>

    @Query("SELECT * FROM chores WHERE isCompleted = 0 ORDER BY score DESC, createdAt ASC LIMIT 5")
    fun getCoreFiveActiveChores(): Flow<List<ChoreItem>>

    @Query("SELECT * FROM chores WHERE isCompleted = 0 ORDER BY score DESC")
    suspend fun getActiveChoresList(): List<ChoreItem>

    @Query("SELECT * FROM chores WHERE id = :id")
    suspend fun getChoreById(id: Long): ChoreItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChore(chore: ChoreItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChores(chores: List<ChoreItem>)

    @Update
    suspend fun updateChore(chore: ChoreItem)

    @Query("UPDATE chores SET score = :newScore, matchupWins = matchupWins + :winInc, matchupTotal = matchupTotal + 1 WHERE id = :id")
    suspend fun updateMatchupScore(id: Long, newScore: Int, winInc: Int)

    @Query("UPDATE chores SET isCompleted = 1, completedBy = :completedBy, completedTimestamp = :timestamp WHERE id = :id")
    suspend fun markChoreCompleted(id: Long, completedBy: String, timestamp: Long)

    @Query("UPDATE chores SET beforePhotoUri = :beforeUri, afterPhotoUri = :afterUri, isVerified = 1 WHERE id = :id")
    suspend fun attachProofOfWork(id: Long, beforeUri: String?, afterUri: String?)

    @Query("DELETE FROM chores WHERE id = :id")
    suspend fun deleteChore(id: Long)

    // --- Group Members ---
    @Query("SELECT * FROM group_members")
    fun getAllMembers(): Flow<List<GroupMember>>

    @Query("SELECT * FROM group_members")
    suspend fun getMembersList(): List<GroupMember>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<GroupMember>)

    @Query("UPDATE group_members SET tasksCompletedCount = tasksCompletedCount + 1 WHERE name = :memberName")
    suspend fun incrementMemberTaskCount(memberName: String)

    @Query("UPDATE group_members SET isCurrentActiveUser = (name = :activeName)")
    suspend fun setActiveUser(activeName: String)

    @Query("UPDATE group_members SET tasksCompletedCount = 0")
    suspend fun resetMemberTaskCounts()

    // --- Hatched Pets & Buddy Mode ---
    @Query("SELECT * FROM hatched_pets ORDER BY isBuddy DESC, hatchedTimestamp DESC")
    fun getAllPets(): Flow<List<HatchedPet>>

    @Query("SELECT * FROM hatched_pets WHERE isBuddy = 1 LIMIT 1")
    fun getActiveBuddy(): Flow<HatchedPet?>

    @Query("SELECT * FROM hatched_pets WHERE id = :id")
    suspend fun getPetById(id: Long): HatchedPet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: HatchedPet): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPets(pets: List<HatchedPet>)

    @Query("UPDATE hatched_pets SET accessory = :accessory WHERE id = :petId")
    suspend fun updatePetAccessory(petId: Long, accessory: String)

    @Query("UPDATE hatched_pets SET xPosRatio = :x, yPosRatio = :y WHERE id = :petId")
    suspend fun updatePetPosition(petId: Long, x: Float, y: Float)

    @Query("UPDATE hatched_pets SET isBuddy = (id = :petId)")
    suspend fun setPetAsBuddy(petId: Long)

    @Query("UPDATE hatched_pets SET hungerLevel = MIN(100, hungerLevel + :amount), happinessLevel = MIN(100, happinessLevel + 5) WHERE id = :petId")
    suspend fun feedPet(petId: Long, amount: Int)

    @Query("UPDATE hatched_pets SET happinessLevel = MIN(100, happinessLevel + :amount), affectionLevel = affectionLevel + 1 WHERE id = :petId")
    suspend fun petAndPlay(petId: Long, amount: Int)

    // --- User Profile ---
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET currentXp = currentXp + :xp")
    suspend fun addXp(xp: Int)

    // --- Badges ---
    @Query("SELECT * FROM badges ORDER BY unlocked DESC, badgeTier ASC")
    fun getAllBadges(): Flow<List<BadgeItem>>

    @Query("SELECT COUNT(*) FROM badges WHERE unlocked = 1")
    suspend fun getUnlockedBadgeCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<BadgeItem>)

    @Query("UPDATE badges SET unlocked = 1, unlockedTimestamp = :timestamp WHERE id = :badgeId")
    suspend fun unlockBadge(badgeId: String, timestamp: Long = System.currentTimeMillis())

    // --- Skill Tutorials ---
    @Query("SELECT * FROM skill_tutorials")
    fun getAllTutorials(): Flow<List<SkillTutorial>>

    @Query("SELECT * FROM skill_tutorials WHERE id = :id")
    suspend fun getTutorialById(id: String): SkillTutorial?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTutorials(tutorials: List<SkillTutorial>)

    @Query("UPDATE skill_tutorials SET masteryLevel = MIN(4, masteryLevel + 1) WHERE id = :id")
    suspend fun levelUpSkill(id: String)

    // --- Oasis Meta ---
    @Query("SELECT * FROM oasis_meta WHERE id = 1")
    fun getOasisMeta(): Flow<OasisMeta?>

    @Query("SELECT * FROM oasis_meta WHERE id = 1")
    suspend fun getOasisMetaSync(): OasisMeta?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOasisMeta(meta: OasisMeta)

    /**
     * Reads the current row and writes back the transformed copy inside a single DB
     * transaction, so a concurrent chore completion/timer tick can't read a stale
     * snapshot and clobber the other's write.
     */
    @Transaction
    suspend fun updateOasisMeta(mutate: (OasisMeta) -> OasisMeta) {
        val current = getOasisMetaSync() ?: OasisMeta()
        saveOasisMeta(mutate(current))
    }

    // --- Chore completion (atomic: marks chore done, awards XP once, updates egg + level) ---
    @Transaction
    suspend fun completeChoreTx(choreId: Long, completedBy: String): Pair<Int, Boolean> {
        val chore = getChoreById(choreId)
        val xpGain = chore?.difficulty?.xp ?: 100

        markChoreCompleted(choreId, completedBy, System.currentTimeMillis())
        incrementMemberTaskCount(completedBy)
        addXp(xpGain)

        chore?.tutorialId?.let { tutId -> levelUpSkill(tutId) }

        val meta = getOasisMetaSync() ?: OasisMeta()
        val updatedCracks = (meta.eggCracks + 1).coerceAtMost(5)
        val shouldHatch = updatedCracks >= 5
        saveOasisMeta(
            meta.copy(
                eggCracks = updatedCracks,
                eggState = if (shouldHatch) "HATCHED" else "PULSING"
            )
        )

        val profile = getUserProfileSync()
        if (profile != null) {
            val newLevel = (profile.currentXp / 500) + 1
            if (newLevel != profile.level) {
                saveUserProfile(profile.copy(level = newLevel))
            }
        }

        return updatedCracks to shouldHatch
    }
}
