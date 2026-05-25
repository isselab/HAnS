---
type: component
title: Syntax Highlighting
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/ui
  - domain/annotation
status: mature
related:
  - '[[Feature Model Language]]'
  - '[[Annotation Syntax]]'
sources:
  - .raw/plugin.xml
---
# Syntax Highlighting

Custom syntax highlighting for all 4 HAnS languages.

## Per-Language Factories + Annotators

| Language | Factory class | Annotator class |
|----------|---------------|-----------------|
| `FeatureModel` | `FeatureModelSyntaxHighlighterFactory` | `FeatureModelAnnotator` |
| `FileAnnotation` | `FileAnnotationSyntaxHighlighterFactory` | `FileAnnotationAnnotator` |
| `FolderAnnotation` | `FolderAnnotationSyntaxHighlighterFactory` | `FolderAnnotationAnnotator` |
| `CodeAnnotations` | `CodeAnnotationsSyntaxHighlighterFactory` | `CodeAnnotationAnnotator` |

All under `se.isselab.HAnS.syntaxHighlighting.*`.

## Color Schemes

| Scheme | File |
|--------|------|
| Darcula | `colorSchemes/HAnSDarcula.xml` |
| Default (light) | `colorSchemes/HAnSDefault.xml` |

User-configurable via `HansColorSettingsPage` (Settings > Editor > Color Scheme > HAnS).

## Related

- [[Feature Model Language]]
- [[Annotation Syntax]]
