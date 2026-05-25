---
type: component
title: Extension Points
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/extensibility
status: mature
related:
  - '[[Feature Metrics View]]'
sources:
  - .raw/README.md
  - .raw/plugin.xml
---
# Extension Points

HAnS exposes 5 extension points under `se.isselab.hans.*` for other plugins.

## Extension Points

| Name | Interface | Purpose |
|------|-----------|---------|
| `metricsService` | `se.isselab.HAnS.pluginExtensions.MetricsService` | Replace/extend metrics computation |
| `highlighterService` | `se.isselab.HAnS.pluginExtensions.HighlighterService` | Replace/extend annotation highlighting |
| `metricsCallback` | `...backgroundTasks.MetricsCallback` | Hook into metrics calculation events |
| `featureCallback` | `...backgroundTasks.featureTasks.FeatureCallback` | Hook into feature change events |
| `featureFileMappingCallback` | `...backgroundTasks.featureFileMappingTasks.FeatureFileMappingCallback` | Hook into file mapping change events |
| `tanglingMapCallback` | `...backgroundTasks.tanglingMapTasks.TanglingMapCallback` | Hook into tangling map updates |

## Built-in Service Implementations (project-level)

| Service Interface | Implementation |
|------------------|----------------|
| `MetricsService` | `se.isselab.HAnS.pluginExtensions.ProjectMetricsService` |
| `HighlighterService` | `se.isselab.HAnS.pluginExtensions.FeatureHighlighterService` |

These are registered as `<projectService>` — one instance per open project.

## Usage Pattern

External plugins declare `<extensionPoint>` consumers in their own `plugin.xml`, referencing the HAnS extension point by full qualified name `se.isselab.hans.<name>`.

## Related

- [[Feature Metrics View]]
- [[Annotation Processing Flow]]
