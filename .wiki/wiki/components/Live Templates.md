---
type: component
title: Live Templates
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/annotation
status: mature
related:
  - '[[Annotation Syntax]]'
sources:
  - .raw/EFA.xml
---
# Live Templates

Quick-insert templates for feature annotations. Group: `EFA`.

## Active Templates (context: ANY)

| Abbreviation | Description | Expands to |
|---|---|---|
| `&begin` | EFA block | `<commentStart> &begin[FEATURE]<commentEnd>` + selection + `<commentStart> &end[FEATURE]<commentEnd>` |
| `&end` | Closing EFA | `<commentStart> &end[FEATURE]<commentEnd>` |
| `&line` | EFA line | `<selection> <commentStart> &line[FEATURE]<commentEnd>` |

## Variables

All three templates use:
- `COMMENT` — `commentStart()` (auto-detects language comment style, not editable)
- `FEATURE` — user stop, manually filled in
- `END_COMMENT` — `commentEnd()` (auto-detects, not editable)

`$SELECTION$` preserves selected text when using "Surround with" action.

## Key Behaviour

- `commentStart()` / `commentEnd()` adapt to host language — `//` in Kotlin/Java, `#` in Python, etc.
- `&begin` template wraps selected code — use with "Surround with Feature Annotation" (EditorPopupMenu)
- All three active in **any** file type (context: ANY)

## Disabled Templates

Three variants (`begin`, `end`, `line`) for COMMENT context are present but commented out — they omit comment delimiters (already inside a comment). Currently inactive.

## Related

- [[Annotation Syntax]]
- [[Annotation Processing Flow]]
