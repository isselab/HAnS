# Changelog

## [Unreleased]
### Housekeeping 🧹
- Removed deprecated usage of ```org.apache.commons.lang3.Range.Between```
- Made sure ```@NotNull``` annotations where correctly placed

## [0.0.6] - 2024-08-14
### Housekeeping 🧹

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

[Unreleased]: https://github.com/isselab/HAnS/compare/v0.0.6...HEAD
[0.0.6]: https://github.com/isselab/HAnS/compare/v0.0.5...v0.0.6
[0.0.5]: https://github.com/isselab/HAnS/compare/v0.0.4...v0.0.5
[0.0.4]: https://github.com/isselab/HAnS/compare/v0.0.3...v0.0.4
[0.0.3]: https://github.com/isselab/HAnS/compare/v0.0.2...v0.0.3
[0.0.2]: https://github.com/isselab/HAnS/compare/v0.0.1...v0.0.2
[0.0.1]: https://github.com/isselab/HAnS/commits/v0.0.1
