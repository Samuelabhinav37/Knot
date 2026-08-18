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
          coreFive = listOf(
            ChoreItem(text = "Sparkle Kitchen Dishes", iconCategory = "KITCHEN", postedBy = "Mia")
          ),
          members = listOf(
            GroupMember(name = "Mia", avatarEmoji = "🌸", avatarColorHex = 0xFFFFB6C1, isCurrentActiveUser = true)
          ),
          latestPet = null,
          onTapEgg = {},
          onCompleteChore = { _, _, _ -> },
          onSwitchMember = {},
          onOpenThoughtBubble = {},
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

