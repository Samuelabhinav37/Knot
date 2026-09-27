package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.ChoreItem
import com.example.data.model.GroupMember
import com.example.data.model.OasisMeta
import com.example.ui.screens.OasisHomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        OasisHomeScreen(
          meta = OasisMeta(),
          userProfile = null,
          currentSeason = com.example.viewmodel.AppSeason.SPRING,
          coreFive = listOf(
            ChoreItem(text = "Organize workspace", iconCategory = "ORGANIZING", postedBy = "Alex")
          ),
          members = listOf(
            GroupMember(name = "Alex", avatarEmoji = "👤", avatarColorHex = 0xFF0F172A, isCurrentActiveUser = true)
          ),
          latestPet = null,
          taskFilter = "ALL",
          onSelectTaskFilter = {},
          onTapEgg = {},
          onCompleteChore = { _, _, _ -> },
          onOpenPhotoProof = {},
          onOpenTutorial = {},
          onSwitchMember = {},
          onOpenThoughtBubble = {},
          onOpenSquadRoom = {},
          onOpenBadges = {},
          onOpenSettings = {},
          onStartNewEgg = {},
          onToggleTimer = {},
          onResetTimer = {},
          onGoToParadise = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

