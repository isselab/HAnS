---
type: overview
title: HAnS Overview
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/plugin
  - type/overview
status: seed
related: []
sources: []
---
# HAnS Overview

HAnS (Helping Annotate Software) is a JetBrains IDE plugin for feature-oriented software development. It enables developers to annotate, track, and analyze features throughout a codebase.

## Purpose

Provide tooling for feature location, mapping, and metrics in software systems. HAnS itself is annotated with features — a self-demonstrating example of its own capabilities.

## Key Capabilities

- Feature model definition (`.feature-model` format — hierarchical)
- Code annotation: `&begin[Feature]`, `&end[Feature]`, `&line[Feature]`
- File/folder mapping: `.feature-to-file`, `.feature-to-folder`
- Feature metrics: line count, scattering degree, tangling degree, file-to-feature mappings
- IDE integration: tool windows, context-aware completion, syntax highlighting, refactoring (rename, find usages)

## Tech Stack

- Language: Kotlin
- Build: Gradle (`build.gradle.kts`)
- Platform: IntelliJ Platform (min build 2025.1 / 251)
- Java: 21+
- License: Apache 2.0
- Published: JetBrains Marketplace (plugin ID 22759)

## Repo

- GitHub: isselab/HAnS
- Branch: main

## Key Modules

See [[Modules Index]] for full breakdown.
