# Changelog

## [Unreleased]

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

[Unreleased]: https://github.com/isselab/HAnS/compare/v0.2.1...HEAD
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
