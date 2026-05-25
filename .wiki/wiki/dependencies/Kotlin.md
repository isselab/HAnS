---
type: dependency
title: Kotlin
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - dependency/language
  - domain/build
status: mature
version: 2.3.21
risk: low
purpose: Primary implementation language
managed_by: libs.versions.toml
sources:
  - .raw/libs.versions.toml
---
# Kotlin

Primary language for HAnS. All source code is Kotlin.

## Version

- Kotlin JVM plugin: `2.3.21` (`org.jetbrains.kotlin.jvm`)
- JVM toolchain: `21`
- Kotlin stdlib: not bundled (`kotlin.stdlib.default.dependency = false`) — IntelliJ Platform provides it

## Notes

- JVM target: 21 (`kotlin { jvmToolchain(21) }`)
- stdlib exclusion avoids conflict with stdlib bundled in IntelliJ Platform

## Related

- [[IntelliJ Platform]]
- [[Build Configuration]]
