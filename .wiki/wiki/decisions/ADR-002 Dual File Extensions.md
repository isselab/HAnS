---
type: decision
title: ADR-002 Dual File Extensions
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/annotation
status: active
date: '2024-07-23'
owner: isselab
context: v0.0.5
related:
  - '[[Annotation Syntax]]'
  - '[[Feature Model Language]]'
---
# ADR-002: Dual File Extensions

**Date:** 2024-07-23 (v0.0.5)  
**Status:** active

## Decision

Support two file extensions for both mapping file types:
- `.feature-to-folder` and `.feature-folder` (both valid)
- `.feature-to-file` and `.feature-file` (both valid)

## Context

Users were using both forms in practice. Standardising on one would have broken existing projects. Both extensions now register to the same language/parser.

## Consequences

- Both extensions work identically — same parser, same completion, same highlighting
- `feature-file` / `feature-folder` are shorter aliases; `feature-to-file` / `feature-to-folder` are more descriptive
- Registered in `plugin.xml` as comma-separated `extensions` attribute on `<fileType>`

## Related

- [[Annotation Syntax]]
- [[Feature Model Language]]
