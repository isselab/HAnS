---
type: decision
title: ADR-001 Extension Points for Metrics
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/extensibility
  - domain/metrics
status: active
date: '2024-03-06'
owner: isselab
context: v0.0.4
related:
  - '[[Extension Points]]'
  - '[[Feature Metrics View]]'
---
# ADR-001: Extension Points for Metrics

**Date:** 2024-03-06 (v0.0.4)  
**Status:** active

## Decision

Expose metrics computation via IntelliJ extension points (`metricsService`, `metricsCallback`) rather than keeping it internal.

## Context

v0.0.4 introduced the metrics service and the first 4 metrics (LineCount, FeatureFileMapping, Scattering, Tangling). Rather than hard-coding the metric pipeline, the team chose to expose it publicly so other plugins could consume or extend metrics.

## Consequences

- External plugins can implement `MetricsService` to replace computation
- External plugins can register `MetricsCallback` / `FeatureCallback` / `FeatureFileMappingCallback` / `TanglingMapCallback` to react to metric updates
- Deprecated methods removed in v0.1.1 — breaking change for early adopters
- See [[Extension Points]] for current API

## Related

- [[Extension Points]]
- [[Feature Metrics View]]
- [[Plugin Extensions Module]]
