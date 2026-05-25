---
type: decision
title: ADR-005 Feature Location Caching
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/performance
status: active
date: '2025-11-17'
owner: isselab
context: v0.2.5
related:
  - '[[Feature Location Module]]'
  - '[[Annotation Processing Flow]]'
---
# ADR-005: Feature Location Caching

**Date:** 2025-11-17 (v0.2.5)  
**Status:** active

## Decision

Add caching for feature file mappings in `FeatureLocationManager` to avoid redundant PSI traversals on repeated lookups.

## Context

Performance issues observed when resolving feature locations in larger projects — `FeatureLocationManager` was re-traversing PSI on every call. v0.2.5 introduced a cache layer.

## Consequences

- Repeated feature location lookups now served from cache
- Cache invalidation strategy: unclear from changelog — see [[Feature Location Module]] for implementation details
- Improves metrics calculation speed in large projects

## Related

- [[Feature Location Module]]
- [[Annotation Processing Flow]]
- [[Metrics Module]]
