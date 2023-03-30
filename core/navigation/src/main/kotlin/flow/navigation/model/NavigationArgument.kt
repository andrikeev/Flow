package flow.navigation.model

sealed interface NavigationArgument {
    val name: kotlin.String

    data class Int(override val name: kotlin.String) : NavigationArgument
    data class String(override val name: kotlin.String) : NavigationArgument
    data class StringList(override val name: kotlin.String) : NavigationArgument
}
