---
type: source
title: plugin.xml
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - source/config
status: mature
source_type: configuration
confidence: high
key_claims:
  - >-
    4 custom languages: FeatureModel, FileAnnotation, FolderAnnotation,
    CodeAnnotations
  - '2 tool windows: Feature Model View (left), Feature Metrics View (bottom)'
  - 5 extension points for external plugins
  - CodeAnnotationInjector enables &begin/&end/&line in any file type
  - plugin.xml itself is annotated with HAnS features — self-demonstrating
---
# plugin.xml

IntelliJ Platform plugin manifest for HAnS.

## Pages Derived / Updated

- [[Feature Model Language]] — file types, parser classes, all 4 languages
- [[Annotation Syntax]] — .feature-file alias, injection mechanism
- [[Extension Points]] — full extension point IDs and interfaces
- [[Feature Model View]] — tool window ID, anchor, factory
- [[Feature Metrics View]] — tool window ID, anchor, factory
- [[Traffic Light Indicator]] — HansTrafficLightActionProvider class
- [[Syntax Highlighting]] — all 4 languages, color schemes
- [[Live Templates]] — EFA.xml, context types
- [[Annotation Processing Flow]] — injection model
- [[HAnS Feature Model]] — partial hierarchy visible from annotations
