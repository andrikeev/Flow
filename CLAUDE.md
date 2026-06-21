# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Flow is an unofficial Android client for rutracker.org (a Russian torrent tracker). It's a Kotlin/Compose app with a multi-module architecture, built on MVI (Orbit), Hilt DI, and androidx Navigation3.

## Build Commands

```bash
./gradlew :app:assembleDebug          # Build debug APK
./gradlew :app:lintDebug              # Run lint
./gradlew testDebugUnitTest           # Run all unit tests
./gradlew :module:path:testDebugUnitTest  # Run tests for a specific module
./gradlew spotlessCheck               # Check code formatting
./gradlew spotlessApply               # Auto-format code (ktlint)
```

CI also runs `testReleaseUnitTest` and `:app:assembleRelease` (requires keystore secrets). The `app/google-services.json` is required for the app module to build — it's decoded from a CI secret; locally you need to provide your own.

## Architecture

### Module Structure

```
app/          → Main application module (Hilt entry point, Activities, nav graph)
core/         → Shared foundation modules (data, network, domain, UI, etc.)
feature/      → Feature modules (each a self-contained UI + ViewModel unit)
proxy/        → Ktor-based HTTP proxy server (standalone, uses Koin not Hilt)
buildSrc/     → Convention plugins (applied via plugin id in each module's build.gradle.kts)
```

All modules are declared in `settings.gradle.kts`. Dependency versions are centralized in `gradle/libs.versions.toml`.

### Convention Plugins (buildSrc)

Each module picks one or more convention plugins that wire up common configuration:

| Plugin | Used by |
|--------|---------|
| `flow.android.application` | `:app` — sets up AGP, Compose, Firebase, ktlint |
| `flow.android.library` | Core/feature library modules |
| `flow.android.library.compose` | Library modules needing Compose |
| `flow.android.feature` | All `feature:*` modules — auto-adds core deps + Orbit + Hilt |
| `flow.android.hilt` | Any module needing Hilt |
| `flow.kotlin.serialization` | Modules using kotlinx.serialization |
| `flow.kotlin.library` | Pure Kotlin (non-Android) modules |
| `flow.ktor.application` | `:proxy` module |

`flow.android.feature` automatically adds these as `implementation` dependencies: `core:common`, `core:designsystem`, `core:domain`, `core:dispatchers`, `core:logger`, `core:models`, `core:navigation`, `core:ui`, Orbit MVI, and `kotlinx-coroutines-android`. It also adds `core:testing` as `testImplementation`.

### MVI Pattern (Orbit)

Every feature ViewModel follows the Orbit MVI pattern:

```kotlin
@HiltViewModel
internal class FooViewModel @Inject constructor(...) : ViewModel(), ContainerHost<FooState, FooSideEffect> {
    override val container: Container<FooState, FooSideEffect> = container(
        initialState = FooState.Initial,
        onCreate = { repeatOnSubscription { /* observe flows here */ } },
    )

    fun perform(action: FooAction) = when (action) {
        is FooAction.SomethingClick -> intent { reduce { /* new state */ } }
        is FooAction.Navigate -> intent { postSideEffect(FooSideEffect.OpenSomething) }
    }
}
```

- **State**: `sealed interface` with `data object` and `data class` variants
- **Actions**: `sealed interface` representing user intents, dispatched via `viewModel.perform(action)`
- **SideEffects**: One-time events (navigation, dialogs) sent via `postSideEffect`
- All ViewModel classes and their State/Action/SideEffect types are `internal` to the feature module

### Navigation (Navigation3)

Routes are `@Serializable data object` (or `data class` with parameters) implementing `NavKey`:

```kotlin
@Serializable data object SearchHistoryRoute : NavKey
@Serializable data class TopicRoute(val id: String) : NavKey
```

Each feature exposes an `EntryProviderScope<NavKey>.addXxx(callbacks)` extension function that registers its route. The `app` module wires everything together in `MobileNavigation.kt`. Navigation callbacks are passed as lambdas — features never depend on `:app`. Deep links from rutracker.org URLs are resolved in `resolveDeepLink()` in `MobileNavigation.kt`.

### Layer Dependencies

```
feature/* → core:domain → core:data → core:network:*, core:database
                       → core:auth:api
app/ → all features + core modules
```

`core:auth:api` and `core:network:api` define interfaces; `core:auth:impl` and `core:network:impl` provide implementations bound via Hilt. Features depend only on `core:domain` (use cases), never on data/network/auth directly.

### Key Packages

- `flow.models.*` — pure data models (in `core:models`)
- `flow.domain.usecase.*` — use cases, one operation each, named `VerbNounUseCase`
- `flow.domain.model.*` — domain models (e.g., `PagingData`, `LoadStates`)
- `flow.designsystem.*` — shared Compose components, icons, themes (Material3)
- `flow.navigation.*` — Navigator, NavigationState, nav3 utilities

### Platform Handlers

Platform-specific operations (open file, open link, share) are abstracted as interfaces in `core:ui` and provided as `CompositionLocal`s. Implementations live in `:app` and are injected in `MainActivity`.

## Code Conventions

- The Kotlin compiler option `-Xcontext-parameters` is enabled in all Android feature and app modules.
- `internal` visibility is used pervasively — all feature-level composables, ViewModels, and MVI types are `internal`.
- State sealed interfaces use extension properties for derived state (e.g., `val SearchState.showSearchAction: Boolean`).
- `viewModel()` in feature composables is a thin wrapper around `hiltViewModel()` defined in `core:navigation`.
- The proxy module uses **Koin** (not Hilt) and is a completely separate Ktor server — do not mix DI approaches.
