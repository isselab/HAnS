---
type: source
title: README
created: '2026-05-25'
updated: '2026-05-25'
tags:
  - source/docs
status: mature
source_type: documentation
author: johmara et al.
confidence: high
key_claims:
  - >-
    HAnS supports feature-oriented development via annotation, mapping, and
    metrics
  - 'Three annotation markers: &begin, &end, &line'
  - 'Two mapping files: .feature-to-file, .feature-to-folder'
  - 'Four metrics: line count, file mapping, scattering, tangling'
  - Extension points expose metrics and highlighter services
  - Maintained by ISSELab at Ruhr University Bochum
---
# README

Primary project documentation for HAnS.

## Key Claims

- HAnS enables feature-oriented software development in JetBrains IDEs
- Core annotation syntax: `&begin[F]`, `&end[F]`, `&line[F]` (inline comments)
- File/folder-level mapping via `.feature-to-file` and `.feature-to-folder`
- Four metrics tracked: line count, feature-to-file, scattering degree, tangling degree
- Plugin is extensible via extension points for metrics and highlighting
- HAnS itself is annotated with features — self-demonstrating

## Pages Derived

- [[Annotation Syntax]]
- [[Feature Model Language]]
- [[Feature Metrics View]]
- [[Feature Model View]]
- [[Traffic Light Indicator]]
- [[Extension Points]]
- [[ISSELab]]
