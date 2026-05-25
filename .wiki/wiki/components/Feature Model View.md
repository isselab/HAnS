---
type: component
title: Feature Model View
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/ui
status: mature
related:
  - '[[Feature Model Language]]'
  - '[[Feature Metrics View]]'
sources:
  - .raw/README.md
  - .raw/plugin.xml
  - .raw/FeatureViewFactory.java
  - .raw/FeatureViewModel.java
  - .raw/FeatureViewElement.java
---
# Feature Model View

Tool window showing the hierarchical feature structure of the project.

## Registration

| Property | Value |
|----------|-------|
| Tool window ID | `Feature Model View` |
| Anchor | `left` |
| Secondary | `true` |
| Factory class | `se.isselab.HAnS.featureView.FeatureViewFactory` |

## Behaviour

Built on IntelliJ's `StructureViewComponent` framework — wraps `FeatureViewModel(psiFile)`.

- Reads `.feature-model` via `FeatureModelUtil.findFeatureModelAsync()` (async)
- Tree nodes: `FeatureViewElement` wrapping `FeatureModelFeature` PSI nodes
- Children resolved via `PsiTreeUtil.getChildrenOfTypeAsList(element, FeatureModelFeature.class)` — direct children only
- Sorted alphabetically (`Sorter.ALPHA_SORTER`) — no manual sort config needed
- Nodes are navigable — clicking a feature navigates to its definition in `.feature-model`

## Update trigger

**Automatic** — `StructureViewComponent` registers its own PSI change listeners internally. No custom file watcher. Tree re-renders whenever the `.feature-model` PSI changes.

## No Feature Model state

If no `.feature-model` found: shows fallback panel with:
- Label: "No feature-model could be found"
- Button: "Create feature-model" — opens input dialog (validates regex `([A-Z]+|[a-z]+|[0-9]+|_+|'+)+`), creates `.feature-model` in project root with entered root feature name, reloads tool window

## Context menu (StructureViewPopupMenu)

Rename, Add, Delete feature actions (see [[Feature Model Language]]).

## Related

- [[Feature Model Language]]
- [[Feature Metrics View]]
- [[Traffic Light Indicator]]
