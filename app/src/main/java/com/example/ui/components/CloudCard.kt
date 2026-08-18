package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CloudCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = CloudWhite,
    borderColor: Color = MintGreenPrimary.copy(alpha = 0.6f),
    elevation: Dp = 4.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(26.dp),
                spotColor = borderColor.copy(alpha = 0.35f),
                ambientColor = borderColor.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(26.dp))
            .background(backgroundColor)
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = 0.3f),
                        borderColor
                    )
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun PoppableButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MintGreenPrimary,
    bottomBorderColor: Color = MintGreenBorderBottom,
    contentColor: Color = MintGreenDark,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .squishClickable(enabled = enabled, onClick = onClick)
            .shadow(
                elevation = if (enabled) 3.dp else 1.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = bottomBorderColor.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = bottomBorderColor,
                shape = RoundedCornerShape(32.dp)
            )
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = contentColor,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ChoreCategoryBadge(category: String, modifier: Modifier = Modifier) {
    val (emoji, bg, border) = when (category.uppercase()) {
        "CLEANING" -> Triple("🧹", BlushPinkLight, BlushPink)
        "FIXING" -> Triple("🔧", SkyBlueLight, SkyBlue)
        "KITCHEN" -> Triple("🧽", LemonLight, LemonGold)
        "PLANTS" -> Triple("🌿", MintGreenLight, MintGreenDark)
        "LAUNDRY" -> Triple("🧺", LilacLight, LilacDark)
        "ORGANIZING" -> Triple("📦", PeachPuff, BlushPink)
        "PETS" -> Triple("🐾", BlushPinkLight, BubblePink)
        else -> Triple("✨", MintGreenLight, MintGreenPrimary)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.5.dp, border.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = category.lowercase().replaceFirstChar { it.uppercase() },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SlateText
            )
        }
    }
}

@Composable
fun MemberAvatarPill(
    name: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .squishClickable(onClick = onClick)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) MintGreenPrimary else CloudWhite)
            .border(
                width = 2.dp,
                color = if (isSelected) MintGreenDark else SlateMuted.copy(alpha = 0.25f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (isSelected) SlateText else SlateMuted
            )
        }
    }
}
