---
type: meta
title: Operation Log
---
# Operation Log

## [2026-05-25] ingest | FeatureViewFactory + FeatureViewModel + FeatureViewElement + HansTrafficLightDashboardModel + HansTrafficLightPopup + MetricsViewFactory
- Sources: `.raw/FeatureViewFactory.java`, `.raw/FeatureViewModel.java`, `.raw/FeatureViewElement.java`, `.raw/HansTrafficLightDashboardModel.java`, `.raw/HansTrafficLightPopup.java`, `.raw/MetricsViewFactory.java`
- Pages updated: [[Feature Model View]], [[Traffic Light Indicator]], [[Feature Metrics View]], [[Metrics Module]]
- Gaps resolved: all 3 remaining gaps closed
- Key insight: FeatureView uses IntelliJ StructureView — auto-updates via PSI listeners, no custom watcher; MetricsView has 10 columns (not 7), root features excluded; findings = total feature-to-file/folder assignments; popup min-width 296px, right-aligned below widget

## [2026-05-25] ingest | CHANGELOG.md
- Pages created: [[CHANGELOG]], [[ADR-001]] through [[ADR-005]]
- Key insight: 18 releases; breaking change v0.1.1; LPQ optional since v0.2.0

## [2026-05-25] ingest | src/
- Pages created: [[Metrics Module]], [[Feature Location Module]], [[Plugin Extensions Module]], [[Code Annotation Module]]
- Pages updated: [[Traffic Light Indicator]], [[Annotation Processing Flow]], [[Feature Metrics View]]
- Key insight: metrics not event-driven; scattering uses TreeSet; injection strips TextRange per comment token type

## [2026-05-25] ingest | .feature-model + EFA.xml
- Pages updated: [[HAnS Feature Model]] (complete), [[Live Templates]], [[Feature Metrics View]] (7 metrics)

## [2026-05-25] ingest | plugin.xml
- Pages created: [[Syntax Highlighting]], [[Live Templates]], [[Annotation Processing Flow]], [[HAnS Feature Model]]

## [2026-05-25] ingest | README.md
- Pages created: [[Annotation Syntax]], [[Feature Model Language]], [[Feature Model View]], [[Feature Metrics View]], [[Traffic Light Indicator]], [[Extension Points]], [[ISSELab]], [[johmara]]

## [2026-05-25] ingest | build.gradle.kts + gradle.properties + libs.versions.toml
- Pages created: [[Build Configuration]], [[IntelliJ Platform]], [[Kotlin]], [[Test Dependencies]], [[Gradle Plugins]]

## [2026-05-25] scaffold | HAnS vault initialized
- Mode: B (GitHub/Repository)
