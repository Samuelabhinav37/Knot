package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEffectManager
import com.example.data.model.BadgeItem
import com.example.data.model.GroupMember
import com.example.data.model.HatchedPet
import com.example.data.model.PetAccessory
import com.example.data.model.UserProfile
import com.example.ui.theme.*

@Composable
fun PetParadiseScreen(
    userProfile: UserProfile? = null,
    members: List<GroupMember> = emptyList(),
    pets: List<HatchedPet> = emptyList(),
    badges: List<BadgeItem> = emptyList(),
    currentStreak: Int = 5,
    onEquipAccessory: (petId: Long, accessory: PetAccessory) -> Unit = { _, _ -> },
    onFeedTreat: (treat: String) -> Unit = {},
    onPetBuddy: () -> Unit = {},
    onOpenBadgesGallery: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val userName = userProfile?.username ?: "Alex Morgan"
    val userArchetype = userProfile?.personalityArchetype ?: "Operations Strategist"
    val squadName = userProfile?.squadName ?: "Cadre Unit Alpha"
    val squadCode = userProfile?.squadCode ?: "CADRE-8X9"

    var soundEnabled by remember { mutableStateOf(true) }
    var dailyReminders by remember { mutableStateOf(true) }
    var squadSyncNotifs by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CorporateBg)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with Name & Settings Icon on Top Right
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Account Profile",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Preferences, team status, and system settings",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }

                // Settings Button on Top Right
                IconButton(
                    onClick = {
                        SoundEffectManager.playPop()
                        onOpenSettings()
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CorporateSurface)
                        .border(1.dp, CorporateCardBorder, RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Profile Identity Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CorporateSurface)
                    .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Initials / Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(CorporatePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.take(2).uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = userArchetype,
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CorporateAccentBlueLight)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = squadName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CorporateAccentBlue
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• Code: $squadCode",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        // Key Account Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AccountMetricCard(
                    label = "Consistency",
                    value = "${currentStreak}d",
                    caption = "Daily streak",
                    modifier = Modifier.weight(1f)
                )
                AccountMetricCard(
                    label = "Completed",
                    value = "28",
                    caption = "Tasks closed",
                    modifier = Modifier.weight(1f)
                )
                AccountMetricCard(
                    label = "Cadre Members",
                    value = "${members.size.coerceAtLeast(3)}",
                    caption = "In your unit",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section: General Preferences
        item {
            Text(
                text = "Preferences",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Preferences Card with Clean Toggles
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CorporateSurface)
                    .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    PreferenceToggleRow(
                        icon = Icons.Default.Notifications,
                        title = "Daily Routine Reminders",
                        subtitle = "Receive morning operational check notifications",
                        isChecked = dailyReminders,
                        onCheckedChange = { dailyReminders = it }
                    )

                    Divider(color = CorporateCardBorder, thickness = 0.8.dp)

                    PreferenceToggleRow(
                        icon = Icons.Default.Person,
                        title = "Squad Synchronization",
                        subtitle = "Notify when team members complete shared goals",
                        isChecked = squadSyncNotifs,
                        onCheckedChange = { squadSyncNotifs = it }
                    )

                    Divider(color = CorporateCardBorder, thickness = 0.8.dp)

                    PreferenceToggleRow(
                        icon = Icons.Default.VolumeUp,
                        title = "Audio Feedback",
                        subtitle = "Play subtle sound clicks on task completion",
                        isChecked = soundEnabled,
                        onCheckedChange = { soundEnabled = it }
                    )
                }
            }
        }

        // Section: System & Support
        item {
            Text(
                text = "System & Support",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CorporateSurface)
                    .border(1.dp, CorporateCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SystemLinkRow(
                        icon = Icons.Default.Palette,
                        title = "Appearance & Layout",
                        onClick = { onOpenSettings() }
                    )

                    Divider(color = CorporateCardBorder, thickness = 0.8.dp)

                    SystemLinkRow(
                        icon = Icons.Default.Security,
                        title = "Data & Privacy",
                        onClick = { onOpenSettings() }
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountMetricCard(
    label: String,
    value: String,
    caption: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CorporateSurface)
            .border(1.dp, CorporateCardBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = caption,
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun PreferenceToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CorporateBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = {
                SoundEffectManager.playPop()
                onCheckedChange(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = CorporatePrimary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = CorporateCardBorder
            )
        )
    }
}

@Composable
private fun SystemLinkRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CorporateBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextDisabled,
            modifier = Modifier.size(18.dp)
        )
    }
}
