---
type: flow
title: Annotation Processing Flow
created: 2026-05-25
updated: 2026-05-25
tags:
  - domain/annotation
  - domain/flow
status: mature
related:
  - "[[Annotation Syntax]]"
  - "[[Feature Model Language]]"
  - "[[Extension Points]]"
  - "[[Code Annotation Module]]"
  - "[[Feature Location Module]]"
sources:
  - .raw/plugin.xml
  - .raw/CodeAnnotationInjector.java
  - .raw/FeatureLocationManager.java
  - .raw/GetProjectMetrics.java
  - .raw/GetFeatureMetricsForFeature.java
---

# Annotation Processing Flow

How HAnS processes `&begin`/`&end`/`&line` annotations in arbitrary source files.

## Injection Model

1. Developer writes `// &begin[Feature]` in any source file (Java, Kotlin, XML, etc.).
2. `CodeAnnotationInjector` (`MultiHostInjector`) is invoked for every `PsiComment`.
3. Filter: skip `PsiDocCommentBase`; skip if text does not match `.*(&begin|&line|&end).*`.
4. Injector strips comment delimiters (e.g. removes `//`) to produce a clean `TextRange`.
5. `registrar.startInjecting(CodeAnnotationLanguage.INSTANCE)` — registers the injection.
6. IntelliJ PSI now contains a `CodeAnnotations` language fragment at that location.
7. `FeatureReferenceContributor` resolves the feature name to the `.feature-model` definition, enabling navigation, find usages, rename, and completion.

## PSI event trigger

> [!key-insight] Metrics are NOT triggered by a `DocumentListener` or `PsiTreeChangeListener` directly on the source file. Instead, background tasks are explicitly enqueued by callers (e.g. UI actions, tool window open). `ProjectMetricsService.getProjectMetricsBackground(callback)` wraps `GetProjectMetrics(...).queue()`.

The background task lifecycle:
1. `GetProjectMetrics.run(indicator)` calls `FeatureLocationManager.getAllFeatureFileMappings(project)`.
2. `FeatureLocationManager` uses `ReferencesSearch.search()` — scoped to `FeatureAnnotationSearchScope` — to find all PSI references to each `FeatureModelFeature`.
3. Search runs inside `DumbService.tryRunReadActionInSmartMode()` to avoid `IndexNotReadyException` during indexing.
4. References are classified by containing file type: `CodeAnnotationFile`, `FileAnnotationFile`, `FolderAnnotationFile`.

## Metrics Calculation Flow

1. Caller enqueues `GetProjectMetrics` (or `GetFeatureMetricsForFeature` for single-feature) via `ProjectMetricsService`.
2. Task calls `FeatureLocationManager.getAllFeatureFileMappings()` → builds `Map<String, FeatureFileMapping>`.
3. `FeatureTangling.getTanglingMap()` → `Map<FeatureModelFeature, HashSet<FeatureModelFeature>>`.
4. `NestingDepths.getNestingDepthMap()` → `Map<String, List<Pair<String, Integer>>>`.
5. Per-feature: `FeatureScattering.getScatteringDegree()`, line count from `FeatureFileMapping`, nesting stats.
6. Results written to mutable fields on each `FeatureModelFeature` PSI element.
7. `ProjectMetrics` snapshot constructed.
8. `onSuccess()` → `callback.onComplete(ProjectMetrics)`.

## FeatureFileMapping construction

Within `FeatureLocationManager.calculateFeatureFileMapping()`:
- Inject-host detection: if `InjectedLanguageManager.getInjectionHost(element)` returns a `PsiComment`, use it; otherwise walk up with `PsiTreeUtil.getContextOfType(element, PsiComment.class)`.
- Marker type resolved from PSI parent: `element.getParent().getParent()` → checked against `CodeAnnotationBeginmarker` / `CodeAnnotationEndmarker` / `CodeAnnotationLinemarker`.
- Line numbers via `PsiDocumentManager.getDocument(file).getLineNumber(element.getTextRange().getStartOffset())`.

## Scope Enlargement

`FeatureAnnotationScopeEnlarger` ensures find-usages searches include annotation files outside the default project source roots (e.g. `.feature-to-file`, `.feature-to-folder` files).

## Related

- [[Code Annotation Module]] — injection details
- [[Feature Location Module]] — `FeatureLocationManager`, `FeatureFileMapping`
- [[Metrics Module]] — calculators
- [[Plugin Extensions Module]] — `ProjectMetricsService`, background tasks
- [[Extension Points]]
- [[Feature Metrics View]]
