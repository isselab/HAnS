---
type: component
title: Traffic Light Indicator
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/ui
status: mature
related:
  - '[[Annotation Syntax]]'
  - '[[Feature Model Language]]'
  - '[[Feature Metrics View]]'
sources:
  - .raw/README.md
  - .raw/plugin.xml
  - .raw/HansTrafficLightWidget.java
  - .raw/HansTrafficLightActionProvider.java
  - .raw/HansTrafficLightDashboardModel.java
  - .raw/HansTrafficLightPopup.java
---
# Traffic Light Indicator

Visual widget embedded in the editor inspection area (top-right of each editor tab).

## Registration

`HansTrafficLightActionProvider` implements `InspectionWidgetActionProvider`. Only appears in `MAIN_EDITOR` — not in diffs, console, or embedded editors.

## Widget states (from `refresh()`)

| Condition | Icon | Text label |
|---|---|---|
| `!model.isAlive()` | `AnnotationIcons.PluginIcon` | `"DEAD"` |
| `model.hasFindings()` | `AnnotationIcons.PluginIcon` | findings count as string |
| Default (alive, no findings) | `AnnotationIcons.PluginIcon` | `null` (icon only) |

> [!key-insight] "DEAD" = dashboard model disposed (project closing). No colour change — state communicated via text label only.

## Findings definition (`HansTrafficLightDashboardModel`)

`featureCount` = sum of all `Set<String>` sizes across both FILE and FOLDER annotation type maps.

Structure of input: `Map<annotationType, Map<featureName, Set<filePaths>>>` — so `featureCount` = total individual feature-to-file/folder assignments for the current file.

`hasFindings()` = `featureCount != 0`.

`isAlive` is set externally by the caller — `false` when project is being closed/disposed.

## Interaction

**Click:** toggles `hans.toolwindow.feature-model-view` tool window visibility.

**Hover:** shows `HansTrafficLightPopup` (scheduleShow with `ide.tooltip.initialReshowDelay`).

**Hover exit:** scheduleHide with `ide.tooltip.initialDelay.highlighter` — popup stays open if mouse moves inside it.

## Popup (`HansTrafficLightPopup`)

- Content: `HansTrafficLightPanel` (dashboard)
- Position: `(editor.width - 10 - popupWidth, widgetBottom + 5)` — right-aligned below the widget
- Min width: 296px (scaled)
- Dismisses on: click outside, editor scroll/move (AncestorListener), mouse exit from panel bounds

## Visual feedback (paintComponent)

| Mouse state | Background |
|---|---|
| Pressed | `JBUI.CurrentTheme.ActionButton.pressedBackground()` |
| Hover | `JBUI.CurrentTheme.ActionButton.hoverBackground()` |
| Normal | Transparent |

Font scaled down 2pt on non-Windows. Foreground follows `ActionButton.iconTextForeground` from editor colour scheme.

## Lifecycle

`MouseListener` registered/unregistered in `addNotify()`/`removeNotify()`. Disposed with editor via `EditorUtil.disposeWithEditor()`.

## Related

- [[Annotation Syntax]]
- [[Feature Model Language]]
- [[Feature Metrics View]]
