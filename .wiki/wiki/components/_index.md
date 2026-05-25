---
type: meta
title: Components Index
updated: '2026-05-25'
---
# Components

## Annotation

- [[Annotation Syntax]] — `&begin`/`&end`/`&line`; `.feature-to-file`/`.feature-to-folder`; injection model
- [[Feature Model Language]] — `.feature-model` format, all 4 language registrations, parser classes
- [[Live Templates]] — EFA.xml, ANY + COMMENT contexts
- [[Syntax Highlighting]] — per-language factories/annotators, Darcula + Default color schemes

## UI

- [[Feature Model View]] — tool window ID `Feature Model View`, anchor left, `FeatureViewFactory`
- [[Feature Metrics View]] — tool window ID `Feature Metrics View`, anchor bottom, `MetricsViewFactory`
- [[Traffic Light Indicator]] — `HansTrafficLightActionProvider` (iw.actionProvider)

## Extensibility

- [[Extension Points]] — 5 EPs: metricsService, highlighterService, metricsCallback, featureCallback, featureFileMappingCallback, tanglingMapCallback
