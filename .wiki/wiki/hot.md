---
type: meta
title: Hot Cache
updated: '2026-05-25T00:00:00'
---
# Recent Context

## Last Updated
2026-05-25 — All gaps resolved. Ingested FeatureViewFactory, FeatureViewModel, FeatureViewElement, HansTrafficLightDashboardModel, HansTrafficLightPopup, MetricsViewFactory

## Key Recent Facts
- Feature Model View uses IntelliJ StructureViewComponent — PSI-driven auto-update, no custom watcher
- No .feature-model found → fallback panel with "Create feature-model" button (validates regex, creates file in project root)
- Traffic light findings = sum of all Set sizes across FILE + FOLDER annotation maps for current file
- `isAlive` set externally — false when project closing/disposed
- Popup: right-aligned below widget, min 296px wide, dismissed on click-outside or editor scroll
- MetricsView has 10 columns (Feature, Scattering, Tangling, Lines, AvgND, MaxND, MinND, AnnotatedFiles, FolderAnnotations, FileAnnotations)
- Root features excluded from metrics table (`service.isRootFeature(feature)`)
- Manual refresh button in title bar; deferred on dumb mode via DumbService.runWhenSmart()

## Recent Changes
- Updated (gap-resolved): [[Feature Model View]], [[Traffic Light Indicator]], [[Feature Metrics View]], [[Metrics Module]]

## Active Threads
- All known gaps resolved — wiki comprehensively ingested
- No remaining [!gap] items
- Vault ready to commit to repo
