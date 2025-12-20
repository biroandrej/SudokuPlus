# Application Cleanup TODO

## Priority Legend
- **P0** - Critical / Breaking
- **P1** - High priority
- **P2** - Medium priority
- **P3** - Low priority / Nice to have

---

## Completed Tasks

- [x] **P1** - Remove Accompanist SystemUiController from Theme.kt
- [x] **P1** - Replace Accompanist Pager with native HorizontalPager in LearnScreen.kt
- [x] **P1** - Remove Accompanist from libs.versions.toml and build.gradle.kts
- [x] **P2** - Remove deprecated composeOptions.kotlinCompilerExtensionVersion
- [x] **P3** - Remove unused dependencies (foundation-android, espresso-core, ext-junit)

---

## Dependency Cleanup

### ~~P1 - Remove Deprecated Accompanist Libraries~~ DONE

> **Can Accompanist be removed?** YES - Both libraries have native Compose replacements available since Compose 1.4+. The app already uses `enableEdgeToEdge()` for edge-to-edge support, making `accompanist-systemuicontroller` redundant.

Both Accompanist libraries are deprecated and have native Compose replacements.

| Library | Usage | Replacement |
|---------|-------|-------------|
| `accompanist-systemuicontroller` | `Theme.kt:131-154` | Remove - already using `enableEdgeToEdge()` in MainActivity |
| `accompanist-pager` | `LearnScreen.kt:26-28, 63, 73, 92-97` | Native `androidx.compose.foundation.pager.HorizontalPager` |

**Files to modify:**
- `app/src/main/java/sk/awisoft/sudokuplus/ui/theme/Theme.kt`
- `app/src/main/java/sk/awisoft/sudokuplus/ui/learn/LearnScreen.kt`
- `gradle/libs.versions.toml` - remove accompanist entries
- `app/build.gradle.kts` - remove accompanist dependencies

#### Step 1: Remove SystemUiController from Theme.kt

**Current code to remove (lines 131-155):**
```kotlin
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// Inside SudokuPlusTheme function:
val systemUiController = rememberSystemUiController()

// Inside DynamicMaterialTheme content:
SideEffect {
    systemUiController.setSystemBarsColor(
        color = Color.Transparent,
        darkIcons = !darkTheme
    )
}
```

**Action:** Delete the import and all `systemUiController` references. Edge-to-edge is already configured in MainActivity via `enableEdgeToEdge()` and the splash screen theme.

#### Step 2: Replace Accompanist Pager in LearnScreen.kt

**Before:**
```kotlin
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
fun LearnScreen(...) {
    val pagerState = rememberPagerState()

    TabRow(selectedTabIndex = pagerState.currentPage) {
        // ...
        pagerState.animateScrollToPage(index, 0f)
    }

    HorizontalPager(
        modifier = Modifier.fillMaxHeight(),
        count = pages.size,
        state = pagerState,
        verticalAlignment = Alignment.Top
    ) { page ->
        // ...
    }
}
```

**After:**
```kotlin
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState

@OptIn(ExperimentalMaterial3Api::class)
fun LearnScreen(...) {
    val pagerState = rememberPagerState(pageCount = { pages.size })

    TabRow(selectedTabIndex = pagerState.currentPage) {
        // ...
        pagerState.animateScrollToPage(index)
    }

    HorizontalPager(
        modifier = Modifier.fillMaxHeight(),
        state = pagerState,
        verticalAlignment = Alignment.Top
    ) { page ->
        // ...
    }
}
```

**Key API differences:**
| Accompanist | Native Compose |
|-------------|----------------|
| `rememberPagerState()` | `rememberPagerState(pageCount = { count })` |
| `HorizontalPager(count = n, state = ...)` | `HorizontalPager(state = ...)` |
| `animateScrollToPage(index, 0f)` | `animateScrollToPage(index)` |
| `@OptIn(ExperimentalPagerApi::class)` | Not needed |

#### Step 3: Remove from libs.versions.toml

```diff
  [versions]
- accompanist-pager = "0.28.0"
- accompanist-systemuicontroller = "0.28.0"

  [libraries]
- accompanist-pager-indicators = { group = "com.google.accompanist", name = "accompanist-pager-indicators", version.ref = "accompanist-pager" }
- accompanist-systemuicontroller = { group = "com.google.accompanist", name = "accompanist-systemuicontroller", version.ref = "accompanist-systemuicontroller" }
```

#### Step 4: Remove from build.gradle.kts

```diff
  dependencies {
-     implementation(libs.accompanist.systemuicontroller)
-     implementation(libs.accompanist.pager.indicators)
  }
```

### ~~P2 - Remove Deprecated composeOptions~~ DONE

```kotlin
// build.gradle.kts - REMOVED this block (handled by compose-compiler plugin)
composeOptions {
    kotlinCompilerExtensionVersion = "1.5.14"
}
```

### ~~P3 - Remove Unused Dependencies~~ DONE

| Dependency | Location | Reason |
|------------|----------|--------|
| ~~`foundation-android`~~ | ~~libs.versions.toml~~ | Removed |
| ~~`espresso-core`~~ | ~~libs.versions.toml~~ | Removed |
| ~~`ext-junit`~~ | ~~libs.versions.toml~~ | Removed |

---

## Dependency Updates

### P2 - Update Outdated Libraries

| Library | Current | Latest | Notes |
|---------|---------|--------|-------|
| `aboutLibraries` | 10.6.1 | 11.x | Minor breaking changes |
| `compose-destinations` | 1.11.6 | 2.x | Major breaking changes - evaluate effort |
| `composeMarkdown` | 0.5.4 | Check latest | Third-party library |

---

## Code TODOs

### P2 - Address Existing TODOs in Code

| File | Line | Description |
|------|------|-------------|
| `GameStatsSection.kt` | 333 | FlowRow cross-axis arrangement workaround - check if now supported |
| `BackupScreen.kt` | 558 | Get readable name from other sources |
| `AdvancedHint.kt` | 15 | Incomplete TODO documentation |
| `AdvancedHint.kt` | 145 | Add boxes to hint system |

---

## Code Quality

### P2 - Add Unit Tests

- Test directory `app/src/test/kotlin` exists but appears empty
- Priority areas for testing:
  - `QQWing` puzzle generation/difficulty
  - `GameViewModel` game logic
  - Database repositories
  - XP/Level calculations

### P3 - Configure Room Schema Export

Add to `build.gradle.kts` in ksp block:
```kotlin
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
```

### P3 - Add Missing Database Index

`SudokuBoard` entity has `folder_id` foreign key but no index. Add:
```kotlin
@Entity(
    indices = [Index(value = ["folder_id"])]
)
```

---

## Build Configuration

### P3 - Clean Up Gradle Configuration

- [ ] Review ProGuard rules for unused entries
- [ ] Ensure R8 full mode is enabled
- [ ] Review signing configuration for CI/CD

---

## Summary

| Category | P0 | P1 | P2 | P3 | Total | Completed |
|----------|----|----|----|----|-------|-----------|
| Dependencies | 0 | ~~1~~ | ~~2~~ | ~~1~~ | 4 | **4** |
| Code TODOs | 0 | 0 | 4 | 0 | 4 | 0 |
| Code Quality | 0 | 0 | 1 | 2 | 3 | 0 |
| Build Config | 0 | 0 | 0 | 1 | 1 | 0 |
| **Total** | **0** | **1** | **7** | **4** | **12** | **4** |

### Progress: 4/12 tasks completed (33%)
