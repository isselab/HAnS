---
type: module
title: HAnS Feature Model
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/annotation
  - domain/meta
status: mature
path: .feature-model (project root)
language: FeatureModel
purpose: Documents HAnS own feature decomposition
sources:
  - .raw/plugin.feature-model
---
# HAnS Feature Model

Full feature hierarchy of HAnS itself. The plugin annotates its own source — self-demonstrating.

## Complete Hierarchy

```
HAnS
├── FeatureModel
│   ├── Language
│   └── File
├── FolderAnnotation
│   ├── Language
│   └── File
├── FileAnnotation
│   ├── Language
│   └── File
├── CodeAnnotation
│   ├── Language
│   ├── File
│   └── Injection
│       └── JavaStyleComment
├── CodeCompletion
│   ├── FeatureNameProvider
│   ├── FileNameProvider
│   └── LiveTemplate
├── SyntaxHighlighting
│   ├── FeatureModel
│   ├── FolderAnnotation
│   ├── FileAnnotation
│   ├── CodeAnnotation
│   └── ColorSettingsPage
├── Referencing
│   └── FeatureLocation
├── Metrics
│   ├── LineCount
│   ├── FeatureFileMapping
│   ├── Scattering
│   ├── Tangling
│   ├── NestingDepths
│   ├── NumberOfAnnotatedFiles
│   ├── NumberOfFeatures
│   └── MetricsView
├── ExtensionPoint
│   ├── Callback
│   └── Service
├── TrafficLight
│   ├── WidgetLocation
│   ├── WidgetStyle
│   ├── SearchFeatures
│   ├── ClickAndHover
│   └── HoverPopupStyle
├── FileHighlighter
├── FeatureView
├── FileTemplate
│   └── NewFile
├── Quickfix
└── SettingsPage
```

## Notes

- `CodeAnnotation::Injection::JavaStyleComment` — injection specifically targets Java-style comment patterns
- `Metrics` has 7 metrics, 3 not in README: **NestingDepths**, **NumberOfAnnotatedFiles**, **NumberOfFeatures**
- `TrafficLight` has 5 sub-features — richer than documented: WidgetLocation, WidgetStyle, SearchFeatures, ClickAndHover, HoverPopupStyle
- `SettingsPage` leaf matches the TODO/disabled entry in plugin.xml
- `FileHighlighter` separate from `ExtensionPoint::Service` (HighlighterService) — likely the internal editor highlighter

## Related

- [[Feature Model Language]]
- [[Annotation Syntax]]
- [[Feature Metrics View]]
- [[Traffic Light Indicator]]
- [[Extension Points]]
