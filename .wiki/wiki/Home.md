# HAnS Wiki

**HAnS** (Helper for Annotation and Structure) is a JetBrains IDE plugin for feature-oriented software development — annotate code to features, visualize models, and measure metrics.

- [GitHub](https://github.com/isselab/HAnS) · [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/22405-hans) · [Overview](overview)

---

## Modules

| Module | Purpose |
|--------|---------|
| [Code Annotation](Code-Annotation-Module) | `&begin`/`&end`/`&line` injection, PSI types |
| [Feature Model](HAnS-Feature-Model) | 52-feature hierarchy, `.feature-model` language |
| [Feature Location](Feature-Location-Module) | Location tracking, caching, file mapping |
| [Metrics](Metrics-Module) | Scattering, Tangling, Nesting Depth calculators |
| [Plugin Extensions](Plugin-Extensions-Module) | MetricsService, HighlighterService, background APIs |
| [Build Configuration](Build-Configuration) | Gradle setup, versions, plugins |

## Components

| Component | Purpose |
|-----------|---------|
| [Annotation Syntax](Annotation-Syntax) | `&begin`/`&end`/`&line`, mapping files |
| [Feature Model Language](Feature-Model-Language) | 4 languages, parser classes |
| [Feature Model View](Feature-Model-View) | Tool window, left anchor |
| [Feature Metrics View](Feature-Metrics-View) | Tool window, bottom anchor, 7 metrics |
| [Traffic Light Indicator](Traffic-Light-Indicator) | InspectionWidget, 3 states |
| [Extension Points](Extension-Points) | 5 EPs: metricsService, highlighterService, callbacks |
| [Syntax Highlighting](Syntax-Highlighting) | 4 languages, Darcula + Default |
| [Live Templates](Live-Templates) | `&begin`/`&end`/`&line` abbreviations |

## Decisions

| ADR | Decision |
|-----|---------|
| [ADR-001](ADR-001-Extension-Points-for-Metrics) | Extension Points for Metrics |
| [ADR-002](ADR-002-Dual-File-Extensions) | Dual File Extensions |
| [ADR-003](ADR-003-Traffic-Light-Feature) | Traffic Light Feature |
| [ADR-004](ADR-004-Optional-LPQ-Paths) | Optional LPQ Paths |
| [ADR-005](ADR-005-Feature-Location-Caching) | Feature Location Caching |

## Dependencies

[IntelliJ Platform](IntelliJ-Platform) · [Kotlin](Kotlin) · [Gradle Plugins](Gradle-Plugins) · [Test Dependencies](Test-Dependencies)

## Flows

[Annotation Processing Flow](Annotation-Processing-Flow)
