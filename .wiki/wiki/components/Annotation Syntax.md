---
type: component
title: Annotation Syntax
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/annotation
  - domain/core
status: mature
related:
  - '[[Feature Model Language]]'
  - '[[Feature Metrics View]]'
  - '[[Annotation Processing Flow]]'
sources:
  - .raw/README.md
  - .raw/plugin.xml
---
# Annotation Syntax

HAnS uses lightweight comment markers and mapping files to annotate code with features.

## Inline Code Markers

Embedded via `CodeAnnotationInjector` (multi-host injector) — works in **any file type**.

| Marker | Usage |
|--------|-------|
| `&begin[FeatureName]` | Start of a feature code block |
| `&end[FeatureName]` | End of a feature code block |
| `&line[FeatureName]` | Single-line feature annotation |

These are parsed as comments by the host language; the injector treats them as `CodeAnnotations` language fragments.

## File/Folder Mapping Files

| File extension | Alias | Language ID |
|----------------|-------|-------------|
| `.feature-to-file` | `.feature-file` | `FileAnnotation` |
| `.feature-to-folder` | `.feature-folder` | `FolderAnnotation` |

Completion contributors (`FileCompletionContributor`) active for both FileAnnotation and FolderAnnotation.

## New File Action

`CreateNewFileAction` ("New EFA File") in the IDE **New** menu — creates mapping files from a template via `EFAFileTemplateManager`.

## Live Templates

`EFA.xml` provides quick-insert templates. Available in `ANY` and `COMMENT` contexts. See [[Live Templates]].

## Surround Action

"Surround with Feature Annotation" in EditorPopupMenu (uses `SurroundWithLiveTemplate` shortcut).

## Related

- [[Feature Model Language]]
- [[Annotation Processing Flow]]
- [[Live Templates]]
