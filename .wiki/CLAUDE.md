# HAnS — LLM Wiki

Mode: B (GitHub/Repository)
Purpose: JetBrains IDE plugin for feature-oriented software development (annotation, tracking, metrics)
Owner: johmara
Created: 2026-05-25

## Structure

```
vault/
├── .raw/              # README, git log exports, code dumps, issues, docs
├── wiki/
│   ├── index.md       # master catalog
│   ├── log.md         # append-only operation log
│   ├── hot.md         # recent context cache (~500 words)
│   ├── overview.md    # executive summary
│   ├── modules/       # one note per module / package / service
│   ├── components/    # reusable UI components and extension points
│   ├── decisions/     # Architecture Decision Records
│   ├── dependencies/  # external deps, versions, risk
│   └── flows/         # data flows, annotation processing paths
├── WIKI.md            # schema reference (Karpathy LLM Wiki pattern)
└── CLAUDE.md          # this file
```

## Conventions

- All notes use YAML frontmatter: type, status, created, updated, tags (minimum)
- Wikilinks use [[Note Name]] format — filenames are unique, no paths needed
- .raw/ contains source documents — never modify them
- wiki/index.md is the master catalog — update on every ingest
- wiki/log.md is append-only — new entries go at the TOP, never edit past entries
- Frontmatter for modules/: path, status (active|deprecated|experimental|planned), language, purpose, maintainer, depends_on, used_by, linked_issues

## Operations

- Ingest: drop source in .raw/, say "ingest [filename]"
- Query: ask any question — read hot.md → index.md → relevant pages → synthesize
- Lint: say "lint the wiki" to run a health check
- Scaffold complete: vault initialized 2026-05-25

## Key Pages

- [[HAnS Overview]] — what the plugin does
- [[Architecture Overview]] — module structure
- [[Tech Stack]] — Kotlin, IntelliJ Platform, Gradle
- [[Key Decisions]] — ADR log
