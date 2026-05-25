---
type: module
title: Plugin Extensions Module
created: 2026-05-25
updated: 2026-05-25
tags:
  - domain/metrics
  - domain/ui
status: mature
related:
  - "[[Metrics Module]]"
  - "[[Feature Location Module]]"
  - "[[Extension Points]]"
  - "[[Feature Metrics View]]"
sources:
  - .raw/MetricsService.java
  - .raw/ProjectMetricsService.java
  - .raw/HighlighterService.java
  - .raw/FeatureHighlighterService.java
  - .raw/GetProjectMetrics.java
  - .raw/GetFeatureMetricsForFeature.java
---

# Plugin Extensions Module

Package: `se.isselab.HAnS.pluginExtensions`

## MetricsService (interface)

The primary API surface for external plugins. All methods fall into regions tagged in the source:

### Feature model navigation

| Method | Return | Notes |
|---|---|---|
| `getFeatures()` | `List<FeatureModelFeature>` | All registered features |
| `getChildFeatures(feature)` | `List<FeatureModelFeature>` | Direct children in model tree |
| `getParentFeature(feature)` | `FeatureModelFeature` | Returns file root if at top level |
| `isRootFeature(feature)` | `boolean` | True if parent is `FeatureModelFile` |
| `getRootFeature(feature)` | `FeatureModelFeature` | Walks up until parent is `FeatureModelFile` |
| `getRootFeatures()` | `List<FeatureModelFeature>` | All top-level features via sibling traversal |

### FeatureFileMapping (sync + async)

| Method | Notes |
|---|---|
| `getFeatureFileMappingOfFeature(map, feature)` | Lookup by `feature.getLPQText()` |
| `isFeatureInFeatureModel(map, feature)` | Map containment check |
| `getFeatureFileMappingBackground(feature, callback)` | Spawns `GetFeatureFileMappingForFeature` |
| `getAllFeatureFileMappingsBackground(callback)` | Spawns `GetFeatureFileMappings` |

### LineCount (sync)

| Method | Notes |
|---|---|
| `getTotalFeatureLineCount(mapping)` | Delegates to `FeatureFileMapping.getTotalFeatureLineCount()` |
| `getFeatureLineCountInFile(mapping, location)` | Uses `location.getMappedPathPairMappedBy()` as key |

### Tangling (sync + async)

| Method | Notes |
|---|---|
| `getTanglingMapOfFeature(map, feature)` | Direct `HashMap.get()` |
| `getFeatureTanglings(feature, callback)` | Spawns `GetTangledFeaturesForFeature` |
| `getFeatureTanglingDegreeBackground(feature, callback)` | Spawns `GetTanglingDegreeForFeature` |
| `getTanglingMapBackground(callback)` | Spawns `GetTanglingMap` |

### Scattering (sync + async)

| Method | Notes |
|---|---|
| `getFeatureScattering(mapping)` | Calls `FeatureScattering.getScatteringDegree(mapping)` directly |
| `getFeatureScatteringBackground(feature, callback)` | Spawns `GetScatteringDegreeForFeature` |

### NestingDepths (async only)

| Method | Notes |
|---|---|
| `getNestingDepthsBackGround(feature, callback)` | Spawns `GetNestingDepthsForFeature` |

### NumberOfAnnotatedFiles (async only)

| Method | Notes |
|---|---|
| `getNumberOfAnnotatedFilesBackground(feature, callback)` | Spawns `GetNumberOfAnnotationsForFeature` |

### FeatureLocation (sync)

| Method | Notes |
|---|---|
| `getFeatureLocations(mapping)` | Delegates to `FeatureFileMapping.getFeatureLocations()` |
| `getListOfFeatureLocationBlock(location)` | Delegates to `FeatureLocation.getFeatureLocations()` |

### Metrics aggregate (async)

| Method | Notes |
|---|---|
| `getProjectMetricsBackground(callback)` | Spawns `GetProjectMetrics` (all features) |
| `getFeatureMetricsBackground(callback, feature)` | Spawns `GetFeatureMetricsForFeature` |

## ProjectMetricsService (implementation)

`@Service(Service.Level.PROJECT)` — one instance per project, accessed via `project.getService(ProjectMetricsService.class)`.

All async methods follow the pattern:
```java
new BackgroundTask(project, "title", callback, [optional data]).queue();
```

`getRootFeatures()` traverses sibling PSI links (prev/next) from `FeatureModelUtil.findFeatures().getFirst()`.

## HighlighterService (interface)

| Method | Notes |
|---|---|
| `highlightFeatureInFeatureModel(String lpq)` | Opens feature model and scrolls to feature by LPQ |
| `highlighFeatureInFeatureModel(FeatureModelFeature)` | Same, but from PSI element |
| `openFileInProject(String path)` | Opens file in editor |
| `openFileInProject(String path, int start, int end)` | Opens file and highlights line range |

## FeatureHighlighterService (implementation)

`@Service(Service.Level.PROJECT)` — thin delegate to static `FileHighlighter` methods.

## Background task classes

### GetProjectMetrics (`Task.Backgroundable`)

Runs in background thread:
1. `FeatureLocationManager.getAllFeatureFileMappings(project)` — full location scan
2. `FeatureTangling.getTanglingMap(project, mappings)` — tangling
3. `NestingDepths.getNestingDepthMap(mappings)` — nesting

Then iterates every `FeatureFileMapping`, computes per-feature metrics and sets them on the `FeatureModelFeature`:
- `scatteringDegree`, `tanglingDegree`, `lineCount`
- `avgNestingDepth`, `maxNestingDepth`, `minNestingDepth`
- `numberOfAnnotatedFiles`, `numberOfFolderAnnotations`, `numberOfFileAnnotations`

Constructs `ProjectMetrics` snapshot. On `onSuccess()` → `callback.onComplete(metrics)`.

Progress indicator: fraction 0 → 1 over feature count.

### GetFeatureMetricsForFeature (`Task.Backgroundable`)

Sequentially runs four sub-tasks inline (reusing same `ProgressIndicator`):
1. `GetScatteringDegreeForFeature` (0% → 25%)
2. `GetTanglingDegreeForFeature` (25% → 50%)
3. `GetNestingDepthsForFeature` (50% → 75%)
4. `GetNumberOfAnnotationsForFeature` (75% → 100%)

On `onSuccess()` → `callback.onComplete(feature)`.

> [!key-insight] `GetFeatureMetricsForFeature` calls `.run(indicator)` directly on sub-tasks rather than `.queue()` — they share the same background thread and progress indicator.

## Callback interfaces

| Interface | `onComplete` signature |
|---|---|
| `MetricsCallback` | `(ProjectMetrics)` |
| `FeatureCallback` | `(FeatureModelFeature)` |
| `FeatureFileMappingCallback` | `(HashMap<String, FeatureFileMapping>)` |
| `TanglingMapCallback` | `(HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>>)` |

## Related

- [[Metrics Module]] — calculator implementations
- [[Feature Location Module]] — data model consumed by all tasks
- [[Extension Points]] — how external plugins register callbacks
