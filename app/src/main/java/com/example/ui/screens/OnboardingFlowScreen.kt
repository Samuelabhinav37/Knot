package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEffectManager
import com.example.ui.components.PoppableButton
import com.example.ui.theme.*

data class InterestCardOption(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val defaultGoalId: String,
    val color: Color
)

@Composable
fun OnboardingFlowScreen(
    onCompleteOnboarding: (
        username: String,
        gender: String,
        avatar: String,
        archetype: String,
        interests: List<String>,
        squadCode: String,
        initialGoal: String
    ) -> Unit
) {
    var step by remember { mutableStateOf(0) } // 0: Welcome & Auth, 1: Profile & Avatar, 2: 5 Interest Cards Quiz, 3: Squad & Goal

    var authProvider by remember { mutableStateOf("GOOGLE") }
    var email by remember { mutableStateOf("explorer@knot.app") }
    var username by remember { mutableStateOf("Mia") }
    var genderPronoun by remember { mutableStateOf("She/Her") }
    var selectedAvatar by remember { mutableStateOf("🌸") }
    var squadCode by remember { mutableStateOf("KNOT-7X29") }
    var initialGoal by remember { mutableStateOf("FOLDING") }

    val interestOptions = listOf(
        InterestCardOption(
            id = "PLANTS",
            title = "Plant Whisperer 🌿",
            description = "Indoor jungles, propagation, watering cycles",
            emoji = "🌿",
            defaultGoalId = "PLANTS",
            color = MintGreenPrimary
        ),
        InterestCardOption(
            id = "CULINARY",
            title = "Culinary Artisan 🍳",
            description = "15-min meal prep, spice racks, clean cooktop",
            emoji = "🍳",
            defaultGoalId = "COOKING",
            color = EggYellowLight
        ),
        InterestCardOption(
            id = "ORGANIZING",
            title = "KonMari Organizer 🧺",
            description = "Closet folding, color coding, tidy sanctuary",
            emoji = "🧺",
            defaultGoalId = "FOLDING",
            color = ChoicePinkBg
        ),
        InterestCardOption(
            id = "WELLNESS",
            title = "Mindful Minimalist 🧘",
            description = "Bed making, hydration tracking, calm routines",
            emoji = "🧘",
            defaultGoalId = "MINDFUL",
            color = ChoiceSkyBg
        ),
        InterestCardOption(
            id = "HANDY",
            title = "Handy Crafter 🛠️",
            description = "Appliance tune-ups, quick fixes, DIY maintenance",
            emoji = "🛠️",
            defaultGoalId = "MAINTENANCE",
            color = LilacLight
        )
    )

    val selectedInterests = remember { mutableStateListOf("PLANTS", "ORGANIZING") }

    val pronounOptions = listOf("She/Her", "He/Him", "They/Them", "Custom ✨")
    val availableAvatars = listOf("🌸", "🌿", "⚡", "🎀", "🦊", "🐻", "🐼", "🐱", "🦄", "🌟", "🍳", "🛠️")

    val calculatedArchetype by remember {
        derivedStateOf {
            when {
                selectedInterests.contains("PLANTS") && selectedInterests.contains("ORGANIZING") -> "The Cozy Cultivator"
                selectedInterests.contains("CULINARY") -> "The Culinary Artisan"
                selectedInterests.contains("HANDY") -> "The Handy Crafter"
                selectedInterests.contains("WELLNESS") -> "The Mindful Minimalist"
                else -> "The Energetic Organizer"
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        CreamBackground,
                        MintGreenLight.copy(alpha = 0.6f),
                        CreamBackground
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Step Progress Indicator
            StepProgressBar(currentStep = step, totalSteps = 4)

            Spacer(modifier = Modifier.height(18.dp))

            when (step) {
                0 -> {
                    // Step 0: Welcome & Auth Providers
                    WelcomeAuthStep(
                        onSelectProvider = { provider ->
                            authProvider = provider
                            SoundEffectManager.playPop()
                            step = 1
                        }
                    )
                }
                1 -> {
                    // Step 1: Create Profile & Avatar
                    ProfileCreationStep(
                        username = username,
                        onUsernameChange = { username = it },
                        selectedAvatar = selectedAvatar,
                        onSelectAvatar = {
                            selectedAvatar = it
                            SoundEffectManager.playPop()
                        },
                        availableAvatars = availableAvatars,
                        genderPronoun = genderPronoun,
                        pronounOptions = pronounOptions,
                        onSelectPronoun = {
                            genderPronoun = it
                            SoundEffectManager.playPop()
                        },
                        onNext = {
                            SoundEffectManager.playPop()
                            step = 2
                        }
                    )
                }
                2 -> {
                    // Step 2: 5 Interest Cards Quiz
                    InterestsQuizStep(
                        options = interestOptions,
                        selectedInterests = selectedInterests,
                        onToggleInterest = { id ->
                            SoundEffectManager.playPop()
                            if (selectedInterests.contains(id)) {
                                if (selectedInterests.size > 1) selectedInterests.remove(id)
                            } else {
                                selectedInterests.add(id)
                            }
                        },
                        calculatedArchetype = calculatedArchetype,
                        onNext = {
                            SoundEffectManager.playPop()
                            step = 3
                        }
                    )
                }
                3 -> {
                    // Step 3: Goal Selection & Squad Code
                    GoalAndSquadStep(
                        archetype = calculatedArchetype,
                        avatarEmoji = selectedAvatar,
                        username = username,
                        squadCode = squadCode,
                        onSquadCodeChange = { squadCode = it.uppercase() },
                        initialGoal = initialGoal,
                        onGoalChange = { initialGoal = it },
                        onFinish = {
                            SoundEffectManager.playFanfare()
                            onCompleteOnboarding(
                                username,
                                genderPronoun,
                                selectedAvatar,
                                calculatedArchetype,
                                selectedInterests.toList(),
                                squadCode,
                                initialGoal
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StepProgressBar(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until totalSteps) {
            val isCompleted = i <= currentStep
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (isCompleted) MintGreenDark else SlateLight)
            )
        }
    }
}

@Composable
private fun WelcomeAuthStep(
    onSelectProvider: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Cute Welcoming Mascot Box
        Box(
            modifier = Modifier
                .size(100.dp)
                .shadow(8.dp, CircleShape, spotColor = EggAmber.copy(alpha = 0.4f))
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(EggYellowLight, ChoicePinkBg)
                    )
                )
                .border(3.dp, CloudWhite, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🐣", fontSize = 48.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome to Knot",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MintGreenDark,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Your whimsical co-op paradise for daily routines, skill mastery, and companion care.",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = SlateMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Social Sign-Up Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PoppableButton(
                text = "Continue with Google 🌐",
                onClick = { onSelectProvider("GOOGLE") },
                backgroundColor = CloudWhite,
                bottomBorderColor = SlateLight,
                contentColor = SlateText,
                modifier = Modifier.fillMaxWidth()
            )

            PoppableButton(
                text = "Continue with Apple 🍏",
                onClick = { onSelectProvider("APPLE") },
                backgroundColor = CloudWhite,
                bottomBorderColor = SlateLight,
                contentColor = SlateText,
                modifier = Modifier.fillMaxWidth()
            )

            PoppableButton(
                text = "Sign in with Email ✉️",
                onClick = { onSelectProvider("EMAIL") },
                backgroundColor = MintGreenPrimary,
                bottomBorderColor = MintGreenBorderBottom,
                contentColor = MintGreenDark,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ProfileCreationStep(
    username: String,
    onUsernameChange: (String) -> Unit,
    selectedAvatar: String,
    onSelectAvatar: (String) -> Unit,
    availableAvatars: List<String>,
    genderPronoun: String,
    pronounOptions: List<String>,
    onSelectPronoun: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Your Identity ✨",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MintGreenDark
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Pick an avatar and name for your squad profile.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = SlateMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Avatar Preview
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(EggYellowMid)
                .border(2.5.dp, CloudWhite, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = selectedAvatar, fontSize = 38.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Avatar selector grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(availableAvatars) { emoji ->
                val isSelected = (emoji == selectedAvatar)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) MintGreenPrimary else CloudWhite)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MintGreenDark else SlateLight,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelectAvatar(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Username Input
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Display Name (e.g. Mia)") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MintGreenDark,
                unfocusedBorderColor = SlateLight
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Gender / Pronoun Selector
        Text(
            text = "PRONOUNS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.2.sp,
            color = AmberTextDark,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            pronounOptions.forEach { pronoun ->
                val isSelected = (pronoun == genderPronoun)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) ChoicePinkBg else CloudWhite)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) ChoicePinkBorder else SlateLight,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onSelectPronoun(pronoun) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pronoun,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                        color = if (isSelected) ChoicePinkText else SlateText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        PoppableButton(
            text = "Continue to Personality Quiz →",
            onClick = onNext,
            backgroundColor = MintGreenPrimary,
            bottomBorderColor = MintGreenBorderBottom,
            contentColor = MintGreenDark,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun InterestsQuizStep(
    options: List<InterestCardOption>,
    selectedInterests: List<String>,
    onToggleInterest: (String) -> Unit,
    calculatedArchetype: String,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pick Your Interests 🎯",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MintGreenDark
        )

        Text(
            text = "Select cards that fit your daily life to shape your task ladder & archetype.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = SlateMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Archetype banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(EggYellowMid)
                .border(1.5.dp, EggAmber, RoundedCornerShape(18.dp))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✨ Identified Archetype: $calculatedArchetype",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = AmberTextDark
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Interest Cards list
        options.forEach { option ->
            val isSelected = selectedInterests.contains(option.id)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isSelected) option.color else CloudWhite)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MintGreenDark else SlateLight,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onToggleInterest(option.id) }
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = option.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = option.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = SlateText
                        )
                        Text(
                            text = option.description,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = SlateMuted
                        )
                    }
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(MintGreenDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = CloudWhite,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        PoppableButton(
            text = "Continue to Squad Room →",
            onClick = onNext,
            backgroundColor = MintGreenPrimary,
            bottomBorderColor = MintGreenBorderBottom,
            contentColor = MintGreenDark,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun GoalAndSquadStep(
    archetype: String,
    avatarEmoji: String,
    username: String,
    squadCode: String,
    onSquadCodeChange: (String) -> Unit,
    initialGoal: String,
    onGoalChange: (String) -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Join Squad & Tackled Goal 🚀",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MintGreenDark
        )

        Text(
            text = "Blend your tasks with friends via code, or start solo focus.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = SlateMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Squad Code Input
        OutlinedTextField(
            value = squadCode,
            onValueChange = onSquadCodeChange,
            label = { Text("Squad Code (e.g. KNOT-7X29)") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MintGreenDark,
                unfocusedBorderColor = SlateLight
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "CHOOSE YOUR FIRST FOCUS GOAL",
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.2.sp,
            color = AmberTextDark,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val goals = listOf(
            Pair("FOLDING", "🧺 Master KonMari Wardrobe Folding"),
            Pair("COOKING", "🍳 15-Min Healthy Dinner Meal Prep"),
            Pair("PLANTS", "🌿 Deep Hydration & Plant Pruning Cycle"),
            Pair("MINDFUL", "🧘 5-Minute Bedroom Reset & Desk Clear")
        )

        goals.forEach { (key, label) ->
            val isSelected = (initialGoal == key)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) MintGreenPrimary else CloudWhite)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) MintGreenDark else SlateLight,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onGoalChange(key) }
                    .padding(12.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                    color = if (isSelected) MintGreenDark else SlateText
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PoppableButton(
            text = "Enter Knot Paradise ✨",
            onClick = onFinish,
            backgroundColor = MintGreenPrimary,
            bottomBorderColor = MintGreenBorderBottom,
            contentColor = MintGreenDark,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
