package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundEffectManager
import com.example.data.model.GroupMember
import com.example.data.model.UserProfile
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SquadRoomDialog(
    members: List<GroupMember>,
    userProfile: UserProfile?,
    onDismiss: () -> Unit,
    onJoinSquad: (code: String) -> Unit,
    onToggleMode: (String) -> Unit
) {
    var inputCode by remember { mutableStateOf("") }
    val squadCode = userProfile?.squadCode ?: "SQUAD-8X9"
    val squadName = userProfile?.squadName ?: "Alpha Squad"
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isCodeCopied by remember { mutableStateOf(false) }
    var cheerNotice by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CorporateSurface)
                .border(1.dp, CorporateCardBorder, RoundedCornerShape(24.dp))
                .padding(22.dp)
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
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CorporateAccentBlue.copy(alpha = 0.15f))
                                .border(1.dp, CorporateAccentBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = CorporateAccentBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = squadName,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Shared Team Operations Hub",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
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

                Spacer(modifier = Modifier.height(16.dp))

                // Squad Code Pill with One-Tap Copy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CorporateBg)
                        .border(1.dp, CorporateCardBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SQUAD INVITE CODE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = squadCode,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CorporateAccentBlue
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isCodeCopied) CorporateSuccessLight else CorporatePrimaryLight)
                            .border(
                                1.dp,
                                if (isCodeCopied) CorporateSuccess else CorporateCardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                SoundEffectManager.playPop()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Squad Code", squadCode)
                                clipboard.setPrimaryClip(clip)
                                isCodeCopied = true
                                Toast.makeText(context, "Squad code copied to clipboard!", Toast.LENGTH_SHORT).show()
                                coroutineScope.launch {
                                    delay(2000L)
                                    isCodeCopied = false
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isCodeCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = if (isCodeCopied) CorporateSuccess else Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (isCodeCopied) "Copied!" else "Copy",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isCodeCopied) CorporateSuccess else Color.White
                            )
                        }
                    }
                }

                // Cheer Notice Feedback
                cheerNotice?.let { notice ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CorporateSuccessLight)
                            .border(1.dp, CorporateSuccess.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = notice,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Active Team Members List
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TEAM MEMBERS (${members.size})",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "Tap 👋 to Cheer",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    members.forEach { member ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(CorporateBg)
                                .border(1.dp, CorporateCardBorder, RoundedCornerShape(14.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(CorporatePrimaryLight)
                                        .border(1.dp, CorporateCardBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = member.avatarEmoji,
                                        fontSize = 18.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = member.name + if (member.isCurrentActiveUser) " (You)" else "",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${member.personalityArchetype} • ${member.tasksCompletedCount} checks",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            if (!member.isCurrentActiveUser) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(CorporatePrimaryLight)
                                        .border(1.dp, CorporateCardBorder, CircleShape)
                                        .clickable {
                                            SoundEffectManager.playPop()
                                            cheerNotice = "Sent high-five to ${member.name}! 🙌"
                                            coroutineScope.launch {
                                                delay(2500L)
                                                cheerNotice = null
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "👋", fontSize = 14.sp)
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CorporateSuccessLight)
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Active",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CorporateSuccess
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Connect to another squad
                OutlinedTextField(
                    value = inputCode,
                    onValueChange = { inputCode = it.uppercase() },
                    label = { Text("Switch to Another Squad Code", fontSize = 12.sp) },
                    placeholder = { Text("e.g. CADRE-8X9", fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CorporateAccentBlue,
                        unfocusedBorderColor = CorporateCardBorder,
                        focusedLabelColor = CorporateAccentBlue,
                        cursorColor = CorporateAccentBlue
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (inputCode.isNotBlank()) {
                            SoundEffectManager.playPop()
                            onJoinSquad(inputCode.trim())
                            onDismiss()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CorporateAccentBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Join Squad",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
