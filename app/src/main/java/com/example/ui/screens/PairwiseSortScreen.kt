package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEffectManager
import com.example.data.model.GroupMember
import com.example.data.model.UserProfile
import com.example.ui.components.PoppableButton
import com.example.ui.components.squishClickable
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MatchupCardOption(
    val title: String,
    val emoji: String,
    val description: String,
    val tag: String,
    val bgGradient: List<Color>,
    val borderCol: Color
)

data class MatchupQuizQuestion(
    val id: Int,
    val categoryTitle: String,
    val questionText: String,
    val cardA: MatchupCardOption,
    val cardB: MatchupCardOption
)

data class MatchedSquadInfo(
    val squadName: String,
    val squadCode: String,
    val overallMatchPercent: Int,
    val summary: String,
    val teammates: List<MatchedTeammate>
)

data class MatchedTeammate(
    val name: String,
    val avatarEmoji: String,
    val avatarColorHex: Long,
    val personalityArchetype: String,
    val matchPercent: Int
)

@Composable
fun PairwiseSortScreen(
    userProfile: UserProfile?,
    members: List<GroupMember>,
    onJoinSquadWithCode: (code: String) -> Unit,
    onJoinMatchedSquad: (squadName: String, squadCode: String, matchedMembers: List<GroupMember>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedOptionTab by remember { mutableStateOf(0) } // 0 = Friends with Code, 1 = Online Friends (6 Choices)
    var enteredCode by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 6-question quiz state
    val questions = remember {
        listOf(
            MatchupQuizQuestion(
                id = 1,
                categoryTitle = "Daily Rhythm",
                questionText = "When do you feel most inspired to get things done?",
                cardA = MatchupCardOption(
                    title = "Morning Energy",
                    emoji = "🌅",
                    description = "Sunrise momentum, getting daily tasks done early.",
                    tag = "EARLY",
                    bgGradient = listOf(Color(0xFFFFF8E7), Color(0xFFFFF1CC)),
                    borderCol = EggAmber
                ),
                cardB = MatchupCardOption(
                    title = "Night Owl Flow",
                    emoji = "🌙",
                    description = "Peaceful evening vibes, steady nighttime focus.",
                    tag = "NIGHT",
                    bgGradient = listOf(Color(0xFFF3EDFF), Color(0xFFE5D4FF)),
                    borderCol = LilacDark
                )
            ),
            MatchupQuizQuestion(
                id = 2,
                categoryTitle = "Habit Focus",
                questionText = "Which daily chore brings you the most satisfaction?",
                cardA = MatchupCardOption(
                    title = "Tidy & Organized",
                    emoji = "🧹",
                    description = "Decluttering surfaces, neat drawers, and clean floors.",
                    tag = "ORGANIZING",
                    bgGradient = listOf(Color(0xFFE8F7FF), Color(0xFFD0EFFF)),
                    borderCol = SkyBlue
                ),
                cardB = MatchupCardOption(
                    title = "Nourishing Cooking",
                    emoji = "🍳",
                    description = "Prepping healthy ingredients and cooking fresh meals.",
                    tag = "COOKING",
                    bgGradient = listOf(Color(0xFFFFF3E6), Color(0xFFFFDEC2)),
                    borderCol = Color(0xFFF09A58)
                )
            ),
            MatchupQuizQuestion(
                id = 3,
                categoryTitle = "Sanctuary Vibe",
                questionText = "What kind of home environment makes you feel happiest?",
                cardA = MatchupCardOption(
                    title = "Green Plant Haven",
                    emoji = "🌿",
                    description = "Lush indoor greenery, fresh air, and mindful plant care.",
                    tag = "PLANTS",
                    bgGradient = listOf(Color(0xFFEAF9F1), Color(0xFFC7F3DE)),
                    borderCol = MintGreenDark
                ),
                cardB = MatchupCardOption(
                    title = "Hands-on DIY & Craft",
                    emoji = "🛠️",
                    description = "Fixing squeaks, crafting home setups, building habits.",
                    tag = "FIXING",
                    bgGradient = listOf(Color(0xFFFFF0F5), Color(0xFFFFDDE7)),
                    borderCol = BlushPinkDark
                )
            ),
            MatchupQuizQuestion(
                id = 4,
                categoryTitle = "Team Energy",
                questionText = "How do you prefer to complete your daily goals?",
                cardA = MatchupCardOption(
                    title = "Active Squad Sync",
                    emoji = "👥",
                    description = "Cheering friends on, co-op sync, and shared energy.",
                    tag = "COOP",
                    bgGradient = listOf(Color(0xFFFDF6E2), Color(0xFFFBEBC4)),
                    borderCol = EggAmber
                ),
                cardB = MatchupCardOption(
                    title = "Quiet Solo Peace",
                    emoji = "🧘",
                    description = "Calm independent focus, no pressure, serene rhythm.",
                    tag = "SOLO",
                    bgGradient = listOf(Color(0xFFF5EEFF), Color(0xFFEAD8FF)),
                    borderCol = LilacDark
                )
            ),
            MatchupQuizQuestion(
                id = 5,
                categoryTitle = "Daily Pace",
                questionText = "What style of daily habit routine fits you best?",
                cardA = MatchupCardOption(
                    title = "Steady Daily Steps",
                    emoji = "⏱️",
                    description = "Consistent small daily checkpoints to build momentum.",
                    tag = "STEADY",
                    bgGradient = listOf(Color(0xFFEBF8FA), Color(0xFFCEF1F5)),
                    borderCol = MintGreenDark
                ),
                cardB = MatchupCardOption(
                    title = "Gentle Flexible Flow",
                    emoji = "🌊",
                    description = "Relaxed pace that adapts naturally to your schedule.",
                    tag = "FLEXIBLE",
                    bgGradient = listOf(Color(0xFFFFF5EE), Color(0xFFFFE4D6)),
                    borderCol = Color(0xFFF5A376)
                )
            ),
            MatchupQuizQuestion(
                id = 6,
                categoryTitle = "Favorite Reward",
                questionText = "Which reward motivates you most when you finish tasks?",
                cardA = MatchupCardOption(
                    title = "Cute Pet Companions",
                    emoji = "🐣",
                    description = "Hatching and caring for dragons, sloths, and bunnies.",
                    tag = "PETS",
                    bgGradient = listOf(Color(0xFFFFF0F7), Color(0xFFFFD6EA)),
                    borderCol = BlushPinkDark
                ),
                cardB = MatchupCardOption(
                    title = "Life Skill Badges",
                    emoji = "🏆",
                    description = "Mastering guides, unlocking trophies, and tracking milestones.",
                    tag = "BADGES",
                    bgGradient = listOf(Color(0xFFFFFDE8), Color(0xFFFFF9B8)),
                    borderCol = EggAmber
                )
            )
        )
    }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedChoices by remember { mutableStateOf(mutableListOf<String>()) }
    var isCalculatingMatch by remember { mutableStateOf(false) }
    var matchedSquadResult by remember { mutableStateOf<MatchedSquadInfo?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header
        item {
            Column {
                Text(
                    text = "Squad Matchup 🤝",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MintGreenDark
                )
                Text(
                    text = "Join friends using a code or match with compatible online peers.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Option Switcher Tabs (Two Clear Options)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(CloudWhite)
                        .border(1.5.dp, MintGreenLight, RoundedCornerShape(18.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Option 1 Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selectedOptionTab == 0) MintGreenPrimary else Color.Transparent)
                            .clickable {
                                selectedOptionTab = 0
                                SoundEffectManager.playPop()
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔑", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Friends with Code",
                                fontSize = 11.sp,
                                fontWeight = if (selectedOptionTab == 0) FontWeight.Black else FontWeight.Bold,
                                color = if (selectedOptionTab == 0) MintGreenDark else SlateMuted
                            )
                        }
                    }

                    // Option 2 Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selectedOptionTab == 1) MintGreenPrimary else Color.Transparent)
                            .clickable {
                                selectedOptionTab = 1
                                SoundEffectManager.playPop()
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌐", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Online Match (Quiz)",
                                fontSize = 11.sp,
                                fontWeight = if (selectedOptionTab == 1) FontWeight.Black else FontWeight.Bold,
                                color = if (selectedOptionTab == 1) MintGreenDark else SlateMuted
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // OPTION 1: FRIENDS WITH CODE
        // ==========================================
        if (selectedOptionTab == 0) {
            item {
                // Card A: Enter Code to Join
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(24.dp), spotColor = MintGreenDark.copy(alpha = 0.15f))
                        .clip(RoundedCornerShape(24.dp))
                        .background(CloudWhite)
                        .border(1.5.dp, MintGreenLight, RoundedCornerShape(24.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MintGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = MintGreenDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Join with a Friend's Code",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SlateText
                                )
                                Text(
                                    text = "Enter a code shared by your friend",
                                    fontSize = 11.sp,
                                    color = SlateMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = enteredCode,
                            onValueChange = { enteredCode = it.uppercase() },
                            placeholder = {
                                Text(
                                    text = "e.g. OASIS-7X29",
                                    fontSize = 13.sp,
                                    color = SlateMuted
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MintGreenDark,
                                unfocusedBorderColor = MintGreenLight,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PoppableButton(
                            text = "Join Squad 🚀",
                            onClick = {
                                val clean = enteredCode.trim().uppercase()
                                if (clean.isNotEmpty()) {
                                    onJoinSquadWithCode(clean)
                                    Toast.makeText(context, "Joined Squad $clean!", Toast.LENGTH_SHORT).show()
                                    enteredCode = ""
                                } else {
                                    Toast.makeText(context, "Please enter a valid squad code", Toast.LENGTH_SHORT).show()
                                }
                            },
                            backgroundColor = MintGreenPrimary,
                            bottomBorderColor = MintGreenBorderBottom,
                            contentColor = MintGreenDark,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                // Card B: Your Invite Code (Share with Friends)
                val currentCode = userProfile?.squadCode ?: "OASIS-7X29"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(24.dp), spotColor = EggAmber.copy(alpha = 0.2f))
                        .clip(RoundedCornerShape(24.dp))
                        .background(EggYellowLight)
                        .border(1.5.dp, EggAmber, RoundedCornerShape(24.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "💌", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Your Team Invite Code",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = AmberTextDark
                                    )
                                    Text(
                                        text = "Share this code so friends can team up with you",
                                        fontSize = 10.sp,
                                        color = SlateMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(CloudWhite)
                                .border(1.dp, EggAmber, RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentCode,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = AmberTextDeep
                            )

                            Box(
                                modifier = Modifier
                                    .squishClickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Squad Code", currentCode)
                                        clipboard.setPrimaryClip(clip)
                                        SoundEffectManager.playPop()
                                        Toast.makeText(context, "Copied code $currentCode!", Toast.LENGTH_SHORT).show()
                                    }
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EggYellowMid)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = AmberTextDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Copy",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberTextDark
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Card C: Active Squad Members
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current Squad: ${userProfile?.squadName ?: "Pastel Squad"}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = SlateText
                        )
                        Text(
                            text = "${members.size} Members",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MintGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    members.forEach { member ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(CloudWhite)
                                .border(1.dp, MintGreenLight, RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(member.avatarColorHex).copy(alpha = 0.6f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = member.avatarEmoji, fontSize = 18.sp)
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = member.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SlateText
                                        )
                                        if (member.isCurrentActiveUser) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(MintGreenLight)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "You",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = MintGreenDark
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = member.personalityArchetype,
                                        fontSize = 10.sp,
                                        color = SlateMuted
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(EggYellowLight)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "✓ ${member.tasksCompletedCount} done",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberTextDeep
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // OPTION 2: ONLINE FRIENDS (6 CHOICES QUIZ)
        // ==========================================
        if (selectedOptionTab == 1) {
            if (matchedSquadResult == null && !isCalculatingMatch) {
                val currentQ = questions[currentQuestionIndex]

                item {
                    // Question Counter & Progress
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(CloudWhite)
                            .border(1.5.dp, MintGreenLight, RoundedCornerShape(18.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "QUESTION ${currentQuestionIndex + 1} OF 6: ${currentQ.categoryTitle.uppercase()}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.1.sp,
                                    color = MintGreenDark
                                )
                                Text(
                                    text = "${((currentQuestionIndex + 1) * 100) / 6}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SlateMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Linear Progress Bar
                            LinearProgressIndicator(
                                progress = { (currentQuestionIndex + 1) / 6f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MintGreenDark,
                                trackColor = MintGreenLight
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = currentQ.questionText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateText
                            )
                        }
                    }
                }

                // Card A Choice
                item {
                    QuizCardChoice(
                        option = currentQ.cardA,
                        onClick = {
                            selectedChoices.add(currentQ.cardA.tag)
                            SoundEffectManager.playPop()
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex += 1
                            } else {
                                // Finished all 6 choices -> calculate match
                                isCalculatingMatch = true
                                coroutineScope.launch {
                                    delay(1200)
                                    isCalculatingMatch = false
                                    matchedSquadResult = generateMatchedSquad(selectedChoices)
                                    SoundEffectManager.playFanfare()
                                }
                            }
                        }
                    )
                }

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CloudWhite)
                                .border(1.5.dp, MintGreenLight, CircleShape)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "— OR —",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = SlateMuted
                            )
                        }
                    }
                }

                // Card B Choice
                item {
                    QuizCardChoice(
                        option = currentQ.cardB,
                        onClick = {
                            selectedChoices.add(currentQ.cardB.tag)
                            SoundEffectManager.playPop()
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex += 1
                            } else {
                                // Finished all 6 choices -> calculate match
                                isCalculatingMatch = true
                                coroutineScope.launch {
                                    delay(1200)
                                    isCalculatingMatch = false
                                    matchedSquadResult = generateMatchedSquad(selectedChoices)
                                    SoundEffectManager.playFanfare()
                                }
                            }
                        }
                    )
                }
            } else if (isCalculatingMatch) {
                // Calculating animation state
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(CloudWhite)
                            .border(2.dp, MintGreenDark, RoundedCornerShape(28.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "✨", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Finding Your 60%+ Compatible Squad...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                color = MintGreenDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Analyzing your 6 value answers against active online daily routine seekers...",
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                color = SlateMuted
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator(
                                color = MintGreenDark,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            } else if (matchedSquadResult != null) {
                // Matched Squad Result!
                val squad = matchedSquadResult!!

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(28.dp), spotColor = MintGreenDark.copy(alpha = 0.25f))
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFEAF9F1),
                                        Color(0xFFF6FFF9),
                                        CloudWhite
                                    )
                                )
                            )
                            .border(2.5.dp, MintGreenDark, RoundedCornerShape(28.dp))
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MintGreenLight)
                                        .border(1.dp, MintGreenDark, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "🎉 MATCH FOUND",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        color = MintGreenDark
                                    )
                                }

                                Text(
                                    text = "🔥 ${squad.overallMatchPercent}% Value Match",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MintGreenDark
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = squad.squadName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = SlateText
                            )

                            Text(
                                text = squad.summary,
                                fontSize = 11.sp,
                                color = SlateMuted
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Matched Teammates (≥ 60% Similarity):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = SlateText
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            squad.teammates.forEach { mate ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(CloudWhite)
                                        .border(1.dp, MintGreenLight, RoundedCornerShape(14.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(Color(mate.avatarColorHex).copy(alpha = 0.6f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = mate.avatarEmoji, fontSize = 16.sp)
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = mate.name,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SlateText
                                            )
                                            Text(
                                                text = mate.personalityArchetype,
                                                fontSize = 10.sp,
                                                color = SlateMuted
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFE6FAF1))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "${mate.matchPercent}% Match",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = MintGreenDark
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action: Join this group
                            PoppableButton(
                                text = "Join This Matched Group 🎉",
                                onClick = {
                                    val groupMembers = squad.teammates.map { mate ->
                                        GroupMember(
                                            name = mate.name,
                                            avatarEmoji = mate.avatarEmoji,
                                            avatarColorHex = mate.avatarColorHex,
                                            personalityArchetype = mate.personalityArchetype,
                                            primaryInterest = "Daily Routine",
                                            tasksCompletedCount = (1..3).random(),
                                            isCurrentActiveUser = false
                                        )
                                    }
                                    onJoinMatchedSquad(squad.squadName, squad.squadCode, groupMembers)
                                    Toast.makeText(context, "Joined ${squad.squadName}!", Toast.LENGTH_SHORT).show()
                                    selectedOptionTab = 0
                                },
                                backgroundColor = MintGreenPrimary,
                                bottomBorderColor = MintGreenBorderBottom,
                                contentColor = MintGreenDark,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Retake quiz button
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentQuestionIndex = 0
                                        selectedChoices.clear()
                                        matchedSquadResult = null
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Retake 6-Question Quiz 🔄",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizCardChoice(
    option: MatchupCardOption,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .squishClickable(onClick = onClick)
            .shadow(3.dp, RoundedCornerShape(22.dp), spotColor = option.borderCol.copy(alpha = 0.25f))
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(colors = option.bgGradient)
            )
            .border(2.dp, option.borderCol, RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.8f))
                    .border(1.5.dp, option.borderCol, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = option.emoji, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = SlateText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = option.description,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = SlateMuted
                )
            }

            Text(
                text = "Choose →",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = option.borderCol
            )
        }
    }
}

private fun generateMatchedSquad(choices: List<String>): MatchedSquadInfo {
    val isEarly = choices.contains("EARLY")
    val isPlants = choices.contains("PLANTS")
    val isCooking = choices.contains("COOKING")

    return if (isPlants || isEarly) {
        MatchedSquadInfo(
            squadName = "Sunlit Bloomers Squad",
            squadCode = "BLOOM-84X",
            overallMatchPercent = 82,
            summary = "Peers who thrive on morning sunlight, plant hydration rituals, and clean spaces.",
            teammates = listOf(
                MatchedTeammate("Aria", "🌸", 0xFFFFB6C1, "The Plant Whisperer", 88),
                MatchedTeammate("Felix", "🌿", 0xFFA8E6CF, "The Cozy Cultivator", 82),
                MatchedTeammate("Maya", "🍳", 0xFFFFF9A6, "The Culinary Artisan", 74),
                MatchedTeammate("Leo", "🎀", 0xFFE8D7FF, "The Mindful Minimalist", 68)
            )
        )
    } else if (isCooking) {
        MatchedSquadInfo(
            squadName = "Cozy Hearth Squad",
            squadCode = "HEARTH-29Y",
            overallMatchPercent = 78,
            summary = "Peers passionate about healthy batch-cooking, kitchen prep, and warm community vibes.",
            teammates = listOf(
                MatchedTeammate("Kai", "🍳", 0xFFFFF9A6, "The Culinary Artisan", 85),
                MatchedTeammate("Chloe", "🎀", 0xFFE8D7FF, "The Mindful Minimalist", 78),
                MatchedTeammate("Milo", "🛠️", 0xFFFFD1DC, "The Handy Crafter", 72),
                MatchedTeammate("Nova", "🌸", 0xFFFFB6C1, "The Cozy Cultivator", 66)
            )
        )
    } else {
        MatchedSquadInfo(
            squadName = "Zen Starlight Squad",
            squadCode = "STAR-91Z",
            overallMatchPercent = 75,
            summary = "Peers dedicated to gentle daily pacing, mindful breathing, and organized desks.",
            teammates = listOf(
                MatchedTeammate("Soren", "🌙", 0xFFE8D7FF, "The Mindful Minimalist", 86),
                MatchedTeammate("Luna", "🌿", 0xFFA8E6CF, "The Plant Whisperer", 79),
                MatchedTeammate("Jasper", "🧺", 0xFFFFF9A6, "The Energetic Organizer", 71),
                MatchedTeammate("Nina", "🎨", 0xFFFFB6C1, "The Cozy Cultivator", 64)
            )
        )
    }
}
