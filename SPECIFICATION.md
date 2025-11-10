# HAnS Technical Specification

## Overview

HAnS (Helping Annotate Software) is an IntelliJ IDEA plugin that helps developers annotate software assets with features. This specification documents the core architecture, components, and expected behaviors of the system.

## Architecture Overview

The plugin is structured around several key subsystems:

1. **Feature Model System** - Defines and manages feature hierarchies
2. **Feature Annotation System** - Three types: code, file, and folder annotations
3. **Feature Location System** - Tracks where features are implemented
4. **Metrics System** - Calculates scattering, tangling, and nesting metrics
5. **Referencing System** - Provides find usages, rename, and navigation
6. **Plugin Services** - Background tasks and service initialization

## Core Components

### 1. Feature Model System

**Package:** `se.isselab.HAnS.featureModel`

The feature model defines a hierarchical structure of features using a custom DSL.

**Key Classes:**
- `FeatureModelFile` - PSI file representation
- `FeatureModelFeature` - PSI element representing a feature
- `FeatureModelParser` - Grammar-based parser
- `FeatureModelUtil` - Utility methods for finding features
- `FeatureModelElementFactory` - Factory for creating PSI elements

**Expected Behaviors:**
- Features are defined in `.feature-model` files
- Features can be hierarchical (parent-child relationships)
- Feature names must be unique within a project
- LPQ (Longest Prefix Qualifier) text uniquely identifies features

### 2. Feature Annotation System

**Package:** `se.isselab.HAnS.featureAnnotation`

Three types of annotations map code/files/folders to features:

#### 2.1 Code Annotations
**Package:** `se.isselab.HAnS.featureAnnotation.codeAnnotation`

**Key Classes:**
- `CodeAnnotationInjector` - Injects annotation language into host languages
- `CodeAnnotationParser` - Parses annotation markers
- `CodeAnnotationPsiImplUtil` - PSI utility methods

**Annotation Syntax:**
```
// &begin[FeatureName]
code...
// &end[FeatureName]
// &line[FeatureName]
```

**Expected Behaviors:**
- Begin/end markers must be balanced
- Line markers annotate a single line
- Supports LPQ feature references (e.g., `Parent::Child`)

#### 2.2 File Annotations
**Package:** `se.isselab.HAnS.featureAnnotation.fileAnnotation`

Maps entire files to features using `.feature-file` or `.feature-to-file` files.

**Expected Behaviors:**
- `.feature-file` - Contains feature name and file references
- `.feature-to-file` - Maps features to multiple files
- Supports relative file paths

#### 2.3 Folder Annotations
**Package:** `se.isselab.HAnS.featureAnnotation.folderAnnotation`

Maps directories to features using `.feature-folder` or `.feature-to-folder` files.

**Expected Behaviors:**
- Maps entire directory trees to features
- Supports glob patterns for selective mapping

### 3. Feature Location System

**Package:** `se.isselab.HAnS.featureLocation`

Tracks and manages where features are implemented in the codebase.

**Key Classes:**
- `FeatureLocationBlock` - Represents a contiguous block of lines (start, end)
- `FeatureFileMapping` - Maps a feature to all its locations across files
- `FeatureLocation` - Locations of a feature in a specific file
- `FeatureLocationManager` - Manages all feature-to-location mappings

**Data Model:**
```
FeatureFileMapping
  └─ Map<Pair<FilePath, Origin>, Pair<AnnotationType, List<FeatureLocationBlock>>>
```

**Expected Behaviors:**
- `FeatureLocationBlock` has package-private constructor (created via FeatureFileMapping)
- `enqueue()` collects markers before building
- `buildFromQueue()` processes markers and creates blocks
- Handles unmatched begin/end markers gracefully with error messages
- LINE markers create single-line blocks
- NONE markers (file annotations) create blocks from 0 to file length

### 4. Metrics System

**Package:** `se.isselab.HAnS.metrics`

Calculates feature-oriented metrics for software analysis.

**Key Classes:**
- `FeatureScattering` - Measures feature dispersion across files
- `FeatureTangling` - Detects overlapping features
- `NestingDepths` - Calculates nesting levels
- `ProjectMetrics` - Aggregates all metrics

**Metrics Definitions:**

**Scattering Degree:**
- Number of contiguous segments for a feature
- Higher = more scattered implementation
- Algorithm: Count non-overlapping blocks after sorting by line number

**Tangling Degree:**
- Number of other features sharing lines with a given feature
- Algorithm: Check for line overlaps between FeatureLocationBlocks

**Nesting Depth:**
- Maximum nesting level of feature annotations
- Algorithm: Count how many features contain a given block

**Expected Behaviors:**
- Metrics accept FeatureFileMapping as input
- Overloaded methods exist for project-wide vs. single-feature calculations
- Calculations are cached in services for performance

### 5. Referencing System

**Package:** `se.isselab.HAnS.referencing`

Provides IDE features like find usages, rename, and go-to-definition.

**Key Classes:**
- `FeatureReference` - Resolves feature references to definitions
- `FeatureReferenceUtil` - Helper methods for reference handling
- `FeatureFindUsagesProvider` - Find usages integration
- `FeatureRefactoringSupportProvider` - Rename refactoring

**Expected Behaviors:**
- References resolve to FeatureModelFeature PSI elements
- Rename updates all annotation references (LPQ-aware)
- Find usages searches all three annotation types

### 6. Plugin Services

**Package:** `se.isselab.HAnS.pluginExtensions`

**Key Services:**
- `ProjectMetricsService` - Caches and provides metrics
- `FeatureHighlighterService` - Manages feature highlighting
- `MetricsService` - Background metric calculations

**Background Tasks:**
- `GetProjectMetrics` - Calculates all project metrics
- `GetFeatureFileMappingForFeature` - Builds mappings for a feature
- `GetScatteringDegreeForFeature` - Calculates scattering
- `GetTanglingDegreeForFeature` - Calculates tangling

**Expected Behaviors:**
- Services are initialized on project open
- Background tasks run asynchronously
- Callbacks handle results when ready
- Uses ReadAction for PSI access

## Edge Cases and Invariants

### Invariants
1. Feature names are unique within a project
2. FeatureLocationBlock start <= end always
3. Line numbers are 0-based internally
4. All PSI access must be in ReadAction

### Edge Cases
1. **Unmatched markers:** Begin without end - error message, metrics may be inaccurate
2. **Empty blocks:** Start == end - line count is 1
3. **File annotations with no files:** Valid but contributes 0 to metrics
4. **Duplicate annotations:** Same feature annotated twice to same asset - warning message
5. **Missing feature in model:** References resolve to null, quick-fix available

## Testing Strategy

See [TEST_PLAN.md](TEST_PLAN.md) for comprehensive testing strategy.

**Test Coverage Priorities:**
1. Feature location block operations (high)
2. Metrics calculation accuracy (high)
3. Parser correctness for all annotation types (medium)
4. Reference resolution and rename (medium)
5. Service initialization and background tasks (low)

## API Contracts

### FeatureLocationBlock
```java
// Package-private constructor - create via FeatureFileMapping
int getLineCount()                           // Returns end - start + 1 (min 0)
boolean hasSharedLines(FeatureLocationBlock) // True if blocks overlap
boolean isInsideOfBlock(FeatureLocationBlock) // True if completely contained
int countTimesInsideOfBlocks(Collection)     // Count containing blocks
```

### FeatureFileMapping
```java
void enqueue(path, line, MarkerType, AnnotationType, origin) // Cache a marker
void buildFromQueue()                                         // Process cached markers
List<FeatureLocation> getFeatureLocations()                   // All locations
FeatureLocation getFeatureLocationsForFile(Pair)             // Locations in file
int getTotalFeatureLineCount()                               // Sum of all lines
Set<String> getMappedFilePaths()                             // All mapped files
```

### FeatureScattering
```java
static int getScatteringDegree(FeatureFileMapping)           // Count segments
static HashMap<FeatureModelFeature, Integer> getScattering(Project, HashMap<String, FeatureFileMapping>)
```

### FeatureTangling
```java
static HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap(Project, HashMap)
static int getTanglingDegree(FeatureModelFeature, HashMap)   // Count tangled features
```

## Performance Considerations

1. **ReferencesSearch** is expensive - cache results in services
2. **Global searches** - provide overloads accepting pre-computed mappings
3. **Background tasks** - use ProgressIndicator for long operations
4. **PSI access** - always wrap in ReadAction

## Future Enhancements

1. Support for variability-aware analysis
2. Integration with version control for feature evolution tracking
3. Machine learning for feature location recommendation
4. Cross-project feature comparison

---

*This specification is based on the HAnS v1.x codebase and should be updated as the system evolves.*
