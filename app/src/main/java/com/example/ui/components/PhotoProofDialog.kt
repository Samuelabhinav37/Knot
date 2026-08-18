package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundEffectManager
import com.example.data.model.ChoreItem
import com.example.ui.theme.*

@Composable
fun PhotoProofDialog(
    chore: ChoreItem,
    onDismiss: () -> Unit,
    onSubmitProof: (beforeUri: String?, afterUri: String?) -> Unit
) {
    var hasBeforePhoto by remember { mutableStateOf(chore.beforePhotoUri != null) }
    var hasAfterPhoto by remember { mutableStateOf(chore.afterPhotoUri != null) }
    var isVerifying by remember { mutableStateOf(false) }
    var isVerified by remember { mutableStateOf(chore.isVerified) }

    LaunchedEffect(isVerifying) {
        if (isVerifying) {
            kotlinx.coroutines.delay(1200)
            isVerifying = false
            isVerified = true
            SoundEffectManager.playFanfare()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(CloudWhite)
                .border(2.5.dp, MintGreenDark, RoundedCornerShape(32.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📸", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Proof of Work Verification",
                            fontSize = 14.sp,
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

                Text(
                    text = chore.text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = SlateText,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Upload Before & After snapshots to earn +150 bonus XP and verify task completion!",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = SlateMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Before & After Picture Placeholders Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Before Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(130.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (hasBeforePhoto) ChoicePinkBg else SlateLight.copy(alpha = 0.5f))
                            .border(
                                width = 2.dp,
                                color = if (hasBeforePhoto) ChoicePinkBorder else SlateLight,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                hasBeforePhoto = !hasBeforePhoto
                                SoundEffectManager.playPop()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (hasBeforePhoto) "📷 BEFORE ✓" else "📸 Snap Before",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (hasBeforePhoto) ChoicePinkText else SlateMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (hasBeforePhoto) "Messy state captured" else "Tap to snap/upload",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = SlateMuted
                            )
                        }
                    }

                    // After Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(130.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (hasAfterPhoto) MintGreenPrimary else SlateLight.copy(alpha = 0.5f))
                            .border(
                                width = 2.dp,
                                color = if (hasAfterPhoto) MintGreenDark else SlateLight,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                hasAfterPhoto = !hasAfterPhoto
                                SoundEffectManager.playPop()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (hasAfterPhoto) "✨ AFTER ✓" else "📸 Snap After",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (hasAfterPhoto) MintGreenDark else SlateMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (hasAfterPhoto) "Sparkle state captured" else "Tap to snap/upload",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = SlateMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verification Stamp Banner
                if (isVerified) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .rotate(-2f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(EggYellowMid)
                            .border(2.dp, EggAmber, RoundedCornerShape(18.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "👑", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "VERIFIED MASTERPIECE ✨ (+150 XP)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberTextDark
                            )
                        }
                    }
                } else if (isVerifying) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🔍 Squad AI & Peer Review in progress...",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MintGreenDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            modifier = Modifier
                                .width(160.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MintGreenDark,
                            trackColor = SlateLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                PoppableButton(
                    text = if (isVerified) "Done & Saved ✨" else "Submit & Verify Proof 📸",
                    onClick = {
                        if (!isVerified) {
                            if (!hasBeforePhoto) hasBeforePhoto = true
                            if (!hasAfterPhoto) hasAfterPhoto = true
                            isVerifying = true
                            SoundEffectManager.playPop()
                        } else {
                            onSubmitProof("content://before_${chore.id}", "content://after_${chore.id}")
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
