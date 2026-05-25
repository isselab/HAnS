---
type: decision
title: ADR-004 Optional LPQ Paths
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/annotation
  - domain/language
status: active
date: '2025-10-07'
owner: isselab
context: v0.2.0
related:
  - '[[Annotation Syntax]]'
  - '[[Feature Model Language]]'
---
# ADR-004: Optional LPQ Parent Paths in Annotations

**Date:** 2025-10-07 (v0.2.0)  
**Status:** active

## Decision

Allow optional Locally-qualified Path (LPQ) parent paths in all annotations even when the feature name is unique.

## Context

Previously, LPQs (e.g. `HAnS::FeatureModel`) were only required when duplicate feature names existed in the model — needed to disambiguate. Users wanted to add context/readability via parent paths even for unique names.

## Consequences

- `&begin[HAnS::FeatureModel]` now valid even if `FeatureModel` is unique
- `&begin[FeatureModel]` still works — LPQ is optional
- Existing projects unaffected (no breaking change)
- Grammar updated to treat LPQ as optional rather than conditional on name uniqueness
- `Lpq` PSI node now appears in all 4 language grammars as optional

## Related

- [[Annotation Syntax]]
- [[Feature Model Language]]
- [[Code Annotation Module]]
