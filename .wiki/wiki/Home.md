# HAnS Wiki

**HAnS** (Helper for Annotation and Structure) is a JetBrains IDE plugin for feature-oriented software development — annotate code to features, visualize models, and measure metrics.

- [GitHub](https://github.com/isselab/HAnS) · [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/22405-hans) · [Overview](overview)

---

## Modules

| Module | Purpose |
|--------|---------|
| [Code Annotation](modules/Code-Annotation-Module) | `&begin`/`&end`/`&line` injection, PSI types |
| [Feature Model](modules/HAnS-Feature-Model) | 52-feature hierarchy, `.feature-model` language |
| [Feature Location](modules/Feature-Location-Module) | Location tracking, caching, file mapping |
| [Metrics](modules/Metrics-Module) | Scattering, Tangling, Nesting Depth calculators |
| [Plugin Extensions](modules/Plugin-Extensions-Module) | MetricsService, HighlighterService, background APIs |
| [Build Configuration](modules/Build-Configuration) | Gradle setup, versions, plugins |

## Components

| Component | Purpose |
|-----------|---------|
| [Annotation Syntax](components/Annotation-Syntax) | `&begin`/`&end`/`&line`, mapping files |
| [Feature Model Language](components/Feature-Model-Language) | 4 languages, parser classes |
| [Feature Model View](components/Feature-Model-View) | Tool window, left anchor |
| [Feature Metrics View](components/Feature-Metrics-View) | Tool window, bottom anchor, 7 metrics |
| [Traffic Light Indicator](components/Traffic-Light-Indicator) | InspectionWidget, 3 states |
| [Extension Points](components/Extension-Points) | 5 EPs: metricsService, highlighterService, callbacks |
| [Syntax Highlighting](components/Syntax-Highlighting) | 4 languages, Darcula + Default |
| [Live Templates](components/Live-Templates) | `&begin`/`&end`/`&line` abbreviations |

## Decisions

| ADR | Decision |
|-----|---------|
| [ADR-001](decisions/ADR-001-Extension-Points-for-Metrics) | Extension Points for Metrics |
| [ADR-002](decisions/ADR-002-Dual-File-Extensions) | Dual File Extensions |
| [ADR-003](decisions/ADR-003-Traffic-Light-Feature) | Traffic Light Feature |
| [ADR-004](decisions/ADR-004-Optional-LPQ-Paths) | Optional LPQ Paths |
| [ADR-005](decisions/ADR-005-Feature-Location-Caching) | Feature Location Caching |

## Dependencies

[IntelliJ Platform](dependencies/IntelliJ-Platform) · [Kotlin](dependencies/Kotlin) · [Gradle Plugins](dependencies/Gradle-Plugins) · [Test Dependencies](dependencies/Test-Dependencies)

## Flows

[Annotation Processing Flow](flows/Annotation-Processing-Flow)
