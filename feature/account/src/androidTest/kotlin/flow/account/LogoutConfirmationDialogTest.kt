package flow.account

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import flow.designsystem.component.ConfirmationDialog
import flow.designsystem.component.rememberConfirmationDialogState
import flow.designsystem.utils.RunOnFirstComposition
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
            val confirmationDialogState = rememberConfirmationDialogState()
            RunOnFirstComposition {
                confirmationDialogState.show(
                    title = R.string.account_item_logout_title,
                    text = R.string.account_item_logout_confirmation,
                    onConfirm = {},
                    onDismiss = {}
                )
            }
            ConfirmationDialog(confirmationDialogState)
        }

        composeTestRule
            .onNodeWithText(
                composeTestRule.stringResource(R.string.account_item_logout_title)
            )
            .assertExists()

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
