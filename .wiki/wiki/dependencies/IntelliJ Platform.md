---
type: dependency
title: IntelliJ Platform
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - dependency/platform
  - domain/build
status: mature
version: 2025.1.6
risk: low
purpose: Host platform for the plugin
managed_by: gradle.properties
sources:
  - .raw/gradle.properties
---
# IntelliJ Platform

Host platform for the HAnS plugin. HAnS runs inside all JetBrains IDEs.

## Version

- Platform type: `IU` (IntelliJ IDEA Ultimate)
- Platform version: `2025.1.6`
- Since build: `251` (2025.1 minimum)
- Gradle IntelliJ Plugin: `2.16.0` (`org.jetbrains.intellij.platform`)

## Bundled Plugins/Modules

None declared in current config — `platformBundledPlugins` and `platformBundledModules` are empty. No external Marketplace plugin dependencies either.

## Testing

Uses `TestFrameworkType.Platform` — IntelliJ Platform test framework.

UI test runner registered as `runIdeForUiTests` on port `8082`.

## Publishing

- Channel: derived from version suffix (e.g. `2.1.7-alpha` → `alpha` channel; no suffix → `default`)
- Token: `PUBLISH_TOKEN` env var
- Signing: `CERTIFICATE_CHAIN`, `PRIVATE_KEY`, `PRIVATE_KEY_PASSWORD` env vars

## Plugin Verification

Runs against `recommended()` IDEs plus the declared platform version.

## Related

- [[Build Configuration]]
- [[Kotlin]]
