---
type: component
title: Feature Model Language
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/annotation
  - domain/core
status: developing
related:
  - '[[Annotation Syntax]]'
  - '[[Feature Model View]]'
  - '[[Syntax Highlighting]]'
sources:
  - .raw/README.md
  - .raw/plugin.xml
---
# Feature Model Language

Custom PSI language for defining hierarchical feature models.

## File Type

| Property | Value |
|----------|-------|
| Extension | `.feature-model` |
| Language ID | `FeatureModel` |
| FileType class | `se.isselab.HAnS.featureModel.FeatureModelFileType` |
| Parser class | `se.isselab.HAnS.featureModel.FeatureModelParserDefinition` |

## Related Languages (Annotation Files)

| Extension | Language ID | FileType class |
|-----------|-------------|----------------|
| `.feature-to-file` (alias: `.feature-file`) | `FileAnnotation` | `FileAnnotationFileType` |
| `.feature-to-folder` (alias: `.feature-folder`) | `FolderAnnotation` | `FolderAnnotationFileType` |
| `.code-annotation` | `CodeAnnotations` | `CodeAnnotationFileType` |

## IDE Support

- Syntax highlighting — `FeatureModelSyntaxHighlighterFactory` + `FeatureModelAnnotator`
- Find usages — `FeatureFindUsagesProvider`
- References — `FeatureReferenceContributor` (cross-language, language="")
- File references — `FileReferenceContributor`
- Rename — `FeatureNameInputValidator` + `RenameAction` in StructureViewPopupMenu
- Scope enlarger — `FeatureAnnotationScopeEnlarger` (ensures annotations outside src/ are found)

## Actions (StructureViewPopupMenu)

| Action ID | Class | Description |
|-----------|-------|-------------|
| `RenameFeature` | `RenameAction` | Rename feature across all annotations |
| `AddFeature` | `AddAction` | Add feature to model |
| `DeleteFeature` | `DeleteAction` | Delete feature from model |

## Related

- [[Annotation Syntax]]
- [[Feature Model View]]
- [[Syntax Highlighting]]
- [[Referencing Flow]]
