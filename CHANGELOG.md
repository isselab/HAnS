# Changelog

## [Unreleased]

## [0.3.0-beta.4] - 2026-05-27

### Fixed 🐛

- **Metrics view no longer shows test-fixture features** — feature-model files under test source roots are now excluded from the project-wide feature scan. Only feature models under production source roots (or the project root) appear in the Metrics view, Feature Model view, and reference resolution.

## [0.3.0-beta.3] - 2026-05-27

### Fixed 🐛

- **No more IDE freezes at startup** — concurrent callers (traffic-light widget, metrics tool window) of the feature-location scan now share a single computation per project instead of racing on `ReferencesSearch`. Resolves the EDT freeze popup observed when the metrics tool window opened alongside the editor traffic-light.

## [0.3.0-beta.2] - 2026-05-26

### Fixed 🐛

- **Feature Model view now expands grouped children** — features nested inside `or`/`xor` groups appear in the tree view again
- **Metrics view populated** — metrics are computed for every feature including those inside `or`/`xor` groups
- **Reduced startup background load** — traffic-light widget no longer schedules duplicate background scans per editor, which previously could stagger the IDE on startup

## [0.3.0-beta.1] - 2026-05-26

### New ✨

- **OR/XOR feature groups** — you can now express variability constraints directly in the feature model:
  - `or` group: one or more children must be selected
  - `xor` group: exactly one child must be selected
- **Optional features** — suffix any feature name with `?` to mark it as optional

  ```
  telematicsSystem
      xor channel
          single
          dual

      extraDisplay ?

      xor size
          small
          large
  ```

### Fixed 🐛

- Feature names that start with `or` or `xor` (e.g. `orange`, `xorfoo`) are now correctly recognized as feature names instead of keywords

## [0.2.5] - 2025-11-17

### Enhanced 🚀

- **Feature Location Performance Improvements** - Added caching for feature file mappings to improve performance when resolving feature locations
- **Code Quality** - Fixed Qodana static analysis issues and improved code organization across multiple modules

### Changed 🔧

- Updated various components for better performance and maintainability

### Housekeeping 🧹

- Added `AGENTS.md` developer guide for AI coding agents with build commands, code style conventions, and contributing guidelines

## [0.2.4] - 2025-11-12

### Enhanced 🚀

- **Feature Model Template Auto-Population** - Feature Model files now automatically populate with the current project name instead of a placeholder when created

### Fixed 🐛

- Fixed Feature Model file template not replacing `${Project_Name}` placeholder with actual project name
- Fixed issue where references where trying to be retrieved before the indexes where built, causing errors on first load

## [0.2.3] - 2025-11-11

### Enhanced 🚀

- **Improved Rider IDE Support** - Better compatibility with JetBrains Rider through explicit language registration for code completion
- **Better Live Template Support** - Live template markers (`&begin`, `&end`, `&line`) now work reliably in comments across all IDEs

### Fixed 🐛

- Improved code completion context detection for more accurate suggestions

## [0.2.2] - 2025-10-09

- ✅ Updated internal code to remove deprecated IntelliJ APIs for future compatibility.
- 🧹 Minor cleanup and documentation updates

## [0.2.1] - 2025-10-08

### Fixed 🐛

- Addressed discrepancies in how the feature model was previously resolved in different contexts.

## [0.2.0] - 2025-10-07

### New ✨

- Optional parent paths (LPQs) in annotations
  - You can now include parent paths even when a feature name is unique. 
  - This gives you the flexibility to add extra context where it improves readability. 
  - Existing projects remain unaffected: LPQs are still only required when duplicate feature names exist.

## [0.1.2] - 2025-08-11

### Housekeeping 🧹

- Bump dependencies to make available on latest IDE builds

## [0.1.1] - 2025-04-23

### Added

- Added Traffic light feature to show when a code file is mapped by file or folder annotation, [feature video](https://youtu.be/HBZYgyc_xgo).

### Housekeeping 🧹

- Removed deprecated methods from metrics service
- Clean up code in regard to the referencing feature
- Updated dependencies

## [0.1.0] - 2025-04-23

### Added

- Added Traffic light feature to show when a code file is mapped by file or folder annotation, [feature video](https://youtu.be/HBZYgyc_xgo).

### Housekeeping 🧹

- Removed deprecated methods from metrics service
- Clean up code in regard to the referencing feature
- Updated dependencies

## [0.0.8] - 2025-01-30

### Housekeeping 🧹

- Updated Gradle distribution to 8.11
- Updated project JDK to 21
- Updated IntelliJ Platform version to 2.2.1
- Updated Dependencies

## [0.0.7] - 2024-08-23

### Housekeeping 🧹

- Removed deprecated usage of ```org.apache.commons.lang3.Range.Between```
- Made sure ```@NotNull``` annotations where correctly placed

### What's Changed

- Bump org.jetbrains.kotlin.jvm from 2.0.10 to 2.0.20 by @dependabot in https://github.com/isselab/HAnS/pull/91

## [0.0.6] - 2024-08-14

## [0.0.5] - 2024-07-23

### Changed

- Feature to folder can be mapped with file extension .feature-folder and .feature-to-folder
- Feature to file can be mapped with file extension .feature-file and .feature-to-file

### Added

- Added a View of the metrics to the project

### Fixed

- Fixed issue [#46](https://github.com/isselab/HAnS/issues/46) where deleting a feature from the feature model view would not correctly delete the feature from the .feature-model file.
- Fixed so that default name for HAnS files is _

## [0.0.4] - 2024-03-06

### Added

- Extension points for metrics.
- A metrics service to calculate the different metrics.
- Supported metrics:
  - LineCount 
  - FeatureFileMapping 
  - Scattering 
  - Tangling
- Badge to the discord community.

### Changed

- Upgrade Gradle version from 8.4 to 8.6.
- Upgrade gradle files from groovy to kotlin. 
- Upgrade action scripts to new versions

### Fixed

- Contributor link to Kuzzi04

## [0.0.3] - 2023-12-21

### Changed

- Upgrade Gradle Wrapper to 8.4
- Gradle - use JetBrains Runtime
- Change since/until build to 223-233.* (2022.3-2023.3.*)
- Dependencies - upgrade org.jetbrains.intellij to 1.16.0
- Dependencies - upgrade org.jetbrains.kotlin.jvm to 1.9.10
- Dependencies (GitHub Actions) - upgrade actions/checkout to 4
- Dependencies (GitHub Actions) - upgrade JetBrains/qodana-action to v2023.2.6

## [0.0.2] - 2023-10-06

### Added

- Contributors.md

### Changed

- Reformat the readme

### Fixed

- QuickFix added to file and folder annotations
- Bug issue tracker setup

## [0.0.1] - 2023-10-03

First Release to marketplace

### Added

- Feature Annotation Languages
- Mapping code fragments to features
- Mapping files or directories to features
- Completion aid when annotating
- Feature Model View
- Referencing
- Renaming features
- Live templates
- Code annotation quickfix
- Syntax highlighter settings

### Changed

- Updated intellij plugin version to ```1.15.0```

[Unreleased]: https://github.com/isselab/HAnS/compare/v0.2.5...HEAD
[0.2.5]: https://github.com/isselab/HAnS/compare/v0.2.4...v0.2.5
[0.2.4]: https://github.com/isselab/HAnS/compare/v0.2.3...v0.2.4
[0.2.3]: https://github.com/isselab/HAnS/compare/v0.2.2...v0.2.3
[0.2.2]: https://github.com/isselab/HAnS/compare/v0.2.1...v0.2.2
[0.2.1]: https://github.com/isselab/HAnS/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/isselab/HAnS/compare/v0.1.2...v0.2.0
[0.1.2]: https://github.com/isselab/HAnS/compare/v0.1.1...v0.1.2
[0.1.1]: https://github.com/isselab/HAnS/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/isselab/HAnS/compare/v0.0.8...v0.1.0
[0.0.8]: https://github.com/isselab/HAnS/compare/v0.0.7...v0.0.8
[0.0.7]: https://github.com/isselab/HAnS/compare/v0.0.6...v0.0.7
[0.0.6]: https://github.com/isselab/HAnS/compare/v0.0.5...v0.0.6
[0.0.5]: https://github.com/isselab/HAnS/compare/v0.0.4...v0.0.5
[0.0.4]: https://github.com/isselab/HAnS/compare/v0.0.3...v0.0.4
[0.0.3]: https://github.com/isselab/HAnS/compare/v0.0.2...v0.0.3
[0.0.2]: https://github.com/isselab/HAnS/compare/v0.0.1...v0.0.2
[0.0.1]: https://github.com/isselab/HAnS/commits/v0.0.1
