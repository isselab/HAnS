---
type: module
title: Metrics Module
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/metrics
status: mature
related:
  - '[[Feature Location Module]]'
  - '[[Plugin Extensions Module]]'
  - '[[Feature Metrics View]]'
  - '[[HAnS Feature Model]]'
sources:
  - .raw/ProjectMetrics.java
  - .raw/FeatureScattering.java
  - .raw/FeatureTangling.java
  - .raw/NestingDepths.java
  - .raw/MetricsViewFactory.java
---
# Metrics Module

Package: `se.isselab.HAnS.metrics`

## ProjectMetrics — data model

Immutable snapshot constructed from 3 maps. All aggregate fields computed at construction time.

| Field | Type | Description |
|---|---|---|
| `tanglingMap` | `Map<FeatureModelFeature, HashSet<FeatureModelFeature>>` | Per-feature set of features it tangles with |
| `featureFileMappings` | `Map<String, FeatureFileMapping>` | LPQ → file mapping for every feature |
| `nestingDepthMap` | `Map<String, List<Pair<String, Integer>>>` | LPQ → list of (filePath, nestingDepth) |
| `NumberOfFeatures` | `int` | `featureFileMappings.size()` |
| `NumberOfAnnotatedFiles` | `int` | Distinct file paths across all mappings |
| `AvgScatteringDegree` | `double` | Mean of per-feature scattering degrees |
| `AvgLinesOfFeatureCode` | `double` | Mean of per-feature total line counts |
| `AvgNestingDepth` | `double` | Mean over all (feature, file) nesting depth pairs |

## Calculator classes

All three: utility classes (private constructor, static methods only).

### FeatureScattering

`static int getScatteringDegree(FeatureFileMapping)`

**Algorithm:** For each file, collect annotated lines into `TreeSet<Integer>` (sorted, deduped). Walk sorted set — new segment when `line != prevLine + 1`. Sum segments across files.

> [!key-insight] O(N log N) per file. Two adjacent lines = 1 segment.

### FeatureTangling

`static int getFeatureTanglingDegree(Project, FeatureModelFeature)`
`static Map<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap(Project)`

**Algorithm:** Build `filePath → {feature → [FeatureLocationBlock]}`. For each pair (A, B) in same file, if any block of A `hasSharedLines()` with any block of B, record A→B and B→A (symmetric).

**Intersection test:** `!(other.start > this.end || other.end < this.start)`

> [!key-insight] Symmetric and per-line-overlap. Two features in the same file but non-overlapping blocks are NOT tangled.

### NestingDepths

`static List<Pair<String,Integer>> getFeatureNestingDepths(Project, FeatureModelFeature)`
`static Map<String, List<Pair<String,Integer>>> getNestingDepthMap(Map<String,FeatureFileMapping>)`

**Algorithm:** For each (feature, file), count blocks of other features that fully contain each block of this feature (`isInsideOfBlock`: `other.start ≤ this.start AND other.end ≥ this.end`). `depth = 1 + maxContainmentCount`.

**Exposed dimensions:** `avgNestingDepth`, `maxNestingDepth`, `minNestingDepth` (set on `FeatureModelFeature` by `GetProjectMetrics`).

> [!key-insight] Base depth is 1 (not 0).

## MetricsViewFactory — UI wiring

`createToolWindowContent()` calls `triggerService()` immediately.

`triggerService()`: gets `MetricsService` project service → `getProjectMetricsBackground(metrics → refreshTableContent())`.

Dumb mode: deferred via `DumbService.runWhenSmart()`.

`refreshTableContent()`: clears panel, builds new `JBTable` (10 columns, all sortable), wraps in `JBScrollPane`, adds to `BorderLayout.CENTER`. Root features skipped. Title bar has manual "Refresh" button.

## Related

- [[Plugin Extensions Module]] — `ProjectMetricsService` orchestrates calculators
- [[Feature Location Module]] — `FeatureFileMapping` / `FeatureLocationBlock` are calculator inputs
- [[Feature Metrics View]] — UI consumer
