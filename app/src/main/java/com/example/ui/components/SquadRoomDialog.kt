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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundEffectManager
import com.example.data.model.GroupMember
import com.example.data.model.UserProfile
import com.example.ui.theme.*

@Composable
fun SquadRoomDialog(
    members: List<GroupMember>,
    userProfile: UserProfile?,
    onDismiss: () -> Unit,
    onJoinSquad: (code: String) -> Unit,
    onToggleMode: (String) -> Unit
) {
    var inputCode by remember { mutableStateOf("") }
    var isJoiningByCode by remember { mutableStateOf(false) }

    val squadCode = userProfile?.squadCode ?: "OASIS-7X29"
    val squadName = userProfile?.squadName ?: "Pastel Blossom Squad"
    val currentMode = userProfile?.currentMode ?: "SQUAD"

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
                        Text(text = "👥", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Squad & Co-op Hub",
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

                Spacer(modifier = Modifier.height(10.dp))

                // Mode Switcher (Solo vs Squad)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SlateLight.copy(alpha = 0.5f))
                        .padding(4.dp)
                ) {
                    val isSquad = (currentMode == "SQUAD")
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSquad) MintGreenPrimary else Color.Transparent)
                            .border(
                                width = if (isSquad) 1.5.dp else 0.dp,
                                color = if (isSquad) MintGreenDark else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                onToggleMode("SQUAD")
                                SoundEffectManager.playPop()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👥 Squad Co-op",
                            fontSize = 12.sp,
                            fontWeight = if (isSquad) FontWeight.Black else FontWeight.Medium,
                            color = if (isSquad) MintGreenDark else SlateMuted
                        )
                    }

                    val isSolo = (currentMode == "SOLO")
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSolo) ChoicePinkBg else Color.Transparent)
                            .border(
                                width = if (isSolo) 1.5.dp else 0.dp,
                                color = if (isSolo) ChoicePinkBorder else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                onToggleMode("SOLO")
                                SoundEffectManager.playPop()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🌱 Solo Focus",
                            fontSize = 12.sp,
                            fontWeight = if (isSolo) FontWeight.Black else FontWeight.Medium,
                            color = if (isSolo) ChoicePinkText else SlateMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Squad Code Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(EggYellowLight)
                        .border(1.5.dp, EggAmber, RoundedCornerShape(22.dp))
                        .padding(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "ROOM CODE TO SHARE WITH FRIENDS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = AmberTextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = squadCode,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = AmberTextDeep
                        )
                        Text(
                            text = "Friends can type this code to join your blended mission room.",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = SlateMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Join with new code section
                if (!isJoiningByCode) {
                    TextButton(onClick = { isJoiningByCode = true }) {
                        Text(
                            text = "+ Join Another Squad with Code",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MintGreenDark
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputCode,
                            onValueChange = { inputCode = it.uppercase() },
                            label = { Text("Code (e.g. OASIS-8822)") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MintGreenDark,
                                unfocusedBorderColor = SlateLight
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PoppableButton(
                            text = "Join",
                            onClick = {
                                if (inputCode.isNotBlank()) {
                                    onJoinSquad(inputCode)
                                    isJoiningByCode = false
                                }
                            },
                            backgroundColor = MintGreenPrimary,
                            bottomBorderColor = MintGreenBorderBottom,
                            contentColor = MintGreenDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 5 Friends Squad Roster & Personalities
                Text(
                    text = "SQUAD MEMBERS & PERSONALITY BLEND (5/5)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                members.forEach { member ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (member.isCurrentActiveUser) MintGreenLight else CloudWhite)
                            .border(
                                width = 1.dp,
                                color = if (member.isCurrentActiveUser) MintGreenDark else SlateLight,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ChoicePinkBg),
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
                                        fontWeight = FontWeight.Black,
                                        color = SlateText
                                    )
                                    if (member.isCurrentActiveUser) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "(You)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MintGreenDark
                                        )
                                    }
                                }
                                Text(
                                    text = "${member.personalityArchetype} • ${member.primaryInterest}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SlateMuted
                                )
                            }

                            Text(
                                text = "${member.tasksCompletedCount} Done ✓",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (member.tasksCompletedCount > 0) MintGreenDark else SlateMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                PoppableButton(
                    text = "Close Room ✨",
                    onClick = onDismiss,
                    backgroundColor = MintGreenPrimary,
                    bottomBorderColor = MintGreenBorderBottom,
                    contentColor = MintGreenDark,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
