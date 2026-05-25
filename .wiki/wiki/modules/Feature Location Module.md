---
type: module
title: Feature Location Module
created: 2026-05-25
updated: 2026-05-25
tags:
  - domain/metrics
  - domain/annotation
status: mature
related:
  - "[[Metrics Module]]"
  - "[[Plugin Extensions Module]]"
  - "[[Code Annotation Module]]"
  - "[[Annotation Syntax]]"
sources:
  - .raw/FeatureLocation.java
  - .raw/FeatureLocationBlock.java
  - .raw/FeatureFileMapping.java
  - .raw/FeatureLocationManager.java
---

# Feature Location Module

Package: `se.isselab.HAnS.featureLocation`

## Data model

### FeatureLocationBlock

Smallest unit — a contiguous range of lines.

| Field | Type | Notes |
|---|---|---|
| `start` | `int` | 0-based line number |
| `end` | `int` | 0-based line number (inclusive) |

Key methods:
- `getLineCount()` → `max(end - start + 1, 0)`
- `hasSharedLines(FeatureLocationBlock)` → `!(other.start > end || other.end < start)`
- `hasSharedLines(FeatureLocationBlock[])` → any overlap in array
- `countTimesInsideOfBlocks(Collection<FeatureLocationBlock>)` → count of blocks that fully contain this block
- `isInsideOfBlock(FeatureLocationBlock)` → `other.start <= start && other.end >= end`

### FeatureLocation

Holds all blocks for one (feature, file) pair.

| Field | Type | Notes |
|---|---|---|
| `mappedPath` | `String` | Absolute path to the annotated file |
| `mappedBy` | `String` | Path of the annotation file (`.feature-to-file`, etc.) |
| `mappedFeature` | `FeatureModelFeature` | PSI feature reference |
| `annotationType` | `FeatureFileMapping.AnnotationType` | `CODE`, `FILE`, or `FOLDER` |
| `featureLocations` | `List<FeatureLocationBlock>` | All blocks in this file |

`getMappedPathPairMappedBy()` returns a `FileAnnotationKey(mappedPath, mappedBy)`.

### FeatureFileMapping

Immutable, thread-safe container for all locations of one feature across the whole project.

**Key types nested inside:**

| Type | Kind | Purpose |
|---|---|---|
| `MarkerType` | enum | `BEGIN`, `END`, `LINE`, `NONE` |
| `AnnotationType` | enum | `FOLDER`, `FILE`, `CODE` |
| `FileAnnotationKey` | record | `(filePath, originatingAnnotationPath)` — map key |
| `AnnotationData` | record (private) | `(AnnotationType, List<FeatureLocationBlock>)` |
| `MarkerLine` | record (private) | `(MarkerType, lineNumber)` — used during build |
| `MarkerDataBuilder` | record (public) | Mutable builder used by `FeatureLocationManager` |
| `FeatureMappingInfo` | record | `(featureLpq, annotationType, originPath)` |

**Factory pattern:** `FeatureFileMapping.create(SmartPsiElementPointer<FeatureModelFeature>, Map<FileAnnotationKey, MarkerDataBuilder>)`

Build algorithm in `create()`:
1. Sort markers per file by line number.
2. Stack-based BEGIN/END matching: push on BEGIN, pop+create block on END.
3. LINE → single-line block `(line, line)`.
4. NONE (file/folder) → block `(0, lastLine)`.
5. Warn on unmatched BEGIN or orphan END markers.

**Core methods:**

| Method | Notes |
|---|---|
| `getFeatureLocations()` | All locations; requires ReadAction |
| `getFeatureLocationsForFile(FileAnnotationKey)` | Single file; requires ReadAction |
| `getMappedFilePaths()` | Distinct file paths (thread-safe) |
| `getMappedPathPair()` | All `FileAnnotationKey`s (thread-safe) |
| `getFeatureLineCountInFile(FileAnnotationKey)` | Uses `BitSet` for overlap deduplication |
| `getTotalFeatureLineCount()` | Sum across all files |
| `getFolderAnnotations()` | Grouped by originating `.feature-to-folder` |
| `getFileAnnotations()` | Grouped by originating `.feature-to-file` |

> [!key-insight] `SmartPsiElementPointer` is used to hold the feature reference, preventing PSI memory leaks when the tree is rebuilt.

## FeatureLocationManager

Static utility class. Thread-safe via `ConcurrentMap` caches keyed by `Project` (weak references).

**Cache structure:**
- `PROJECT_CACHES`: `Project → ConcurrentMap<featureLPQ, FeatureFileMapping>`
- `INITIALIZED_FEATURES`: `Project → Set<featureLPQ>`
- `FULLY_INITIALIZED`: `Project → Boolean`

**Public API:**

| Method | Behaviour |
|---|---|
| `getAllFeatureFileMappings(Project)` | Lazy full init; returns unmodifiable map |
| `getFeatureFileMapping(Project, FeatureModelFeature)` | Lazy per-feature init; checks cache first |
| `calculateAllFeatureFileMappings(Project)` | Forces full recalculation; batch ReadAction |
| `calculateFeatureFileMapping(Project, FeatureModelFeature)` | Single-feature recalculation |
| `invalidateCache(Project)` | Clears all caches for project |
| `invalidateFeature(Project, String featureLpq)` | Clears single feature entry |

**Reference search:** uses `ReferencesSearch.search()` scoped to `FeatureAnnotationSearchScope` to find all PSI references to a `FeatureModelFeature`. Run inside `DumbService.tryRunReadActionInSmartMode()` to avoid `IndexNotReadyException`.

**Reference processing dispatch:**
- `CodeAnnotationFile` → `processCodeFile()` — resolves injection host, determines `MarkerType` from PSI parent chain
- `FileAnnotationFile` → `processFeatureToFile()` — resolves file references from `.feature-to-file`
- `FolderAnnotationFile` → `processFeatureToFolder()` — iterative (non-recursive) directory traversal

> [!key-insight] Folder traversal uses a `Deque`-based iterative approach to prevent `StackOverflowError` on deep trees.

## Related

- [[Code Annotation Module]] — produces the `CodeAnnotationFile` PSI nodes consumed here
- [[Metrics Module]] — calculators consume `FeatureFileMapping` and `FeatureLocationBlock`
- [[Plugin Extensions Module]] — `ProjectMetricsService` wraps these calls
