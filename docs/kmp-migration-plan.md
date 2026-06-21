# План перехода Flow на Kotlin Multiplatform

**Таргеты:** Android, iOS, macOS, JVM-desktop
**Статус документа:** черновик / план
**Дата:** 2026-06

---

## 1. Цель и принципы

Перевести Flow с чисто-Android архитектуры на Kotlin Multiplatform (KMP) с
переиспользованием бизнес-логики и UI на всех четырёх таргетах. UI шарится через
**Compose Multiplatform** (CMP).

Принципы миграции:

1. **Снизу вверх.** Сначала переводим листовые модули без зависимостей
   (`models`, `common`), затем data/domain, затем UI и features, в самом конце —
   точки входа приложений.
2. **`commonMain` по умолчанию, `expect/actual` по необходимости.** Платформенный
   код прячем за интерфейсами и `expect/actual`, держим его поверхностным.
3. **Зелёный Android на каждом шаге.** Android-сборка и тесты должны оставаться
   рабочими после каждого этапа. iOS/macOS/desktop добавляем таргетами к уже
   существующим модулям, а не отдельной веткой.
4. **Минимум замен библиотек.** Используем те зависимости, что *уже* KMP-совместимы
   в их текущих версиях (см. §3), чтобы сократить объём правок.

---

## 2. Текущее состояние (инвентаризация)

Модулей: `app`, `proxy` + 21 core-модуль + 15 feature-модулей. DI — Hilt
(кроме `proxy`, где Koin). Сеть — Ktor client (движок OkHttp). БД — Room 2.8.
Изображения — Coil 3. MVI — Orbit 10. Навигация — androidx Navigation3. UI —
Jetpack Compose Material3.

### Готовность модулей к KMP

| Модуль | Готовность | Ключевое действие |
|--------|-----------|-------------------|
| `core:models` | ✅ Чистый Kotlin | Сменить плагин на KMP-library |
| `core:common` | ✅ Чистый Kotlin | Сменить плагин на KMP-library |
| `core:network:api` | ✅ Интерфейсы + DTO (serialization) | KMP-library |
| `core:auth:api` | ✅ Интерфейсы + coroutines | KMP-library |
| `core:work:api` | ✅ Интерфейсы | KMP-library |
| `core:network:rutracker` | ⚠️ jsoup (JVM-only) | **jsoup → Ksoup** |
| `core:dispatchers` | ⚠️ Hilt + `Dispatchers.Main` | Koin + `expect` для Main |
| `core:logger` | ⚠️ Hilt | Koin; `expect` логгер на платформу |
| `core:domain` | ⚠️ Только Hilt | Снять Hilt, перенести в `commonMain` |
| `core:database` | ⚠️ Room (Android Context) | Room KMP + `expect` фабрика драйвера |
| `core:preferences` | ❌ EncryptedSharedPreferences | DataStore / multiplatform-settings + Keychain |
| `core:network:impl` | ❌ OkHttp engine + Hilt + proxy | Ktor engine per-platform, proxy через `expect` |
| `core:data` | ❌ `Context` (Connection/Store services) | `expect/actual` сервисы |
| `core:notifications` | ❌ Android Notifications | `expect` интерфейс + actual на платформу |
| `core:downloads` | ❌ Android DownloadManager | `expect` интерфейс + actual на платформу |
| `core:work:impl` | ❌ WorkManager (Android) | `expect` планировщик; iOS BGTask / desktop coroutine |
| `core:designsystem` | ⚠️ Compose | Compose Multiplatform |
| `core:ui` | ⚠️ Compose + Coil + parcelize | CMP; Coil 3 (KMP); убрать parcelize |
| `core:navigation` | ⚠️ Navigation3 (Android) | См. §6 — стратегия навигации |
| `core:testing` | ⚠️ Hilt + Android test | Koin-test + kotlin.test/kotest |
| `feature:*` (15) | ⚠️ Hilt + Compose + Orbit | Koin + CMP; Orbit уже KMP |
| `app` | ❌ Точка входа Android | Остаётся Android; + новые `iosApp`/`desktopApp` |
| `proxy` | ✅ Ktor + Koin, без Android | Не трогаем (опционально вынести общий код) |

---

## 3. Решения по библиотекам (что уже KMP)

Ключевой вывод: большинство ключевых зависимостей **уже multiplatform** в текущих
версиях проекта — это резко сокращает миграцию.

| Область | Сейчас | Решение для KMP | Комментарий |
|---------|--------|-----------------|-------------|
| **DI** | Hilt 2.59 | **Koin 4.2** (уже в `proxy`) | Hilt — Android-only. Единый Koin во всех shared-модулях. Альтернатива — kotlin-inject/Metro, но Koin уже знаком команде. |
| **БД** | Room 2.8 | **Room KMP** (он же) | Room 2.7+ официально поддерживает KMP (android/ios/jvm/native) с `BundledSQLiteDriver`. **Миграция на SQLDelight не нужна.** |
| **Сеть (client)** | Ktor 3.5 (OkHttp) | **Ktor 3.5** + движки per-platform | `OkHttp`/`CIO` на Android/JVM, `Darwin` на iOS/macOS. API клиента общий. |
| **Изображения** | Coil 3.4 (okhttp) | **Coil 3** (KMP) | Coil 3 — полноценный KMP. Заменить `coil-network-okhttp` на `coil-network-ktor3`. |
| **MVI** | Orbit 10 | **Orbit 10** (он же) | Orbit MVI 10 — multiplatform из коробки. |
| **ViewModel** | androidx.lifecycle 2.9 | **lifecycle-viewmodel** (KMP) | androidx lifecycle-viewmodel 2.8+ — multiplatform. |
| **Сериализация** | kotlinx-serialization 1.11 | без изменений | Уже KMP. |
| **Дата/время** | kotlinx-datetime 0.8 | без изменений | Уже KMP. |
| **Корутины** | kotlinx-coroutines 1.11 | `-core` вместо `-android` в common | Уже KMP. |
| **HTML-парсинг** | jsoup 1.22 | **Ksoup** (`com.fleeksoft.ksoup`) | jsoup — JVM-only. Ksoup — KMP-порт с близким API. ~10 файлов в `rutracker`. |
| **Secure storage** | EncryptedSharedPreferences | **DataStore (KMP)** + шифрование per-platform / multiplatform-settings + Keychain | androidx.security — Android-only. |
| **UI** | Jetpack Compose | **Compose Multiplatform** (JetBrains) | Material3 поддержан; iOS/desktop/android из одного кода. |
| **Навигация** | Navigation3 (Android) | См. §6 | Спорная зона — отдельное решение. |
| **Фоновые задачи** | WorkManager | `expect` планировщик | iOS: BGTaskScheduler; desktop: корутины/таймер. |
| **Crashlytics** | Firebase | Остаётся на Android; для iOS — отдельно (Crashlytics iOS SDK) | Точки входа платформенные. |

---

## 4. Целевая структура модуля (source sets)

Каждый шаримый модуль переводится с `flow.android.library` на новый
KMP-конвеншн-плагин со структурой:

```
core/<module>/
  src/
    commonMain/kotlin     # 95% кода: бизнес-логика, DTO, use cases, Compose UI
    commonTest/kotlin
    androidMain/kotlin    # actual: Context, Android API
    iosMain/kotlin        # actual: Darwin, Keychain, BGTask
    jvmMain/kotlin        # actual: desktop (JVM)
    appleMain/kotlin      # общий код iOS + macOS (через иерархию source sets)
```

Иерархия таргетов (default hierarchy template KMP):

```
common
 ├── android
 ├── jvm                  (desktop)
 └── apple
      ├── ios (iosArm64, iosSimulatorArm64, iosX64)
      └── macos (macosArm64, macosX64)
```

`appleMain` переиспользуется iOS и macOS (Darwin engine, Keychain, NSUserDefaults и т.п.).

---

## 5. Новые / изменённые конвеншн-плагины (buildSrc)

1. `flow.kmp.library` — заменяет `flow.kotlin.library` для шаримых не-UI модулей:
   `kotlin("multiplatform")`, таргеты `androidTarget()/jvm()/ios*()/macos*()`,
   `applyDefaultHierarchyTemplate()`, общий `commonMain` с coroutines-core,
   Spotless.
2. `flow.kmp.library.compose` — то же + Compose Multiplatform
   (`org.jetbrains.compose`, `compose.runtime/foundation/material3/components.resources`).
3. `flow.kmp.feature` — аналог `flow.android.feature`: подключает базовые core-модули,
   Orbit, lifecycle-viewmodel (KMP), Koin вместо Hilt.
4. `flow.koin` — заменяет `flow.android.hilt`: подключает `koin-core`
   (+ `koin-android`, `koin-compose-viewmodel` в нужных таргетах).
5. `flow.kmp.room` — Room KMP: KSP для всех таргетов + `BundledSQLiteDriver`.
6. Удаляются по мере миграции: `flow.android.hilt`, `flow.kotlin.library`
   (где заменён), `flow.android.feature`.

Сохраняются: `flow.android.application` (для `app`), `flow.ktor.application` (`proxy`),
`flow.kotlin.serialization`, `StaticAnalysis`.

---

## 6. Навигация (отдельное архитектурное решение)

Navigation3 (androidx) на момент написания — Android-ориентированная. Текущий
паттерн проекта (`@Serializable NavKey` + `EntryProviderScope.addXxx()` в каждой
feature, сборка в `MobileNavigation.kt` в `:app`) хорошо изолирован, что упрощает
замену.

Варианты (выбрать на этапе 6):

- **A. Compose Multiplatform Navigation** (`androidx.navigation:navigation-compose`
  в KMP-варианте от JetBrains) — type-safe routes, близко к текущему подходу.
  **Рекомендуется.**
- **B. Decompose** — мощный, но требует переписать навигацию иначе (компонентная модель).
- **C. Сохранить Navigation3 на Android + абстракция** — если хочется отложить;
  feature-модули зависят от абстрактного `Navigator`, реализации платформенные.

В любом варианте: `NavKey`/маршруты переезжают в `commonMain` (они уже
`@Serializable`), а `resolveDeepLink()` обобщается (URL rutracker.org одинаков на всех платформах).

---

## 7. Поэтапный план

Каждый этап завершается зелёной Android-сборкой и PR. iOS/macOS/desktop-таргеты
добавляются инкрементально; «реальное» приложение под них собирается на этапах 8–9.

### Этап 0. Подготовка инфраструктуры
- Обновить Gradle/AGP при необходимости для KMP + Compose Multiplatform.
- Добавить в `libs.versions.toml`: `compose-multiplatform`, `koin` (android/compose),
  `ksoup`, `coil-network-ktor`, `ktor-client-darwin`, `lifecycle-viewmodel` (KMP),
  `room` (с KMP-артефактами), `datastore`/`multiplatform-settings`.
- Завести каркасные конвеншн-плагины из §5 (без массового применения).
- **Без миграции кода — только сборочная обвязка.**

### Этап 1. Листовые чистые модули
- `core:models`, `core:common`, `core:auth:api`, `core:work:api`,
  `core:network:api` → `flow.kmp.library`, код в `commonMain`.
- Это уже почти чистый Kotlin — наименьший риск, проверка инфраструктуры KMP.

### Этап 2. rutracker-парсер (jsoup → Ksoup)
- В `core:network:rutracker` заменить `org.jsoup` на `com.fleeksoft.ksoup`
  (~10 файлов: `Parse*UseCase`, `Get*UseCase`, `Utils.kt`).
- Перенести в `commonMain`. Покрыть существующими тестами парсинга (критично —
  это бизнес-ядро скрейпинга).

### Этап 3. DI: Hilt → Koin (без смены таргетов)
- Сначала **на Android** заменить Hilt на Koin модуль за модулем (constructor
  injection сохраняется, меняются только DI-модули и точка сборки графа).
- `core:dispatchers`, `core:logger`, `core:domain` — снять Hilt, описать Koin-модули.
- Это самый объёмный «горизонтальный» рефакторинг; делать отдельными PR по слоям.

### Этап 4. БД и хранилище
- `core:database` → Room KMP: `expect fun createDatabaseBuilder()` (actual:
  Android Context / iOS NSHomeDirectory / desktop файл), `BundledSQLiteDriver`.
- `core:preferences` → DataStore (KMP) или multiplatform-settings; на Android
  оставить миграцию со старого EncryptedSharedPreferences; на iOS — Keychain для
  чувствительных данных (токены).

### Этап 5. Сеть и платформенные сервисы
- `core:network:impl`: `expect fun httpClientEngine()` (OkHttp/CIO ↔ Darwin);
  proxy-логику (`ProxySelector`/`Authenticator` — java.net) спрятать за
  `expect ProxyController`, actual на Android/JVM (java.net) и iOS (Ktor proxy / NSURLSession config).
- Coil: `coil-network-okhttp` → `coil-network-ktor3`.
- `core:data`: `ConnectionServiceImpl`, `StoreServiceImpl` → `expect/actual`
  (Android: ConnectivityManager/PackageManager; iOS: NWPathMonitor/App Store;
  desktop: заглушка/Java NetworkInterface).
- `core:work:impl`, `core:notifications`, `core:downloads` → `expect` интерфейсы
  + actual per-platform (Android сначала, остальные — заглушки до этапа 8).

### Этап 6. Навигация и дизайн-система
- Принять решение из §6 (рекомендация — CMP Navigation).
- `core:designsystem`, `core:ui` → `flow.kmp.library.compose`, код в `commonMain`,
  ресурсы → `compose.components.resources`. Убрать `kotlin-parcelize`
  (заменить на `@Serializable`/`Stable`-холдеры).

### Этап 7. Feature-модули
- Перевести 15 `feature:*` на `flow.kmp.feature` + CMP, по одной фиче за PR.
- ViewModel → KMP lifecycle-viewmodel; Orbit уже KMP; DI — Koin.
- Начать с простых (`menu`, `connection`, `rating`), закончить сложными
  (`topic`, `search`, `forum`).

### Этап 8. Точки входа платформ
- `app` остаётся Android-приложением (но зависит от шаримого UI).
- Новый `iosApp` (Xcode + KMP framework) и `desktopApp` (JVM, `application{}` +
  Compose Desktop). macOS — как desktop (JVM) и/или нативный через Compose
  (на старте проще JVM-desktop, нативный macOS опционально).
- Реализовать actual для notifications/downloads/work на iOS и desktop.

### Этап 9. CI, тесты, релиз
- CI: добавить сборку iOS framework (на macOS-раннере), desktop-сборку, тесты
  `commonTest` на всех таргетах.
- Перевести `core:testing` на Koin-test + kotlin.test; убрать mockk-android из common.
- Настроить дистрибуцию desktop (`packageDistributionForCurrentOS`) и iOS (App Store / TestFlight).

---

## 8. Риски и открытые вопросы

| Риск | Митигация |
|------|-----------|
| Navigation3 не имеет зрелого KMP-аналога | Решение §6; рекомендуется CMP Navigation, маршруты уже `@Serializable`. |
| Объём замены Hilt→Koin (много модулей) | Делать на Android до смены таргетов (этап 3), мелкими PR по слоям. |
| Proxy/`ProxySelector` (java.net) на iOS | Через `expect ProxyController`; iOS — конфиг Ktor/NSURLSession. |
| TV-зависимости (`androidx.tv.*`) | Остаются в Android-таргете (`androidMain`), из common не используются. |
| Firebase Crashlytics только Android | Платформенные точки входа; iOS — отдельный Crashlytics SDK. |
| Ksoup ≠ 100% jsoup API | Покрыть парсер тестами до и после замены (этап 2). |
| Сроки | План инкрементальный: ценность (общая логика) доступна уже после этапов 1–5 даже без iOS-UI. |

---

## 9. Краткое резюме

Архитектура Flow **хорошо подходит** для KMP: чёткое разделение api/impl,
интерфейсные границы, Orbit/Ktor/Room/Coil уже KMP-совместимы. Основные работы —
это (1) **Hilt → Koin**, (2) **jsoup → Ksoup**, (3) `expect/actual` для
`Context`-зависимого кода, secure storage и фоновых задач, (4) перевод UI на
**Compose Multiplatform** и выбор навигации. Замена БД на SQLDelight **не нужна** —
Room поддерживает KMP. Миграция выполняется снизу вверх с сохранением рабочей
Android-сборки на каждом этапе.
