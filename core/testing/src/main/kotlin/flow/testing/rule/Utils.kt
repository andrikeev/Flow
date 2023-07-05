package flow.testing.rule

import androidx.annotation.StringRes
import androidx.compose.ui.test.junit4.AndroidComposeTestRule

fun AndroidComposeTestRule<*, *>.stringResource(@StringRes id: Int): String = activity.getString(id)
