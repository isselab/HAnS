---
type: module
title: Build Configuration
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/build
status: mature
path: build.gradle.kts + gradle.properties + gradle/libs.versions.toml
language: Kotlin DSL
purpose: 'Defines plugin build, packaging, publishing, and verification'
depends_on:
  - '[[IntelliJ Platform]]'
  - '[[Kotlin]]'
  - '[[Gradle Plugins]]'
sources:
  - .raw/build.gradle.kts
  - .raw/gradle.properties
  - .raw/libs.versions.toml
---
# Build Configuration

## Identity

| Property | Value |
|----------|-------|
| Plugin ID | `se.isselab.hans` |
| Group | `se.isselab` |
| Version | `0.2.6` |
| Plugin name | HAnS |
| Repo URL | https://github.com/isselab/HAnS |

## Structure

Three files form the build config:
- `build.gradle.kts` — build logic (Kotlin DSL)
- `gradle.properties` — all version/platform values; single source of truth
- `gradle/libs.versions.toml` — version catalog for libs and plugins

## Key Build Behaviours

- **Description**: extracted from `README.md` between `<!-- Plugin description -->` markers
- **Change notes**: extracted from `CHANGELOG.md` via Changelog plugin, rendered as HTML
- **Generated sources**: `src/main/gen` — added to main source set and marked as generated
- **Coverage**: Kover XML report generated on every `check` run (feeds Codecov)
- **Gradle**: configuration cache + build cache both enabled

## Publishing

Channel derived from version suffix: `0.2.6` → `default`, `0.2.6-alpha.1` → `alpha`.

Credentials via env vars: `PUBLISH_TOKEN`, `CERTIFICATE_CHAIN`, `PRIVATE_KEY`, `PRIVATE_KEY_PASSWORD`.

## Related

- [[IntelliJ Platform]]
- [[Kotlin]]
- [[Gradle Plugins]]
- [[Test Dependencies]]
