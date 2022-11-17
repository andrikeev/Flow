package flow.account

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import flow.models.user.AuthState
import flow.testing.rule.stringResource
import org.junit.Rule
import org.junit.Test

class AccountItemTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun show_avatar_and_login_button() {
        composeTestRule.setContent {
            AccountItem(
                state = AuthState.Unauthorized,
                onAction = { },
            )
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.stringResource(R.string.account_item_avatar_content_description)
            )
            .assertExists()

        composeTestRule
            .onNodeWithText(
                composeTestRule.stringResource(R.string.account_item_login_action)
            )
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun show_avatar_name_and_logout_button() {
        val userName = "User"
        composeTestRule.setContent {
            AccountItem(
                state = AuthState.Authorized(
                    name = userName,
                    avatarUrl = null,
                ),
                onAction = { },
            )
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.stringResource(R.string.account_item_avatar_content_description)
            )
            .assertExists()

        composeTestRule
            .onNodeWithText(userName)
            .assertExists()

        composeTestRule
            .onNodeWithText(
                composeTestRule.stringResource(R.string.account_item_logout_action)
            )
            .assertExists()
            .assertHasClickAction()
    }
}
