# Changelog

All notable changes to Sudoku Plus will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.2-rc01] - 2025-12-21

### Added
- **XP & Leveling System**: Earn experience points by completing puzzles and level up from Beginner to Grandmaster
- **Daily Reward Calendar**: Claim daily rewards including bonus hints, XP boosts, and badges
- **Badge Collection**: Earn badges for weekly streaks and completing reward cycles
- **Accessibility Labels**: Added screen reader support for toolbar, navigation bar, and picker controls

### Changed
- **Backup UX**: Improved error handling with clear failure messages and retry option
- **Timer**: Better lifecycle handling and smarter autosave (only saves when board changes)
- **Deep Links**: Restored file import functionality from external apps
- Migrated to Compose Destinations v2
- Cleaned up Gradle configuration and dependencies

### Fixed
- Theme flash on app startup with Android 12+ splash screen
- Database migration for folder index
- Timer not pausing correctly in some cases
- Removed blocking calls from main thread for smoother UI

### Technical
- Updated AboutLibraries and ComposeMarkdown dependencies
- Removed deprecated Accompanist libraries

## [1.0.1-rc02] - 2025-12-19

### Added
- **Achievements System**: 36 achievements to unlock across various gameplay milestones
- **Push Notifications**: Daily challenge reminders and streak alerts
  - Configurable notification times in settings
  - Notifications enabled by default (opt-out available)
  - Permission request dialog on Android 13+

### Changed
- Refactored settings architecture into dedicated managers for better maintainability

### Translations
- Added notification strings to all 29 supported languages

## [1.0.0] - 2025-12-18

### Added
- Initial release with new versioning
- Dev/prod build flavors
- Automated release workflow
- Daily Challenge mode with streak tracking
- Complete sudoku puzzle generator (6x6, 9x9, 12x12)
- Killer Sudoku variant support
- Custom sudoku import/export
- Folder organization for puzzles
- Statistics and records tracking
- Multiple difficulty levels
- Advanced hint system
- Note-taking mode
- Customizable themes and appearance
- 29 language translations
