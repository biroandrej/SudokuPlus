# Sudoku Plus Feature Roadmap

## Overview

This document outlines the implementation plan for new features to increase user engagement and retention.

**Total Estimated Effort:** 89-119 MD (Man-Days)

| Phase | Features | Effort | Timeline |
|-------|----------|--------|----------|
| Phase 1 | Daily Challenges, Achievements, Notifications | 21-28 MD | Sprint 1-2 |
| Phase 2 | XP System, Reward Calendar, Unlockables | 18-24 MD | Sprint 3-4 |
| Phase 3 | Weekly Challenges, Mini-Games | 15-20 MD | Sprint 5-6 |
| Phase 4 | Social Features, Leaderboards, Cloud Sync | 35-47 MD | Sprint 7-10 |

---

## Phase 1: Core Engagement Features

### 1.1 Daily Challenges

**Description:** A unique daily puzzle that's the same for all users, with streak tracking and a calendar view.

**Effort: 8-10 MD**

| Task | Description | MD |
|------|-------------|-----|
| Database | Add `DailyChallenge` entity with date, seed, completion status | 0.5 |
| Generator | Deterministic puzzle generation based on date seed | 1.5 |
| Repository | `DailyChallengeRepository` with completion tracking | 1 |
| ViewModel | `DailyChallengeViewModel` with state management | 1 |
| UI - Main | Daily challenge card on home screen | 1 |
| UI - Calendar | Calendar view showing completed days | 2 |
| UI - Completion | Special completion dialog with stats | 0.5 |
| Streak Logic | Daily streak tracking (separate from regular streak) | 0.5 |
| Testing | Unit tests and UI tests | 1 |

**Database Schema:**
```kotlin
@Entity
data class DailyChallenge(
    @PrimaryKey val date: LocalDate,
    val difficulty: GameDifficulty,
    val gameType: GameType,
    val seed: Long,
    val completedAt: ZonedDateTime? = null,
    val completionTime: Duration? = null,
    val mistakes: Int = 0
)
```

**Dependencies:** None

---

### 1.2 Achievements/Badges System

**Description:** Gamification system with unlockable achievements and visual badges.

**Effort: 10-12 MD**

| Task | Description | MD |
|------|-------------|-----|
| Design | Define 30-40 achievements with criteria | 1 |
| Database | `Achievement` and `UserAchievement` entities | 0.5 |
| Engine | `AchievementEngine` to check and unlock achievements | 2 |
| Repository | `AchievementRepository` with progress tracking | 1 |
| ViewModel | `AchievementsViewModel` | 0.5 |
| UI - List | Achievements list screen with categories | 1.5 |
| UI - Badge | Badge display components | 1 |
| UI - Unlock | Achievement unlock animation/dialog | 1 |
| UI - Profile | Achievement showcase on profile/stats | 0.5 |
| Integration | Hook into game completion, stats updates | 1 |
| Testing | Unit tests for achievement logic | 1 |

**Achievement Categories:**
```kotlin
enum class AchievementCategory {
    COMPLETION,    // Complete X games
    SPEED,         // Time-based achievements
    ACCURACY,      // No mistakes achievements
    STREAK,        // Streak achievements
    VARIETY,       // Play different modes
    MASTERY,       // Difficulty-based
    SPECIAL        // Events, rare achievements
}

@Entity
data class Achievement(
    @PrimaryKey val id: String,
    val category: AchievementCategory,
    val name: String,
    val description: String,
    val iconRes: Int,
    val requirement: Int,
    val xpReward: Int
)

@Entity
data class UserAchievement(
    @PrimaryKey val achievementId: String,
    val progress: Int,
    val unlockedAt: ZonedDateTime? = null
)
```

**Sample Achievements:**
| ID | Name | Description | Requirement |
|----|------|-------------|-------------|
| `first_win` | First Steps | Complete your first puzzle | 1 game |
| `speed_demon` | Speed Demon | Complete Easy in under 3 minutes | < 180s |
| `perfectionist` | Perfectionist | Complete without mistakes | 0 mistakes |
| `dedicated_7` | Dedicated | 7-day streak | 7 days |
| `centurion` | Centurion | Complete 100 puzzles | 100 games |
| `killer_master` | Killer Instinct | Complete 50 Killer puzzles | 50 killer |
| `giant_slayer` | Giant Slayer | Complete 12x12 Challenge | 1 game |

**Dependencies:** None

---

### 1.3 Push Notifications

**Description:** Local notifications for daily challenges, streak reminders, and events.

**Effort: 3-6 MD**

| Task | Description | MD |
|------|-------------|-----|
| Setup | Add WorkManager/AlarmManager for scheduling | 0.5 |
| Permissions | Notification permission handling (Android 13+) | 0.5 |
| Channels | Create notification channels | 0.5 |
| Daily Reminder | "Daily challenge ready" notification | 0.5 |
| Streak Warning | "Don't lose your streak" evening reminder | 0.5 |
| Settings | Notification preferences in settings | 1 |
| Deep Links | Handle notification taps to open correct screen | 1 |
| Testing | Test on different Android versions | 0.5-2 |

**Notification Types:**
```kotlin
enum class NotificationType {
    DAILY_CHALLENGE_READY,      // 8 AM - New daily puzzle
    STREAK_WARNING,             // 8 PM - Haven't played today
    WEEKLY_CHALLENGE_START,     // Monday - New weekly challenge
    ACHIEVEMENT_PROGRESS,       // 50% progress on achievement
    COMEBACK                    // After 3 days inactive
}
```

**Settings:**
```kotlin
data class NotificationSettings(
    val dailyChallengeEnabled: Boolean = true,
    val dailyChallengeTime: LocalTime = LocalTime.of(8, 0),
    val streakReminderEnabled: Boolean = true,
    val streakReminderTime: LocalTime = LocalTime.of(20, 0),
    val weeklyChallengeEnabled: Boolean = true
)
```

**Dependencies:** Daily Challenges (1.1) for daily notification content

---

## Phase 2: Progression System

### 2.1 XP & Leveling System

**Description:** Experience points earned from playing, with levels and rewards.

**Effort: 8-10 MD**

| Task | Description | MD |
|------|-------------|-----|
| Design | XP curve, level thresholds, multipliers | 1 |
| Database | `UserProgress` entity with XP, level | 0.5 |
| Engine | `XPEngine` for calculating and awarding XP | 1.5 |
| Repository | `ProgressRepository` | 0.5 |
| ViewModel | `ProgressViewModel` | 0.5 |
| UI - XP Bar | XP progress bar component | 1 |
| UI - Level Up | Level up celebration animation | 1 |
| UI - Profile | Level display in stats/profile | 0.5 |
| Integration | Award XP on game completion | 1 |
| Testing | Unit tests for XP calculations | 0.5-1.5 |

**XP System Design:**
```kotlin
data class UserProgress(
    val totalXP: Long = 0,
    val level: Int = 1,
    val currentLevelXP: Long = 0,
    val xpToNextLevel: Long = 100
)

object XPCalculator {
    // Base XP by difficulty
    val baseXP = mapOf(
        GameDifficulty.SIMPLE to 10,
        GameDifficulty.EASY to 20,
        GameDifficulty.MODERATE to 40,
        GameDifficulty.HARD to 70,
        GameDifficulty.CHALLENGE to 100
    )

    // Multipliers
    const val NO_MISTAKES_BONUS = 1.5f
    const val NO_HINTS_BONUS = 1.25f
    const val DAILY_CHALLENGE_BONUS = 2.0f
    const val STREAK_BONUS_PER_DAY = 0.1f  // +10% per streak day, max 100%

    // Level curve: XP needed = 100 * level^1.5
    fun xpForLevel(level: Int): Long = (100 * level.toDouble().pow(1.5)).toLong()
}
```

**Level Titles:**
| Level | Title | Total XP |
|-------|-------|----------|
| 1-5 | Beginner | 0-1,118 |
| 6-10 | Novice | 1,119-3,162 |
| 11-20 | Apprentice | 3,163-8,944 |
| 21-30 | Solver | 8,945-16,432 |
| 31-40 | Expert | 16,433-25,298 |
| 41-50 | Master | 25,299-35,355 |
| 51+ | Grandmaster | 35,356+ |

**Dependencies:** None (but enhances Achievements)

---

### 2.2 Reward Calendar (Login Rewards)

**Description:** Daily login rewards that increase over consecutive days.

**Effort: 4-6 MD**

| Task | Description | MD |
|------|-------------|-----|
| Design | 30-day reward cycle | 0.5 |
| Database | `LoginReward` tracking entity | 0.5 |
| Logic | Reward claim logic, cycle reset | 1 |
| UI - Calendar | Monthly calendar with rewards | 1.5 |
| UI - Claim | Reward claim animation | 1 |
| UI - Dialog | Daily popup showing available reward | 0.5 |
| Integration | Hook into app launch | 0.5 |

**Reward Cycle:**
```kotlin
data class DailyReward(
    val day: Int,           // 1-30
    val rewardType: RewardType,
    val amount: Int
)

enum class RewardType {
    HINTS,          // Extra hints
    XP_BOOST,       // 2x XP for X games
    BADGE           // Cosmetic badge
}

val rewardCycle = listOf(
    DailyReward(1, RewardType.HINTS, 1),
    DailyReward(2, RewardType.XP_BOOST, 1),
    DailyReward(3, RewardType.HINTS, 1),
    DailyReward(4, RewardType.XP_BOOST, 1),
    DailyReward(5, RewardType.HINTS, 2),
    DailyReward(6, RewardType.XP_BOOST, 2),
    DailyReward(7, RewardType.BADGE, 1),  // Weekly badge
    // ... continue to day 30
    DailyReward(30, RewardType.BADGE, 1)  // Monthly badge
)
```

**Dependencies:** XP System (2.1) for XP boost rewards

---

### 2.3 Unlockable Themes/Rewards

**Description:** Themes and customizations unlocked through gameplay progression.

**Effort: 6-8 MD**

| Task | Description | MD |
|------|-------------|-----|
| Design | Define unlockable themes (10-15) | 1 |
| Assets | Create theme color schemes | 2 |
| Database | `UnlockedTheme` entity, unlock conditions | 0.5 |
| Logic | Unlock checking and granting | 1 |
| UI - Theme Picker | Show locked/unlocked status | 1 |
| UI - Unlock | Theme unlock celebration | 0.5 |
| Integration | Check unlocks on level up, achievements | 0.5 |
| Testing | Visual testing of all themes | 0.5-1.5 |

**Unlock Conditions:**
```kotlin
data class ThemeUnlock(
    val themeId: String,
    val unlockType: UnlockType,
    val requirement: Int
)

enum class UnlockType {
    LEVEL,              // Reach level X
    ACHIEVEMENT,        // Unlock specific achievement
    GAMES_COMPLETED,    // Complete X games
    DAILY_STREAK,       // X day daily challenge streak
    PURCHASE,           // In-app purchase
    REWARD_CALENDAR,    // From login rewards
    EVENT               // Limited time event
}

val themeUnlocks = listOf(
    ThemeUnlock("ocean", UnlockType.LEVEL, 5),
    ThemeUnlock("forest", UnlockType.LEVEL, 10),
    ThemeUnlock("sunset", UnlockType.GAMES_COMPLETED, 50),
    ThemeUnlock("midnight", UnlockType.DAILY_STREAK, 7),
    ThemeUnlock("gold", UnlockType.ACHIEVEMENT, "centurion"),
    ThemeUnlock("rainbow", UnlockType.LEVEL, 50)
)
```

**Dependencies:** XP System (2.1), Achievements (1.2)

---

## Phase 3: Challenge Systems

### 3.1 Weekly Challenges

**Description:** Special weekly objectives with bonus rewards.

**Effort: 8-10 MD**

| Task | Description | MD |
|------|-------------|-----|
| Design | Define 20+ weekly challenge types | 1 |
| Database | `WeeklyChallenge`, `WeeklyChallengeProgress` | 0.5 |
| Generator | Weekly challenge selection/rotation | 1 |
| Logic | Progress tracking and completion | 1.5 |
| ViewModel | `WeeklyChallengeViewModel` | 0.5 |
| UI - Card | Weekly challenge display on home | 1 |
| UI - Detail | Challenge detail screen with progress | 1 |
| UI - Complete | Completion rewards screen | 0.5 |
| Integration | Track relevant game events | 1 |
| Testing | Test various challenge types | 0.5-1.5 |

**Weekly Challenge Types:**
```kotlin
sealed class WeeklyChallenge {
    data class CompletionCount(
        val count: Int,
        val difficulty: GameDifficulty? = null,
        val gameType: GameType? = null
    ) : WeeklyChallenge()

    data class SpeedRun(
        val targetTime: Duration,
        val difficulty: GameDifficulty
    ) : WeeklyChallenge()

    data class PerfectGames(
        val count: Int
    ) : WeeklyChallenge()

    data class VarietyChallenge(
        val typesToPlay: List<GameType>
    ) : WeeklyChallenge()

    data class NoHints(
        val count: Int
    ) : WeeklyChallenge()

    data class DailyStreak(
        val days: Int
    ) : WeeklyChallenge()
}

// Example weekly challenges
val weeklyChallenges = listOf(
    WeeklyChallenge.CompletionCount(10),                           // Complete 10 games
    WeeklyChallenge.CompletionCount(5, GameDifficulty.HARD),       // Complete 5 Hard games
    WeeklyChallenge.SpeedRun(Duration.ofMinutes(5), GameDifficulty.EASY),  // Easy < 5min
    WeeklyChallenge.PerfectGames(3),                               // 3 games no mistakes
    WeeklyChallenge.VarietyChallenge(GameType.values().toList()),  // Play all types
    WeeklyChallenge.NoHints(5),                                    // 5 games without hints
    WeeklyChallenge.DailyStreak(5)                                 // 5 daily challenges
)
```

**Dependencies:** Daily Challenges (1.1), XP System (2.1)

---

### 3.2 Mini-Games / Game Variants

**Description:** Alternative game modes for variety.

**Effort: 7-10 MD**

| Task | Description | MD |
|------|-------------|-----|
| Time Attack | Solve as many as possible in 10 min | 2-3 |
| Zen Mode | No timer, relaxing experience | 1-2 |
| Puzzle Rush | Progressively harder puzzles | 2-3 |
| UI - Mode Select | Mini-game selection screen | 1 |
| Integration | Stats tracking for mini-games | 1 |

**Time Attack Mode:**
```kotlin
data class TimeAttackSession(
    val duration: Duration = Duration.ofMinutes(10),
    var puzzlesCompleted: Int = 0,
    var totalMistakes: Int = 0,
    val startTime: ZonedDateTime,
    val puzzles: MutableList<TimeAttackPuzzle> = mutableListOf()
)

// Starts with Simple, increases difficulty every 2 puzzles
// Score = puzzles completed * difficulty multiplier - mistakes
```

**Zen Mode:**
```kotlin
data class ZenModeSettings(
    val showTimer: Boolean = false,
    val showMistakes: Boolean = false,
    val infiniteHints: Boolean = true,
    val relaxingBackground: Boolean = true
)
```

**Puzzle Rush:**
```kotlin
// Start: Simple 6x6
// Each completion: increase difficulty or grid size
// 3 mistakes = game over
// Track: highest level reached, total score
```

**Dependencies:** None

---

## Phase 4: Social & Cloud Features

### 4.1 Leaderboards

**Description:** Competitive rankings for daily challenges and speed runs.

**Effort: 12-15 MD**

| Task | Description | MD |
|------|-------------|-----|
| Backend | Firebase/Supabase leaderboard API | 3-4 |
| Auth | Anonymous or Google sign-in | 2 |
| Database | Local cache of leaderboard data | 1 |
| Sync | Upload scores, fetch rankings | 2 |
| UI - Leaderboard | Leaderboard list screen | 2 |
| UI - Rank | Player rank display | 1 |
| Privacy | Opt-in, anonymous usernames | 0.5 |
| Testing | Load testing, edge cases | 0.5-1.5 |

**Leaderboard Types:**
```kotlin
enum class LeaderboardType {
    DAILY_CHALLENGE_TODAY,      // Today's daily puzzle
    DAILY_CHALLENGE_WEEKLY,     // Best daily times this week
    SPEED_RUN_EASY,             // Fastest Easy completion
    SPEED_RUN_MODERATE,
    SPEED_RUN_HARD,
    SPEED_RUN_CHALLENGE,
    TIME_ATTACK_HIGH_SCORE,     // Most puzzles in 10 min
    PUZZLE_RUSH_HIGH_SCORE,
    WEEKLY_XP                   // Most XP earned this week
}

data class LeaderboardEntry(
    val odIdentity: String,        // Anonymous ID
    val displayName: String,
    val score: Long,
    val rank: Int,
    val timestamp: ZonedDateTime
)
```

**Dependencies:** Daily Challenges (1.1), Mini-Games (3.2), Backend infrastructure

---

### 4.2 Share & Challenge Friends

**Description:** Share puzzles and challenge friends to beat your time.

**Effort: 6-8 MD**

| Task | Description | MD |
|------|-------------|-----|
| Share Image | Generate shareable completion image | 1.5 |
| Deep Links | Create/parse challenge links | 1.5 |
| Challenge Flow | Accept and play shared challenge | 1.5 |
| UI - Share | Share button and sheet | 1 |
| UI - Results | Compare results with challenger | 1 |
| Testing | Deep link testing on various apps | 0.5-1 |

**Share Features:**
```kotlin
data class ShareableChallenge(
    val puzzleSeed: Long,
    val difficulty: GameDifficulty,
    val gameType: GameType,
    val challengerTime: Duration?,
    val challengerName: String?
)

// Deep link format:
// sudokuplus://challenge?seed=123&diff=HARD&type=DEFAULT_9X9&time=300&name=John

// Share message:
// "I completed this Hard Sudoku in 5:00! Can you beat my time? [link]"
```

**Dependencies:** None

---

### 4.3 Cloud Sync

**Description:** Sync progress, achievements, and games across devices.

**Effort: 17-24 MD**

| Task | Description | MD |
|------|-------------|-----|
| Backend Setup | Firebase/Supabase project setup | 1-2 |
| Auth | Google Sign-In integration | 2-3 |
| Sync Engine | Conflict resolution, delta sync | 4-5 |
| Data Models | Cloud data models, serialization | 2 |
| Progress Sync | XP, level, achievements sync | 2 |
| Games Sync | Saved games, history sync | 2-3 |
| Settings Sync | Optional settings sync | 1 |
| UI - Account | Account management screen | 1.5 |
| UI - Sync Status | Sync indicator, manual sync | 1 |
| Testing | Multi-device testing, offline scenarios | 1-2 |

**Sync Strategy:**
```kotlin
data class SyncState(
    val lastSyncTime: ZonedDateTime,
    val pendingChanges: Int,
    val syncStatus: SyncStatus
)

enum class SyncStatus {
    SYNCED,
    SYNCING,
    PENDING,
    OFFLINE,
    ERROR
}

// Conflict resolution:
// - XP/Level: Always take highest
// - Achievements: Union of unlocked
// - Saved games: Keep both, mark with device origin
// - Settings: Last modified wins (or keep local)
```

**Cloud Data Structure:**
```kotlin
data class CloudUserData(
    val odIdentity: String,
    val email: String?,
    val displayName: String,
    val progress: UserProgress,
    val achievements: List<UserAchievement>,
    val dailyChallengeHistory: List<DailyChallenge>,
    val statistics: UserStatistics,
    val lastUpdated: ZonedDateTime
)
```

**Dependencies:** XP System (2.1), Achievements (1.2), Leaderboards (4.1) for auth

---

## Implementation Summary

### Effort by Feature

| # | Feature | Min MD | Max MD | Priority |
|---|---------|--------|--------|----------|
| 1.1 | Daily Challenges | 8 | 10 | P0 |
| 1.2 | Achievements | 10 | 12 | P0 |
| 1.3 | Push Notifications | 3 | 6 | P1 |
| 2.1 | XP & Leveling | 8 | 10 | P0 |
| 2.2 | Reward Calendar | 4 | 6 | P1 |
| 2.3 | Unlockable Themes | 6 | 8 | P2 |
| 3.1 | Weekly Challenges | 8 | 10 | P1 |
| 3.2 | Mini-Games | 7 | 10 | P2 |
| 4.1 | Leaderboards | 12 | 15 | P2 |
| 4.2 | Share & Challenge | 6 | 8 | P2 |
| 4.3 | Cloud Sync | 17 | 24 | P3 |
| **Total** | | **89** | **119** | |

### Recommended Implementation Order

```
Phase 1 (Foundation):
├── 1.1 Daily Challenges (8-10 MD)
├── 1.2 Achievements (10-12 MD)
└── 2.1 XP System (8-10 MD)
    Total: 26-32 MD

Phase 2 (Engagement):
├── 1.3 Push Notifications (3-6 MD)
├── 2.2 Reward Calendar (4-6 MD)
└── 3.1 Weekly Challenges (8-10 MD)
    Total: 15-22 MD

Phase 3 (Content):
├── 2.3 Unlockable Themes (6-8 MD)
└── 3.2 Mini-Games (7-10 MD)
    Total: 13-18 MD

Phase 4 (Social):
├── 4.2 Share & Challenge (6-8 MD)
├── 4.1 Leaderboards (12-15 MD)
└── 4.3 Cloud Sync (17-24 MD)
    Total: 35-47 MD
```

### Dependencies Graph

```
                    ┌─────────────────┐
                    │ Cloud Sync 4.3  │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
    ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
    │Leaderboards │  │ Achievements│  │ XP System   │
    │    4.1      │  │    1.2      │  │    2.1      │
    └──────┬──────┘  └──────┬──────┘  └──────┬──────┘
           │                │                │
           │                │      ┌─────────┴─────────┐
           │                │      │                   │
           ▼                ▼      ▼                   ▼
    ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐
    │   Daily     │  │ Unlockable  │  │ Reward Calendar │
    │ Challenges  │  │   Themes    │  │      2.2        │
    │    1.1      │  │    2.3      │  └─────────────────┘
    └──────┬──────┘  └─────────────┘
           │
           ▼
    ┌─────────────┐     ┌─────────────┐
    │   Weekly    │     │ Mini-Games  │
    │ Challenges  │     │    3.2      │
    │    3.1      │     └─────────────┘
    └─────────────┘
           │
           ▼
    ┌─────────────┐
    │    Push     │
    │Notifications│
    │    1.3      │
    └─────────────┘
```

---

## Technical Considerations

### Database Migrations

New tables required:
- `daily_challenges`
- `achievements`
- `user_achievements`
- `user_progress`
- `login_rewards`
- `weekly_challenges`
- `weekly_challenge_progress`
- `unlocked_themes`
- `time_attack_sessions`
- `leaderboard_cache`

### New Dependencies

```kotlin
// build.gradle.kts additions
implementation("com.google.firebase:firebase-auth-ktx")        // Auth
implementation("com.google.firebase:firebase-firestore-ktx")   // Cloud sync
implementation("com.google.android.gms:play-services-games")   // Leaderboards (optional)
implementation("androidx.work:work-runtime-ktx")               // Notifications
```

### Analytics Events to Track

```kotlin
enum class AnalyticsEvent {
    DAILY_CHALLENGE_STARTED,
    DAILY_CHALLENGE_COMPLETED,
    ACHIEVEMENT_UNLOCKED,
    LEVEL_UP,
    REWARD_CLAIMED,
    WEEKLY_CHALLENGE_COMPLETED,
    MINI_GAME_STARTED,
    MINI_GAME_COMPLETED,
    SHARE_INITIATED,
    CHALLENGE_ACCEPTED,
    LEADERBOARD_VIEWED
}
```

---

## Success Metrics

### KPIs to Track

| Metric | Target | Measurement |
|--------|--------|-------------|
| DAU/MAU Ratio | > 20% | Daily active / Monthly active |
| D1 Retention | > 40% | Users returning day after install |
| D7 Retention | > 20% | Users returning week after install |
| D30 Retention | > 10% | Users returning month after install |
| Avg Session Length | > 10 min | Time spent per session |
| Sessions per Day | > 2 | Average sessions per DAU |
| Daily Challenge Completion | > 30% | DAU completing daily challenge |
| Achievement Unlock Rate | > 50% | Users with 5+ achievements |

---

## Notes

- All MD estimates assume a single experienced Android developer
- Estimates include design, implementation, and basic testing
- Additional time may be needed for:
  - UI/UX design review
  - Extensive QA testing
  - Performance optimization
  - Localization
- Cloud features (4.1, 4.3) require backend infrastructure decisions
- Consider A/B testing for reward values and XP curves
