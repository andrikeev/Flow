package flow.connection

internal sealed interface ConnectionsSideEffect {
    data object ShowConnectionDialog : ConnectionsSideEffect
}
