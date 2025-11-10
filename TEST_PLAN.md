Test Plan for HAnS IntelliJ Plugin

Overview

This document outlines a practical, maintainable test strategy for the HAnS (Helping Annotate Software) IntelliJ plugin. It summarizes current coverage, identifies gaps, and provides concrete test case recommendations and priorities. The goal is to ensure correctness of parsing, mapping, metrics, referencing, and background behavior while keeping tests fast and reliable using the IntelliJ Platform Test Framework.

Test Strategy Overview

- Test Pyramid:
  - Unit tests: fast, deterministic tests for pure logic and small components (FeatureLocationBlock, FeatureFileMapping queue processing, metrics calculators countSegments/isInsideOfBlock)
  - Integration tests: tests that use the IntelliJ test fixture (BasePlatformTestCase / ParsingTestCase) to exercise PSI-based code (parsers, element factories, FeatureLocationManager, FeatureTangling using project features)
  - End-to-end UI / acceptance tests: small set of heavy tests that exercise background tasks and service initialisation; these should run in CI job configured for UI tests and be limited in number.

- Principles:
  - Prefer BasePlatformTestCase for tests that require a project and PSI.
  - Use ParsingTestCase for grammar/lexer/parser tests when test-data based golden parsing verification is needed.
  - Keep unit tests pure and independent of the IntelliJ platform where possible; when PSI is required use the fixture.
  - Avoid expensive global searches in unit tests by constructing controlled FeatureFileMapping maps and using the fileMapping overloads on metrics calculators.
  - Make tests deterministic and avoid reliance on real file-system state: create files with myFixture.configureByText or element factories.

Existing Test Coverage (analysis summary)

Present tests in src/test/java cover:
- Feature model parsing: FeatureModelParsingTest (ParsingTestCase uses test resources)
- File and folder annotation parsing: ParsingTestCase-based tests with test resources
- Code completion / live template contexts: multiple BasePlatformTestCase tests verifying completion providers and AnyContext behavior

Coverage gaps (priority):
- Metrics calculators: FeatureScattering, FeatureTangling, NestingDepths currently have no direct unit tests.
- Feature location mapping logic: FeatureFileMapping enqueue/buildFromQueue logic and FeatureLocationBlock operations.
- Code annotation PSI/element utilities: no direct unit tests verifying CodeAnnotation element factory and psi util behavior.
- Referencing and resolve/rename logic: FeatureReference resolution and handleElementRename need tests.
- Service initialization and background task wrappers: ProjectMetricsService and backgroundable tasks have no unit tests covering their basic behavior.
- Integration tests for FeatureLocationManager: heavy as it uses ReferencesSearch; add light-weight mapping tests using constructed FeatureFileMapping inputs.

Recommended New Test Cases (by priority)

1. Feature location and block logic (high)
   - FeatureLocationBlock: hasSharedLines (single & array), isInsideOfBlock, countTimesInsideOfBlocks, getLineCount, toString.
   - FeatureFileMapping: enqueue with different MarkerTypes (BEGIN, END, LINE, NONE); buildFromQueue should create the correct blocks; validate getFeatureLineCountInFile and getTotalFeatureLineCount; file/folder/file-annotations group retrieval.

2. Metrics calculation (high)
   - FeatureScattering: scattering for contiguous and fragmented blocks; special cases (empty blocks).
   - FeatureTangling: overlapping blocks across features should result in tangling mapping; use project feature files + constructed FeatureFileMappings to test getTanglingMap overload.
   - NestingDepths: nested blocks across features in same file should increase nesting depth accordingly.

3. Code annotation parsing & PSI utilities (medium)
   - Use CodeAnnotationElementFactory to create LPQ elements and ensure recognizable PSI types exist (CodeAnnotationLpq, CodeAnnotationFeature).
   - Test CodeAnnotationPsiImplUtil.rename behavior via CodeAnnotationElementFactory-created elements.

4. Referencing and find usages (medium)
   - FeatureReference.resolve returns correct FeatureModelFeature when feature exists in project.
   - FeatureReference.getVariants returns feature LPQs for completion variants.
   - handleElementRename should use FeatureReferenceUtil to compute LPQ renames when appropriate (simulate by creating a rename scenario).

5. Service initialization and background tasks (low/medium)
   - ProjectMetricsService wrappers call underlying metric calculators correctly (e.g., getFeatureScattering returns same value as FeatureScattering for a provided FeatureFileMapping).
   - Background tasks: ensure GetScatteringDegreeForFeature and GetTanglingDegreeForFeature can run without throwing when given small projects (mocked by in-test setup).

Unit Test Recommendations

- Small focused tests (FeatureLocationBlock, countSegments behavior) that don't require the IDE fixture.
- Use FeatureFileMapping.enqueue/buildFromQueue in tests to validate block construction and error-handling paths (unmatched begin/end).
- Use the metrics calculators' overloads that accept precomputed file mappings to avoid expensive ReferencesSearch calls in tests.

Integration Test Recommendations

- Use BasePlatformTestCase to create feature model files with myFixture.configureByText to populate the project with features used by FeatureTangling and FeatureReference tests.
- Construct FeatureFileMapping instances and place them in a HashMap keyed by ReadAction.compute(feature::getLPQText) so metric calculators can use them.
- For parser tests of code-annotation, either add parsing test data under src/test/resources and a ParsingTestCase, or use element factories to create PSI elements and assert structure.

UI Test Recommendations (IntelliJ plugin specific)

- Keep UI tests limited and stable. Use the existing GitHub Actions job run-ui-tests.yml to run heavy UI tests separately.
- Test scenarios:
  - Full end-to-end: create feature-model and annotation files, run the background metrics task, and assert the metrics view/table shows expected values (can be an integration test with fixture + dedicated UI test job).
  - Feature rename refactor: trigger rename on a FeatureModelFeature and assert referencing annotation LPQs are updated (requires testing refactoring pipeline and is higher effort).

New Test Files Added

- src/test/java/se/isselab/HAnS/featureLocationTests/FeatureLocationBlockTest.java
- src/test/java/se/isselab/HAnS/featureLocationTests/FeatureFileMappingTest.java
- src/test/java/se/isselab/HAnS/metricsTests/FeatureScatteringTest.java
- src/test/java/se/isselab/HAnS/metricsTests/FeatureTanglingTest.java
- src/test/java/se/isselab/HAnS/codeAnnotationTests/CodeAnnotationPsiTest.java
- src/test/java/se/isselab/HAnS/referencingTests/FeatureReferenceTest.java
- src/test/java/se/isselab/HAnS/pluginExtensionsTests/ProjectMetricsServiceTest.java

Remaining Key Areas Needing Coverage

- Full test coverage for FeatureLocationManager.getFeatureFileMapping and getAllFeatureFileMappings (requires controlling ReferencesSearch; plan to add in integration tests with small fixture projects or by refactoring code to allow injection/mocking of search results).
- End-to-end background tasks that use heavy searches (ReferencesSearch) and UI components (Metrics view); these should be added as separate UI/integration tests in the run-ui-tests workflow.
- More extensive tests for rename/refactoring behavior using FeatureReference.handleElementRename in real refactor flows.
- Parser golden tests for code annotations (add test resources under src/test/resources and ParsingTestCase-based tests to validate full grammar).

Notes and Trade-offs

- Many classes interact with IntelliJ PSI and Project APIs; unit tests should avoid triggering heavy global searches by using metrics overloads that accept prepared mappings.
- Some behaviors (ReferencesSearch) are inherently expensive; test them in isolated integration tests and/or use mocking strategies (if the code is refactored to allow injection of searcher) to keep unit tests fast.

If you want I can:
- Add parsing test data for code-annotation and a ParsingTestCase to validate the parser.
- Add more integration tests for FeatureLocationManager that exercise ReferencesSearch by creating inline references and PSI elements (this will be more involved).

