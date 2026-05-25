---
type: dependency
title: Test Dependencies
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - dependency/test
  - domain/build
status: mature
risk: low
purpose: Unit and integration testing
managed_by: libs.versions.toml
sources:
  - .raw/libs.versions.toml
---
# Test Dependencies

## Libraries

| Library | Version | Purpose |
|---------|---------|---------|
| `junit:junit` | 4.13.2 | Unit test framework |
| `org.opentest4j:opentest4j` | 1.3.0 | Open test assertions (JUnit 5 foundation) |
| IntelliJ Platform test framework | via platform | Platform-level test support (`TestFrameworkType.Platform`) |

## Notes

- Both declared as `testImplementation`
- JUnit 4 (not 5) — classic test runner
- `opentest4j` underpins JUnit 5 assertions but used here standalone

## Related

- [[Build Configuration]]
- [[IntelliJ Platform]]
