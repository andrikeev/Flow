package flow.navigation.model

data class NavigationOptions(
    val showNavigationBar: Boolean = false,
) {
    companion object {
        val Empty = NavigationOptions()
    }
}
