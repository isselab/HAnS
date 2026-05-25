---
type: decision
title: ADR-003 Traffic Light Feature
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - domain/ui
status: active
date: '2025-04-23'
owner: isselab
context: v0.1.0
related:
  - '[[Traffic Light Indicator]]'
---
# ADR-003: Traffic Light Feature

**Date:** 2025-04-23 (v0.1.0)  
**Status:** active

## Decision

Add a visual indicator in the editor to show whether the current file is mapped via file or folder annotation.

## Context

Users needed a quick way to see at a glance whether a file was feature-mapped without opening the feature model or metrics view. The traffic light widget was implemented as an `iw.actionProvider` (InspectionWidget) in the main editor gutter.

## Consequences

- Widget shows icon-only / count / "DEAD" states (see [[Traffic Light Indicator]])
- Click toggles Feature Model View tool window
- Hover shows popup with feature mapping details
- Implemented: `HansTrafficLightActionProvider`, `HansTrafficLightWidget`, `HansTrafficLightPanel`, `HansTrafficLightPopup`, `HansTrafficLightDashboardModel`
- Demo video: https://youtu.be/HBZYgyc_xgo

## Related

- [[Traffic Light Indicator]]
