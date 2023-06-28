package flow.connection

internal sealed interface ConnectionsSideEffect {
    object ShowConnectionDialog : ConnectionsSideEffect
}
