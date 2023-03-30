package flow.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel

@Composable
inline fun <reified VM : ViewModel> viewModel(): VM = hiltViewModel()
