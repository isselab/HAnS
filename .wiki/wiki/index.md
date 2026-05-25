---
type: meta
title: Wiki Index
updated: '2026-05-25'
---
# Wiki Index

## Overview
- [[HAnS Overview]] — JetBrains plugin for feature-oriented software development

## Modules
- [[Build Configuration]] — build.gradle.kts, gradle.properties, libs.versions.toml
- [[HAnS Feature Model]] — complete 52-feature hierarchy
- [[Metrics Module]] — ProjectMetrics, calculators (Scattering, Tangling, NestingDepths)
- [[Feature Location Module]] — FeatureLocation, FeatureLocationBlock, FeatureFileMapping, FeatureLocationManager
- [[Plugin Extensions Module]] — MetricsService, HighlighterService, background task APIs
- [[Code Annotation Module]] — CodeAnnotationInjector, PSI types

## Components
- [[Annotation Syntax]] — `&begin`/`&end`/`&line`; mapping files; injection model
- [[Feature Model Language]] — `.feature-model`, all 4 languages, parser classes
- [[Feature Model View]] — tool window, left anchor, `FeatureViewFactory`
- [[Feature Metrics View]] — tool window, bottom anchor, 7 metrics
- [[Traffic Light Indicator]] — InspectionWidget, 3 states, click/hover behaviour
- [[Extension Points]] — 5 EPs: metricsService, highlighterService, 3 callbacks
- [[Syntax Highlighting]] — 4 languages, Darcula + Default schemes
- [[Live Templates]] — `&begin`/`&end`/`&line` abbreviations, commentStart()/commentEnd()

## Entities
- [[ISSELab]] — research group, Ruhr University Bochum
- [[johmara]] — Johan Martinson, maintainer

## Decisions
- [[ADR-001 Extension Points for Metrics]] — v0.0.4, 2024-03-06
- [[ADR-002 Dual File Extensions]] — v0.0.5, 2024-07-23
- [[ADR-003 Traffic Light Feature]] — v0.1.0, 2025-04-23
- [[ADR-004 Optional LPQ Paths]] — v0.2.0, 2025-10-07
- [[ADR-005 Feature Location Caching]] — v0.2.5, 2025-11-17

## Dependencies
- [[IntelliJ Platform]] — IU 2025.1.6, plugin SDK 2.16.0
- [[Kotlin]] — 2.3.21, JVM 21
- [[Test Dependencies]] — JUnit 4.13.2, opentest4j 1.3.0
- [[Gradle Plugins]] — changelog, qodana, kover

## Flows
- [[Annotation Processing Flow]] — injection → PSI → references → metrics

## Sources
- `.raw/build.gradle.kts` · `.raw/gradle.properties` · `.raw/libs.versions.toml`
- `.raw/README.md` · `.raw/plugin.xml` · `.raw/plugin.feature-model` · `.raw/EFA.xml`
- `.raw/CHANGELOG.md`

## Questions
*(empty)*
