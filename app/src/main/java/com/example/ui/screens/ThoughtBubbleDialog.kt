package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.GroupMember
import com.example.ui.components.PoppableButton
import com.example.ui.components.squishClickable
import com.example.ui.theme.*

@Composable
fun ThoughtBubbleDialog(
    members: List<GroupMember>,
    activeMemberName: String,
    onDismiss: () -> Unit,
    onSubmitThought: (text: String, category: String, postedBy: String) -> Unit
) {
    var thoughtText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("CLEANING") }
    var selectedPoster by remember { mutableStateOf(activeMemberName) }

    val quickSuggestions = listOf(
        Pair("Banish the Dust Bunnies 🧹", "CLEANING"),
        Pair("Water the Sproutlings 🌿", "PLANTS"),
        Pair("Wash the Bubble Dishes 🧽", "KITCHEN"),
        Pair("Fold Warm Magic Laundry 🧺", "LAUNDRY"),
        Pair("Tidy the Snack Nook 🍪", "ORGANIZING"),
        Pair("Fix the Squeaky Drawer 🔧", "FIXING"),
        Pair("Pamper the Cozy Pets 🐾", "PETS")
    )

    val categories = listOf(
        Pair("CLEANING", "🧹 Cleaning"),
        Pair("KITCHEN", "🧽 Kitchen"),
        Pair("PLANTS", "🌿 Plants"),
        Pair("LAUNDRY", "🧺 Laundry"),
        Pair("ORGANIZING", "📦 Organize"),
        Pair("FIXING", "🔧 Fixing"),
        Pair("PETS", "🐾 Pets")
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(32.dp), spotColor = MintGreenDark.copy(alpha = 0.4f))
                .clip(RoundedCornerShape(32.dp))
                .background(CreamBackground)
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(listOf(MintGreenPrimary, SkyBlue, BlushPink)),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💭", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Post a Thought-Bubble",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = SlateText
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = SlateMuted)
                    }
                }

                Text(
                    text = "Share chore ideas! The oasis will magically standardize phrasing with cute cartoon icons.",
                    fontSize = 12.sp,
                    color = SlateMuted,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Thought Bubble Input Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(CloudWhite)
                        .border(1.5.dp, SkyBlue.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                        .padding(14.dp)
                ) {
                    TextField(
                        value = thoughtText,
                        onValueChange = { thoughtText = it },
                        placeholder = {
                            Text(
                                text = "e.g., Sweep the cloud floor or polish tea cups...",
                                fontSize = 13.sp,
                                color = SlateMuted.copy(alpha = 0.7f)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Whimsical Suggestions
                Text(
                    text = "✨ Quick Sparkling Ideas",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateText,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quickSuggestions) { (suggestion, cat) ->
                        Box(
                            modifier = Modifier
                                .squishClickable {
                                    thoughtText = suggestion
                                    selectedCategory = cat
                                }
                                .clip(RoundedCornerShape(16.dp))
                                .background(LemonLight)
                                .border(1.dp, LemonGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = suggestion,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SlateText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Selection
                Text(
                    text = "🏷️ Chore Category",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateText,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { (catKey, catLabel) ->
                        val isSelected = (selectedCategory == catKey)
                        Box(
                            modifier = Modifier
                                .squishClickable { selectedCategory = catKey }
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) MintGreenPrimary else CloudWhite)
                                .border(
                                    width = 1.5.dp,
                                    color = if (isSelected) MintGreenDark else SlateMuted.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = catLabel,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) SlateText else SlateMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Posted By Friend Selector
                Text(
                    text = "👤 Posted By",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateText,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(members) { member ->
                        val isSelected = (selectedPoster == member.name)
                        Box(
                            modifier = Modifier
                                .squishClickable { selectedPoster = member.name }
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) BlushPink else CloudWhite)
                                .border(
                                    1.5.dp,
                                    if (isSelected) BlushPinkDark else SlateMuted.copy(alpha = 0.3f),
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = member.avatarEmoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = member.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) CloudWhite else SlateText
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Submit Button
                PoppableButton(
                    text = "Release Thought Bubble 🫧",
                    onClick = {
                        if (thoughtText.isNotBlank()) {
                            onSubmitThought(thoughtText, selectedCategory, selectedPoster)
                        }
                    },
                    enabled = thoughtText.isNotBlank(),
                    backgroundColor = MintGreenPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
