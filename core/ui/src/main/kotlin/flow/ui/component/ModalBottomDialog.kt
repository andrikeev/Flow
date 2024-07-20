package flow.ui.component

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewRootForInspector
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import flow.designsystem.component.Surface
import flow.designsystem.theme.AppTheme
import flow.ui.R
import java.util.*

@Composable
fun ModalBottomDialog(
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val composeView = LocalView.current
    val composition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)
    val windowInsets = WindowInsets.systemBars
        .union(WindowInsets.ime)
        .asPaddingValues()
    val dialogId = rememberSaveable { UUID.randomUUID() }
    val dialog = remember(composeView) {
        ModalBottomDialog(
            dialogId = dialogId,
            composeView = composeView,
            onDismissRequest = onDismissRequest,
        ).apply {
            setContent(composition) {
                ModalBottomDialogLayout(
                    insets = windowInsets,
                    content = currentContent,
                )
            }
        }
    }

    DisposableEffect(dialog) {
        dialog.show()
        onDispose {
            dialog.dismiss()
            dialog.disposeComposition()
        }
    }
}

private class ModalBottomDialog(
    dialogId: UUID,
    composeView: View,
    private val onDismissRequest: () -> Unit,
) : BottomSheetDialog(
    composeView.context,
    R.style.AppThemeOverlay_BottomSheetDialog,
),
    ViewRootForInspector {

    private val dialogLayout = ModalBottomDialogLayout(context).apply {
        tag = "ModalBottomDialog:$dialogId"
    }

    init {
        dialogLayout.setViewTreeLifecycleOwner(composeView.findViewTreeLifecycleOwner())
        dialogLayout.setViewTreeViewModelStoreOwner(composeView.findViewTreeViewModelStoreOwner())
        dialogLayout.setViewTreeSavedStateRegistryOwner(composeView.findViewTreeSavedStateRegistryOwner())
        setContentView(dialogLayout)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
    }

    override val subCompositionView
        get() = dialogLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkNotNull(window).run {
            setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL)
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        onDismissRequest()
    }

    override fun cancel() {
        onDismissRequest()
    }

    fun setContent(
        parent: CompositionContext,
        content: @Composable () -> Unit,
    ) {
        dialogLayout.setContent(parent, content)
    }

    fun disposeComposition() {
        dialogLayout.disposeComposition()
    }
}

@Suppress("ViewConstructor")
private class ModalBottomDialogLayout(context: Context) : AbstractComposeView(context) {
    private var composableContent: @Composable () -> Unit by mutableStateOf({})

    override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    fun setContent(
        parent: CompositionContext,
        content: @Composable () -> Unit,
    ) {
        setParentCompositionContext(parent)
        composableContent = content
        shouldCreateCompositionOnAttachedToWindow = true
        createComposition()
    }

    @Composable
    override fun Content() {
        composableContent()
    }
}

@Composable
private fun ModalBottomDialogLayout(
    insets: PaddingValues = PaddingValues(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(
                start = insets.calculateStartPadding(LocalLayoutDirection.current),
                top = insets.calculateTopPadding(),
                end = insets.calculateEndPadding(LocalLayoutDirection.current),
            )
            .semantics { dialog() },
        shape = AppTheme.shapes.extraLarge.copy(
            bottomStart = CornerSize(0.0.dp),
            bottomEnd = CornerSize(0.0.dp),
        ),
        tonalElevation = AppTheme.elevations.large,
        shadowElevation = AppTheme.elevations.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = AppTheme.spaces.extraLarge,
                    bottom = AppTheme.spaces.large + insets.calculateBottomPadding(),
                ),
            content = content,
        )
    }
}
