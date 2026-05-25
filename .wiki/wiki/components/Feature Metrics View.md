---
type: component
title: Feature Metrics View
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/metrics
  - domain/ui
status: mature
related:
  - '[[Annotation Syntax]]'
  - '[[Extension Points]]'
  - '[[HAnS Feature Model]]'
  - '[[Metrics Module]]'
  - '[[Plugin Extensions Module]]'
sources:
  - .raw/README.md
  - .raw/plugin.xml
  - .raw/plugin.feature-model
  - .raw/FeatureScattering.java
  - .raw/FeatureTangling.java
  - .raw/NestingDepths.java
  - .raw/ProjectMetrics.java
  - .raw/MetricsViewFactory.java
---
# Feature Metrics View

Bottom panel showing calculated metrics per feature.

## Registration

| Property | Value |
|----------|-------|
| Tool window ID | `Feature Metrics View` |
| Anchor | `bottom` |
| Factory class | `se.isselab.HAnS.metrics.view.MetricsViewFactory` |

## Table columns (10)

| # | Column | Type |
|---|--------|------|
| 0 | Feature (LPQ text) | String |
| 1 | Scattering Degree | Integer |
| 2 | Tangling Degree | Integer |
| 3 | Lines of Feature-code | Integer |
| 4 | Avg Nesting Depth | Double |
| 5 | Max Nesting Depth | Integer |
| 6 | Min Nesting Depth | Integer |
| 7 | Annotated Files | Integer |
| 8 | Folder Annotations | Integer |
| 9 | File Annotations | Integer |

All columns sortable. Root features excluded (`service.isRootFeature(feature)`).

## Refresh trigger

1. On tool window creation — `triggerService()` called immediately
2. Manual — "Refresh Metrics" button in title bar (`AllIcons.Actions.Refresh`)
3. If IDE is indexing (dumb mode): deferred via `DumbService.runWhenSmart()`

Refresh clears the panel, creates new `JBTable` wrapped in `JBScrollPane`, adds to `BorderLayout.CENTER`, calls `revalidate()` + `repaint()`.

## Metrics (7 — 3 undocumented in README)

| Metric | Formula |
|--------|---------|
| Line Count | Sum of `(endLine - startLine + 1)` per `FeatureLocationBlock`, deduplicated with `BitSet` |
| Feature-to-File mappings | Distinct `FileAnnotationKey` entries in `FeatureFileMapping.locationMap` |
| Scattering | Contiguous line-segments across all files (TreeSet adjacency walk) |
| Tangling | Count of features with overlapping blocks (`hasSharedLines`) |
| Nesting depths (avg/max/min) | `1 + maxContainmentCount` per file; reported as three columns |
| Annotated Files | `getNumberOfAnnotatedFiles()` |
| Folder/File Annotations | `getNumberOfFolderAnnotations()` / `getNumberOfFileAnnotations()` |

> [!key-insight] README mentions 4 metrics; table has 10 columns covering 7 distinct metrics (nesting split into avg/max/min).

## Related

- [[Metrics Module]]
- [[Plugin Extensions Module]]
- [[Extension Points]]
- [[HAnS Feature Model]]
