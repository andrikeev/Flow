package flow.account

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import flow.designsystem.component.DialogState
import flow.testing.rule.stringResource
import org.junit.Rule
import org.junit.Test
import flow.designsystem.R as DR

class LogoutConfirmationDialogTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun show_confirmation_message_yes_button_and_no_button() {
        composeTestRule.setContent {
            LogoutConfirmationDialog(
                state = DialogState.Show,
                onAction = { },
            )
        }

        composeTestRule
            .onNodeWithText(
                composeTestRule.stringResource(R.string.account_item_logout_confirmation)
            )
            .assertExists()

        composeTestRule
            .onNodeWithText(
                composeTestRule.stringResource(DR.string.designsystem_action_yes)
            )
            .assertExists()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText(
                composeTestRule.stringResource(DR.string.designsystem_action_no)
            )
            .assertExists()
            .assertHasClickAction()
    }
}
