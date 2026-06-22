# Big-bang Этапа 3: полный переход DI с Hilt на Koin

**Статус:** план к согласованию
**Контекст:** продолжение `docs/kmp-migration-plan.md` (Этап 3). «Чистая» strangler-фаза
(инфра-модули с 1–2 биндингами) завершена; оставшиеся модули взаимосвязаны и
мигрируются вместе.

---

## 1. Зачем отдельный план

Per-module Hilt-мост в `:app` работал для инфраструктуры (dispatchers, logger,
auth:impl, preferences, notifications, downloads). Дальше он **не масштабируется**:

- `core:data` — 16 биндингов (репозитории + сервисы) с глубоким деревом зависимостей.
- `core:network:impl` — `@Multibinds Set<Interceptor>`, провайдер `OkHttpClient`,
  взаимозависимые биндинги.
- `core:domain` — десятки use-case'ов, инжектятся в `@HiltViewModel`.
- `feature:*` (15) — `@HiltViewModel` + `viewModel() = hiltViewModel()`.
- `core:work:impl` — `@HiltWorker` + `HiltWorkerFactory`.

Это требует поднять **Koin как единый граф** и перевести потребителей (вплоть до
ViewModel'ей и Worker'ов), после чего удалить Hilt целиком.

---

## 2. Что уже готово

- 6 модулей **Hilt-free**: `dispatchers`, `logger`, `auth:impl` (есть Koin-модули
  `dispatchersModule`/`loggerModule`/`authModule`); `preferences`, `notifications`,
  `downloads` (пока только фабрики + Hilt-мост, Koin-модулей нет — Android-specific).
- В `:app` — 6 тонких Hilt-мостов (`me.rutrackersearch.app.di.*`), конструирующих
  сервисы через фабрики `createX(...)`.

---

## 3. Целевая архитектура DI

- Каждый модуль экспортирует **свой Koin-модуль** (`val xModule = module { ... }`).
- `:app` поднимает Koin в `Application.onCreate()`:
  ```kotlin
  startKoin {
      androidContext(this@FlowApplication)
      workManagerFactory()            // koin-androidx-workmanager
      modules(appModules)             // список всех модулей
  }
  ```
- Composable'ы получают VM через `koinViewModel()` (koin-compose-viewmodel).
- Worker'ы — через `KoinWorkerFactory` (koin-androidx-workmanager).
- `:app` остаётся `Application` (не `@HiltAndroidApp`); Firebase/Crashlytics — как есть.

### Зависимости (libs.versions.toml)
- `koin-android` (androidContext, viewModel DSL, WorkManager).
- `koin-androidx-compose` / `koin-compose-viewmodel` (`koinViewModel()`).
- `koin-androidx-workmanager` (`workerOf`, `KoinWorkerFactory`).
- `koin-test` (`checkModules`/`verify` в unit-тестах).
- *(опционально)* `koin-annotations` + KSP — автогенерация определений для domain/features.

### Новые конвеншн-плагины (см. §5 основного плана)
- `flow.koin` — заменяет `flow.android.hilt`: подключает koin-core/koin-android.
- `flow.kmp.feature` (позже, на UI-этапе) — Koin + lifecycle-viewmodel KMP вместо Hilt.

---

## 4. Стратегия сосуществования на переходе (ключевое)

Мигрируем **снизу вверх**, и на каждом шаге **Koin — единственный владелец**
смигрированных объектов (никаких дублей singleton'ов). Потребители, ещё сидящие на
Hilt, получают Koin-объект через **inverse-bridge**:

```kotlin
// :app, временный мост Hilt -> Koin (на время перехода)
@Module @InstallIn(SingletonComponent::class)
object DispatchersBridge {
    @Provides fun dispatchers(): Dispatchers = GlobalContext.get().get()
}
```

То есть текущие прямые мосты (`createX()`) заменяются на чтение из Koin, как только у
модуля появляется Koin-модуль. Это гарантирует **единственный инстанс** (важно для
stateful — `AuthServiceImpl`, репозитории) при смешанном графе. Когда последний
потребитель (features) переедет на Koin — мосты и Hilt удаляются.

Инвариант: **зелёный Android после каждой фазы** (отдельный PR на фазу).

---

## 5. Поэтапный план (фазы big-bang)

### Ф0. Bootstrap Koin
- Добавить koin-android/compose/workmanager/test в каталог; плагин `flow.koin`.
- В `:app`: `startKoin { androidContext(...); modules(emptyList()) }` (пустой граф),
  Hilt продолжает работать. Проверить, что приложение поднимает оба контейнера.
- Добавить unit-тест `KoinGraphTest` с `checkModules { }` (пока тривиальный) —
  **каркас проверки графа в CI**.

### Ф1. Инфраструктура → Koin
- Подключить существующие `dispatchersModule`/`loggerModule`/`authModule` в `startKoin`.
- Добавить Koin-модули для `preferences`/`notifications`/`downloads`
  (`single { createX(androidContext()) }`).
- Текущие 6 прямых Hilt-мостов заменить на **inverse-bridge** (читать из Koin).
- Расширить `KoinGraphTest` (`androidContext` мок/Robolectric) — проверка резолва.

### Ф2. `core:data` → Koin
- Снять Hilt; `dataModule` с 16 `single { ... }` (репозитории + сервисы).
- Context-сервисы (`ConnectionServiceImpl`, `StoreServiceImpl`) — `androidContext()`.
- Hilt-потребители (domain) временно получают репозитории через inverse-bridge — но
  чтобы не плодить 16 мостов, **перевести domain в той же фазе или сразу за data**.

### Ф3. `core:network:impl` → Koin
- `networkModule`: `single { OkHttpClient.Builder()... }` (proxy + перехватчики),
  `Set<Interceptor>` через Koin (`getAll()` или явный список), `NetworkApi`,
  `ImageLoader`, `ProxyController`. Debug — отдельный модуль с Chucker.

### Ф4. `core:domain` → Koin
- Use-case'ы — `factory { XUseCase(get(), ...) }` (или `factoryOf(::XUseCase)`).
- Их десятки → **рекомендуется Koin Annotations (KSP)**: `@Factory`/`@Single` +
  автосборка модуля; резко сокращает boilerplate и риск ошибок.
- После Ф2–Ф4 граф data+domain полностью на Koin; убрать соответствующие мосты.

### Ф5. `feature:*` → Koin (ViewModel'и)
- В каждом модуле: `val xFeatureModule = module { viewModelOf(::XViewModel) }`
  (или `viewModel { XViewModel(get(), ...) }`).
- `@HiltViewModel`/`@Inject` убрать; конструктор остаётся прежним.
- **Переключить `core:navigation/viewModel()`**: `hiltViewModel()` → `koinViewModel()`
  (koin-compose-viewmodel). Это флипает все фичи разом — поэтому VM всех 15 фич
  мигрируются в одной фазе (механически).
- Assisted-вариант (`viewModel(creationCallback)`) → `koinViewModel { parametersOf(...) }`.
- Orbit не меняется (это обычный `ViewModel`).

### Ф6. `core:work:impl` → Koin
- `@HiltWorker` → конструкторные воркеры; `workerOf(::XWorker)` в Koin-модуле.
- `KoinWorkerFactory` (koin-androidx-workmanager) вместо `HiltWorkerFactory`.
- Убрать `hilt-work`, `DelegatingWorker`-обвязку при необходимости.

### Ф7. Удаление Hilt
- Удалить все Hilt-мосты из `:app`, плагин `flow.android.hilt`, hilt-зависимости,
  `@HiltAndroidApp` → обычный `Application`, KSP hilt-compiler.
- Удалить `androidx.hilt.navigation-compose`, `hilt-work`.
- Финальный прогон `checkModules`/`verify` + smoke-запуск.

---

## 6. Миграция ViewModel: до/после

```kotlin
// До (Hilt)
@HiltViewModel
class RatingViewModel @Inject constructor(
    private val appLaunchedUseCase: AppLaunchedUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<...>

// Composable: RatingDialog(viewModel())   // viewModel() == hiltViewModel()
```
```kotlin
// После (Koin)
class RatingViewModel(
    private val appLaunchedUseCase: AppLaunchedUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<...>

// Koin module:  viewModelOf(::RatingViewModel)
// core:navigation:  inline fun <reified VM: ViewModel> viewModel(): VM = koinViewModel()
```

---

## 7. `@HiltWorker` → Koin Worker

```kotlin
// До
@HiltWorker
class SyncFavoritesWorker @AssistedInject constructor(
    @Assisted ctx: Context, @Assisted params: WorkerParameters,
    private val useCase: SyncFavoritesUseCase,
) : CoroutineWorker(ctx, params)

// После
class SyncFavoritesWorker(
    ctx: Context, params: WorkerParameters,
    private val useCase: SyncFavoritesUseCase,
) : CoroutineWorker(ctx, params)
// Koin:  workerOf(::SyncFavoritesWorker)
// app:   startKoin { workManagerFactory() }  (+ KoinWorkerFactory в манифесте/инициализации)
```

---

## 8. Тестирование и снижение рисков

- **Главный инструмент:** `org.koin.test.verify.verify()` / `checkModules { }` в unit-тесте —
  валидирует, что граф полон (все зависимости резолвятся), **на этапе тестов в CI**.
  Это закрывает основной риск Koin (ошибки графа всплывают в рантайме, а не на компиляции),
  учитывая что локальная сборка недоступна.
- `core:testing`: перевести с Hilt-тест-инфраструктуры на `koin-test`; фейки
  (`TestDispatchers`, `TestLoggerFactory`) подаются через тестовый Koin-модуль.
- Smoke-тест запуска `startKoin` + резолв ключевых корней (NavHost, root VM).
- **Чек-лист на каждый де-Hilt модуль** (уроки strangler-фазы): после удаления
  `flow.android.hilt` грепнуть `import androidx.*` и `kotlinx.coroutines.*` — они
  приходили транзитивно через `hilt-android`; добавить явные `androidx.core.ktx`/
  `coroutines-core`, где нужно.

---

## 9. Порядок PR (исполнение)

1. Ф0 — bootstrap Koin + `KoinGraphTest` каркас.
2. Ф1 — инфраструктура в Koin + inverse-bridge.
3. Ф2+Ф4 — data + domain в Koin (вместе, чтобы не плодить мосты).
4. Ф3 — network:impl.
5. Ф5 — все feature ViewModel'и + флип `viewModel()`.
6. Ф6 — work:impl.
7. Ф7 — удаление Hilt.

Каждый PR: зелёный `:app:assembleDebug` + `testDebugUnitTest` (+ растущий `checkModules`).

---

## 10. Решения (согласовано)

1. **Koin Annotations (KSP)** — **используем** для domain и features (резко сокращает boilerplate; `verify()` работает по сгенерированным определениям).
2. **`data` и `domain`** — мигрируем **раздельными PR** (мельче диффы; на стыке временно держим inverse-bridge для репозиториев, потребляемых ещё-Hilt-domain).
3. **`work:impl`** — мигрируем **отдельным шагом** (Ф6), не откладываем за пределы Этапа 3.
4. **Без промежуточного «оба контейнера» в проде** — доводим big-bang до конца (Hilt удаляется полностью в Ф7) перед релизом.

### Уточнение порядка по зависимостям (важно для исполнения)
Перенос модуля во владение Koin возможен только когда **его зависимости уже в Koin**
(иначе `get()` в Koin-определении не резолвится). Безопасный порядок:
`dispatchers`/`logger` (без зависимостей) → `preferences`/`notifications`/`downloads`
(Context + dispatchers) → `data` → `network:impl` → `auth:impl` (нужны NetworkApi +
PreferencesStorage) → `domain` → `features` → `work:impl` → удаление Hilt.
На стыках, где потребитель ещё на Hilt, а зависимость уже в Koin — inverse-bridge
(Hilt `@Provides` читает из Koin), единственный владелец — Koin.
