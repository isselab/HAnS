---
type: dependency
title: Gradle Plugins
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - dependency/build
  - domain/build
status: mature
risk: low
purpose: Build tooling
managed_by: libs.versions.toml
sources:
  - .raw/libs.versions.toml
---
# Gradle Plugins

## Plugin Versions

| Plugin | ID | Version |
|--------|-----|---------|
| Kotlin JVM | `org.jetbrains.kotlin.jvm` | 2.3.21 |
| IntelliJ Platform | `org.jetbrains.intellij.platform` | 2.16.0 |
| Changelog | `org.jetbrains.changelog` | 2.5.0 |
| Qodana | `org.jetbrains.qodana` | 2026.1.0 |
| Kover (coverage) | `org.jetbrains.kotlinx.kover` | 0.9.8 |

## Purposes

- **intellij.platform**: IntelliJ plugin SDK, packaging, publishing, verification
- **changelog**: Parses `CHANGELOG.md`, injects change notes into plugin manifest
- **qodana**: Static analysis / code quality CI
- **kover**: Kotlin code coverage; configured to output XML on every check (for Codecov)

## Gradle Version

`9.0.0` (from `gradle.properties`). Configuration cache and build cache both enabled.

## Related

- [[Build Configuration]]
- [[IntelliJ Platform]]
- [[Kotlin]]
