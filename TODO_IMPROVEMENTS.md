# TODO - Code & UX Improvements

## Priority Legend
- **P0** Critical / breaking
- **P1** High priority
- **P2** Medium priority
- **P3** Low priority / nice to have

## Code Quality & Stability
- [x] **P1** Remove `runBlocking` on the main thread and workers; switch to suspending flows + scoped launches in `HomeScreen.kt` navigation, `HomeViewModel.startGame`, `GameViewModel.getAdvancedHint`, and the backup pipeline (`data/backup/BackupWorker.kt`, `data/backup/SettingsBackup.kt`) to avoid UI jank/ANRs and blocking WorkManager threads.
- [x] **P1** Rework the game timer/autosave to be lifecycle-aware and crash-safe: guard `pauseTimer()` before init, cancel in `onCleared`, move the `fixedRateTimer` to a coroutine ticker, and debounce Room writes instead of saving every second regardless of board changes (`GameViewModel.kt`, first-game flow in `GameScreen.kt`).
- [x] **P2** Convert home navigation triggers to single-shot events backed by `StateFlow/SharedFlow` rather than mutable flags + `runBlocking` in composition so start/continue/daily flows survive process death and never double-navigate (`HomeViewModel` ready flags, `HomeScreen.kt`).
- [x] **P2** Make the backup pipeline fully suspending and resilient: collect DataStore flows without `runBlocking`, return `Result.retry()` for transient I/O, validate persisted URI before work, and surface failures to UI/notifications (`BackupWorker.kt`, `SettingsBackup.kt`, `BackupScreen.kt`).
- [ ] **P2** Add targeted tests: unit tests for `GameViewModel` timer/hint/mistake-limit paths, daily streak calculation, backup retention pruning, and import parsing; plus Compose UI smoke tests for bottom navigation and start/continue game flows.
- [x] **P3** Clean up Gradle config: merge duplicated `ksp {}` blocks, centralize schema export settings, and consider a Compose BOM to keep Kotlin 2.3 + Compose artifacts in sync (`app/build.gradle.kts`, `gradle/libs.versions.toml`).

## User Experience
- [x] **P1** Restore file-import deep link handling (commented out in `MainActivity.kt`): handle `ACTION_VIEW` in the manifest/activity and route safely to `ImportFromFileScreen` without relying on the broken compose-destinations deep link.
- [ ] **P2** Improve backup UX: show last backup time, explicit permission/error state when SAF tree access is missing, and a retry prompt instead of silently returning `Result.failure()` (`BackupScreen`, worker logs).
- [ ] **P2** Make long-running flows (puzzle generation/solving, advanced hints) cancellable and visibly stateful, avoiding `runBlocking`/instant navigation while QQWing runs (`HomeScreen.kt`/`HomeViewModel.kt`, `GameViewModel.getAdvancedHint()`).
- [ ] **P3** Add accessibility labels/semantics to icon-only controls so TalkBack users can navigate: bottom bar icons (`NavigationBarComponent.kt`), difficulty/type pickers and scroll-to-top FAB (`HomeScreen.kt`), More screen quick actions (`MoreScreen.kt`), and game toolbar icons.
