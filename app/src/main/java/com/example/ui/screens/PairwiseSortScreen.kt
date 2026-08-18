package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEffectManager
import com.example.data.model.GroupMember
import com.example.data.model.UserProfile
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CadreTaskOption(
    val title: String,
    val description: String,
    val domain: String
)

data class CadreQuizQuestion(
    val id: Int,
    val domainTitle: String,
    val questionText: String,
    val cardA: CadreTaskOption,
    val cardB: CadreTaskOption
)

data class CadreMatchResult(
    val squadName: String,
    val squadCode: String,
    val overallMatchPercent: Int,
    val summary: String,
    val teammates: List<CadreTeammate>
)

data class CadreTeammate(
    val name: String,
    val role: String,
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
    var selectedOptionTab by remember { mutableStateOf(0) } // 0 = Team Code, 1 = Workflow Alignment (6 Choices)
    var enteredCode by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 6-question quiz state with clean, strong professional verbs and NO '&'
    val questions = remember {
        listOf(
            CadreQuizQuestion(
                id = 1,
                domainTitle = "Operational Focus",
                questionText = "Select your preferred daily work rhythm:",
                cardA = CadreTaskOption(
                    title = "Morning Deep Work",
                    description = "Execute core critical priorities early in the day",
                    domain = "Focus"
                ),
                cardB = CadreTaskOption(
                    title = "Afternoon Sprint",
                    description = "Maintain steady momentum through structured afternoon sessions",
                    domain = "Execution"
                )
            ),
            CadreQuizQuestion(
                id = 2,
                domainTitle = "Task Organization",
                questionText = "Select your task structuring preference:",
                cardA = CadreTaskOption(
                    title = "Standardized Checklists",
                    description = "Follow systematic itemized task sequences",
                    domain = "Organization"
                ),
                cardB = CadreTaskOption(
                    title = "Dynamic Priority Boards",
                    description = "Adapt deliverables fluidly as needs evolve",
                    domain = "Agile"
                )
            ),
            CadreQuizQuestion(
                id = 3,
                domainTitle = "Communication Channel",
                questionText = "Select your team coordination style:",
                cardA = CadreTaskOption(
                    title = "Structured Daily Standup",
                    description = "Align directly on daily blockers and progress",
                    domain = "Collaboration"
                ),
                cardB = CadreTaskOption(
                    title = "Asynchronous Logs",
                    description = "Document clear written status updates independently",
                    domain = "Documentation"
                )
            ),
            CadreQuizQuestion(
                id = 4,
                domainTitle = "Execution Method",
                questionText = "Select your task completion strategy:",
                cardA = CadreTaskOption(
                    title = "Iterative Milestones",
                    description = "Deliver work in rapid, incremental checkpoints",
                    domain = "Delivery"
                ),
                cardB = CadreTaskOption(
                    title = "Comprehensive Completion",
                    description = "Finish end-to-end deliverables thoroughly before review",
                    domain = "Quality"
                )
            ),
            CadreQuizQuestion(
                id = 5,
                domainTitle = "Team Synergy",
                questionText = "Select your peer collaboration approach:",
                cardA = CadreTaskOption(
                    title = "Cross-Functional Review",
                    description = "Validate outputs through multi-disciplinary feedback",
                    domain = "Review"
                ),
                cardB = CadreTaskOption(
                    title = "Autonomous Execution",
                    description = "Drive objectives forward with self-directed ownership",
                    domain = "Autonomy"
                )
            ),
            CadreQuizQuestion(
                id = 6,
                domainTitle = "Continuous Improvement",
                questionText = "Select your evaluation standard:",
                cardA = CadreTaskOption(
                    title = "Daily Routine Audit",
                    description = "Refine individual consistency and habit metrics daily",
                    domain = "Habits"
                ),
                cardB = CadreTaskOption(
                    title = "Weekly Retrospective",
                    description = "Review collective team outcomes at each cycle end",
                    domain = "Strategy"
                )
            )
        )
    }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() } // questionId -> chosen card (0 for A, 1 for B)
    var isCalculatingMatch by remember { mutableStateOf(false) }
    var matchResult by remember { mutableStateOf<CadreMatchResult?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CorporateBg)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Cadre Workspace",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Team synchronization and collaborative workflow matching.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }
        }

        // Clean Segmented Tabs (Option 1 vs Option 2)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CorporateSurface)
                    .border(1.dp, CorporateCardBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedOptionTab == 0) CorporatePrimary else Color.Transparent)
                        .clickable {
                            selectedOptionTab = 0
                            SoundEffectManager.playPop()
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Team Code",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedOptionTab == 0) Color.White else TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedOptionTab == 1) CorporatePrimary else Color.Transparent)
                        .clickable {
                            selectedOptionTab = 1
                            SoundEffectManager.playPop()
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Workflow Match",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedOptionTab == 1) Color.White else TextSecondary
                    )
                }
            }
        }

        // Option 1: Team Code
        if (selectedOptionTab == 0) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CorporateSurface)
                        .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CorporateAccentBlueLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = "Code",
                                    tint = CorporateAccentBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Join with Invite Code",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Connect with colleagues using a squad code",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        OutlinedTextField(
                            value = enteredCode,
                            onValueChange = { enteredCode = it.uppercase() },
                            placeholder = { Text("e.g. CADRE-8X9") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CorporatePrimary,
                                unfocusedBorderColor = CorporateCardBorder
                            )
                        )

                        Button(
                            onClick = {
                                if (enteredCode.isNotBlank()) {
                                    SoundEffectManager.playPop()
                                    onJoinSquadWithCode(enteredCode.trim())
                                    Toast.makeText(context, "Connected to ${enteredCode.trim()}", Toast.LENGTH_SHORT).show()
                                    enteredCode = ""
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CorporatePrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Connect to Team",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Current Squad Share Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CorporateSurface)
                        .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Your Active Squad",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        val currentCode = userProfile?.squadCode ?: "CADRE-8X9"
                        val currentName = userProfile?.squadName ?: "Cadre Unit Alpha"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CorporateBg)
                                .border(1.dp, CorporateCardBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = currentName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Code: $currentCode",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Squad Code", currentCode))
                                    Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Text(
                            text = "${members.size} active colleagues currently in this workspace.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Option 2: 6-Question Workflow Alignment Quiz
        if (selectedOptionTab == 1) {
            if (isCalculatingMatch) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CorporateSurface)
                            .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = CorporatePrimary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Evaluating Alignment...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Matching with professionals sharing 60%+ workflow alignment",
                                fontSize = 12.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else if (matchResult != null) {
                // Display Match Result
                val res = matchResult!!
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CorporateSurface)
                            .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Match Confirmed",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CorporateSuccess
                                    )
                                    Text(
                                        text = res.squadName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(CorporateSuccessLight)
                                        .border(1.dp, CorporateSuccess.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${res.overallMatchPercent}% Match",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CorporateSuccess
                                    )
                                }
                            }

                            Text(
                                text = res.summary,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )

                            Divider(color = CorporateCardBorder, thickness = 0.8.dp)

                            Text(
                                text = "Matched Team Members:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )

                            res.teammates.forEach { tm ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = tm.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = tm.role,
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }

                                    Text(
                                        text = "${tm.matchPercent}% aligned",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = CorporateSuccess
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val newMembers = res.teammates.mapIndexed { idx, tm ->
                                        GroupMember(
                                            name = tm.name,
                                            avatarEmoji = "👤",
                                            avatarColorHex = 0xFF1E293B,
                                            personalityArchetype = tm.role,
                                            primaryInterest = "Operations",
                                            tasksCompletedCount = 12 + idx * 4,
                                            isCurrentActiveUser = false
                                        )
                                    }
                                    onJoinMatchedSquad(res.squadName, res.squadCode, newMembers)
                                    Toast.makeText(context, "Joined ${res.squadName}", Toast.LENGTH_SHORT).show()
                                    matchResult = null
                                    userAnswers.clear()
                                    currentQuestionIndex = 0
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CorporatePrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Join Cadre Squad",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            } else {
                // Active Question Card
                val currentQ = questions[currentQuestionIndex]

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Progress Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Step ${currentQuestionIndex + 1} of ${questions.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMuted
                            )
                            Text(
                                text = currentQ.domainTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CorporateAccentBlue
                            )
                        }

                        LinearProgressIndicator(
                            progress = { (currentQuestionIndex + 1) / questions.size.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CorporatePrimary,
                            trackColor = CorporateCardBorder
                        )

                        Text(
                            text = currentQ.questionText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        // Card A
                        CorporateChoiceCard(
                            option = currentQ.cardA,
                            label = "Option A",
                            isSelected = userAnswers[currentQ.id] == 0,
                            onClick = {
                                SoundEffectManager.playPop()
                                userAnswers[currentQ.id] = 0
                                if (currentQuestionIndex < questions.size - 1) {
                                    currentQuestionIndex++
                                } else {
                                    // Trigger Calculation
                                    coroutineScope.launch {
                                        isCalculatingMatch = true
                                        delay(1200)
                                        isCalculatingMatch = false
                                        val matchScore = 78 + (userAnswers.values.sum() * 3) % 18
                                        matchResult = CadreMatchResult(
                                            squadName = "Cadre Operations Unit",
                                            squadCode = "CADRE-OP9",
                                            overallMatchPercent = matchScore.coerceIn(65, 96),
                                            summary = "High operational alignment across structured milestone delivery and asynchronous documentation standards.",
                                            teammates = listOf(
                                                CadreTeammate("Elena Vance", "Strategy Lead", matchScore),
                                                CadreTeammate("Marcus Cole", "Execution Specialist", matchScore - 4),
                                                CadreTeammate("Priya Nair", "Quality Reviewer", matchScore - 2)
                                            )
                                        )
                                    }
                                }
                            }
                        )

                        // Card B
                        CorporateChoiceCard(
                            option = currentQ.cardB,
                            label = "Option B",
                            isSelected = userAnswers[currentQ.id] == 1,
                            onClick = {
                                SoundEffectManager.playPop()
                                userAnswers[currentQ.id] = 1
                                if (currentQuestionIndex < questions.size - 1) {
                                    currentQuestionIndex++
                                } else {
                                    coroutineScope.launch {
                                        isCalculatingMatch = true
                                        delay(1200)
                                        isCalculatingMatch = false
                                        val matchScore = 74 + (userAnswers.values.sum() * 4) % 20
                                        matchResult = CadreMatchResult(
                                            squadName = "Cadre Agile Core",
                                            squadCode = "CADRE-AG7",
                                            overallMatchPercent = matchScore.coerceIn(65, 96),
                                            summary = "Demonstrates strong alignment in dynamic priority management and continuous peer collaboration.",
                                            teammates = listOf(
                                                CadreTeammate("Sarah Jenkins", "Product Coordinator", matchScore),
                                                CadreTeammate("David Chen", "Sprint Manager", matchScore - 3),
                                                CadreTeammate("Amara Okafor", "Systems Architect", matchScore - 5)
                                            )
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CorporateChoiceCard(
    option: CadreTaskOption,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) CorporateAccentBlueLight else CorporateSurface)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) CorporateAccentBlue else CorporateCardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) CorporateAccentBlue else TextMuted
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CorporateBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = option.domain,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = option.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = option.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
