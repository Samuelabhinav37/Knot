package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
    var selectedCategory by remember { mutableStateOf("OPERATIONS") }
    var selectedMember by remember { mutableStateOf(members.firstOrNull { it.isCurrentActiveUser }?.name ?: "Alex") }
    var selectedDifficulty by remember { mutableStateOf(TaskDifficulty.EASY) }
    var requiresProof by remember { mutableStateOf(false) }
    var isSoloTask by remember { mutableStateOf(false) }

    val categories = listOf(
        "OPERATIONS",
        "PLANNING",
        "ORGANIZING",
        "DEVELOPMENT",
        "REVIEW",
        "COMMUNICATION"
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CorporateSurface)
                .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
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
                    Column {
                        Text(
                            text = "Add Daily Task",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Create a structured operational check",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Task Input Field
                OutlinedTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    label = { Text("Task Description") },
                    placeholder = { Text("e.g. Audit weekly deliverables") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CorporatePrimary,
                        unfocusedBorderColor = CorporateCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category Selector
                Text(
                    text = "CATEGORY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp,
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                val chunked = categories.chunked(3)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    chunked.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowItems.forEach { cat ->
                                val isSelected = (selectedCategory == cat)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) CorporateAccentBlueLight else CorporateBg)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) CorporateAccentBlue else CorporateCardBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            selectedCategory = cat
                                            SoundEffectManager.playPop()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat.lowercase().replaceFirstChar { it.uppercase() },
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) CorporateAccentBlue else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Priority Level
                Text(
                    text = "PRIORITY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp,
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskDifficulty.values().forEach { diff ->
                        val isSelected = (selectedDifficulty == diff)
                        val priorityLabel = when (diff) {
                            TaskDifficulty.EASY -> "Standard"
                            TaskDifficulty.MEDIUM -> "High"
                            TaskDifficulty.HARD -> "Critical"
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CorporatePrimary else CorporateBg)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) CorporatePrimary else CorporateCardBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedDifficulty = diff
                                    SoundEffectManager.playPop()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = priorityLabel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verification Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CorporateBg)
                        .clickable {
                            requiresProof = !requiresProof
                            SoundEffectManager.playPop()
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Require Completion Verification",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Attach deliverable confirmation upon finish",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    Switch(
                        checked = requiresProof,
                        onCheckedChange = { requiresProof = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CorporatePrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val clean = rawText.trim()
                        if (clean.isNotBlank()) {
                            onSubmit(clean, selectedCategory, selectedMember, selectedDifficulty, requiresProof, isSoloTask)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CorporatePrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Create Task",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
