package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundEffectManager
import com.example.data.model.GroupMember
import com.example.data.model.TaskDifficulty
import com.example.ui.theme.*

@Composable
fun AddChoreDialog(
    members: List<GroupMember>,
    onDismiss: () -> Unit,
    onSubmit: (
        text: String,
        category: String,
        postedBy: String,
        difficulty: TaskDifficulty,
        requiresProof: Boolean,
        isSolo: Boolean
    ) -> Unit
) {
    var rawText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ORGANIZING") }
    var selectedMember by remember { mutableStateOf(members.firstOrNull { it.isCurrentActiveUser }?.name ?: "Mia") }
    var selectedDifficulty by remember { mutableStateOf(TaskDifficulty.EASY) }
    var requiresProof by remember { mutableStateOf(false) }
    var isSoloTask by remember { mutableStateOf(false) }

    val categories = listOf(
        Pair("🧺", "ORGANIZING"),
        Pair("🍳", "COOKING"),
        Pair("🌿", "PLANTS"),
        Pair("🧹", "CLEANING"),
        Pair("🧘", "WELLNESS"),
        Pair("🛠️", "FIXING"),
        Pair("🐾", "PETS"),
        Pair("👕", "LAUNDRY")
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(36.dp))
                .background(CloudWhite)
                .border(2.5.dp, MintGreenDark, RoundedCornerShape(36.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💭", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Daily Life Task",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = MintGreenDark
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SlateMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Task Input Field
                OutlinedTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    label = { Text("What needs doing? (e.g., Fold wardrobe)") },
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreenDark,
                        unfocusedBorderColor = SlateLight
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Category Selector
                Text(
                    text = "CATEGORY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.take(4).forEach { (emoji, cat) ->
                        val isSelected = (selectedCategory == cat)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MintGreenPrimary else SlateLight.copy(alpha = 0.4f))
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.dp,
                                    color = if (isSelected) MintGreenDark else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedCategory = cat
                                    SoundEffectManager.playPop()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "$emoji $cat", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSelected) MintGreenDark else SlateText)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.drop(4).forEach { (emoji, cat) ->
                        val isSelected = (selectedCategory == cat)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MintGreenPrimary else SlateLight.copy(alpha = 0.4f))
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.dp,
                                    color = if (isSelected) MintGreenDark else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedCategory = cat
                                    SoundEffectManager.playPop()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "$emoji $cat", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSelected) MintGreenDark else SlateText)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Difficulty Selector
                Text(
                    text = "DIFFICULTY LADDER & XP REWARD",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TaskDifficulty.values().forEach { diff ->
                        val isSelected = (selectedDifficulty == diff)
                        val bgColor = when (diff) {
                            TaskDifficulty.EASY -> MintGreenPrimary
                            TaskDifficulty.MEDIUM -> EggYellowLight
                            TaskDifficulty.HARD -> ChoicePinkBg
                        }
                        val borderColor = when (diff) {
                            TaskDifficulty.EASY -> MintGreenDark
                            TaskDifficulty.MEDIUM -> EggAmber
                            TaskDifficulty.HARD -> ChoicePinkBorder
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) bgColor else SlateLight.copy(alpha = 0.4f))
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.dp,
                                    color = if (isSelected) borderColor else Color.Transparent,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    selectedDifficulty = diff
                                    SoundEffectManager.playPop()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${diff.emoji} ${diff.label}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SlateText
                                )
                                Text(
                                    text = "+${diff.xp} XP",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Photo Proof Requirement Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (requiresProof) ChoicePinkBg else SlateLight.copy(alpha = 0.4f))
                        .clickable {
                            requiresProof = !requiresProof
                            SoundEffectManager.playPop()
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📸", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Require Before & After Photo Proof",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = SlateText
                        )
                        Text(
                            text = "Allows friends to verify with +150 bonus XP!",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = SlateMuted
                        )
                    }
                    Switch(
                        checked = requiresProof,
                        onCheckedChange = { requiresProof = it }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                PoppableButton(
                    text = "Post Task to Oasis ✨",
                    onClick = {
                        if (rawText.isNotBlank()) {
                            onSubmit(rawText, selectedCategory, selectedMember, selectedDifficulty, requiresProof, isSoloTask)
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
}
