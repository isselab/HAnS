> If you are using the claude-obsidian plugin, the skills handle everything here automatically.
> This file is the reference document. Read it to understand how the system works.
> Based on Andrej Karpathy's LLM Wiki pattern.

---

## What This Is

You are maintaining a persistent, compounding wiki inside an Obsidian vault. You don't just answer questions. You build and maintain a structured knowledge base that gets richer with every source added and every question asked. The human curates sources and asks questions. You do all the writing, cross-referencing, filing, and maintenance.

The wiki is the product. Chat is just the interface.

The key difference from RAG: the wiki is a persistent artifact. Cross-references are already there. Contradictions have been flagged. Synthesis already reflects everything that was read. Knowledge compounds like interest.

---

## 1 — Architecture (Mode B: GitHub/Repository)

```
vault/
├── .raw/                   # Layer 1: immutable source documents
│   ├── articles/
│   ├── code-dumps/
│   ├── issues/
│   └── docs/
│
├── wiki/                   # Layer 2: LLM-generated knowledge base
│   ├── index.md            # master catalog of all wiki pages
│   ├── log.md              # chronological record of all operations
│   ├── hot.md              # hot cache: recent context summary (~500 words)
│   ├── overview.md         # executive summary of the entire wiki
│   ├── modules/            # one note per module / package / service
│   │   └── _index.md
│   ├── components/         # reusable components and extension points
│   │   └── _index.md
│   ├── decisions/          # Architecture Decision Records
│   │   └── _index.md
│   ├── dependencies/       # external deps, versions, risk
│   │   └── _index.md
│   ├── flows/              # data flows, request paths, annotation processing
│   │   └── _index.md
│   └── meta/               # dashboards, lint reports
│
├── WIKI.md                 # Layer 3: this file
└── CLAUDE.md               # project instructions (auto-loaded)
```

### Rules

- `.raw/` is read-only. Never modify source files.
- `wiki/` is yours. Create, update, rename, delete freely.
- Every wiki page has frontmatter. No exceptions.
- Wikilinks over paths. Use `[[Page Name]]` not `[text](path/to/file.md)`.
- Atomic notes. One concept per page. If it covers two things, split it.
- Update, don't duplicate. If a page exists, update it.

---

## 2 — Hot Cache

`wiki/hot.md` is a ~500-word summary of the most recent context.

Update hot.md after every ingest, after any significant query exchange, and at end of session.

Format:

```markdown
---
type: meta
title: "Hot Cache"
updated: YYYY-MM-DDTHH:MM:SS
---

# Recent Context

## Last Updated
YYYY-MM-DD — [what happened]

## Key Recent Facts
- [Most important recent takeaway]
- [Second most important]

## Recent Changes
- Created: [[New Page 1]], [[New Page 2]]
- Updated: [[Existing Page]] (added section on X)
- Flagged: Contradiction between [[Page A]] and [[Page B]] on topic Y

## Active Threads
- User is currently researching [topic]
- Open question: [thing still being investigated]
```

Keep under 500 words. Overwrite completely each time.

---

## 3 — Frontmatter Schema

Every wiki page starts with flat YAML frontmatter.

### Universal fields:

```yaml
---
type: <source|entity|concept|module|component|decision|flow|overview|meta>
title: "Human-Readable Title"
created: 2026-05-25
updated: 2026-05-25
tags:
  - <domain-tag>
  - <type-tag>
status: <seed|developing|mature|evergreen>
related:
  - "[[Other Page]]"
sources:
  - "[[.raw/docs/source-file.md]]"
---
```

### Module-specific additions:

`path`, `status` (active|deprecated|experimental|planned), `language`, `purpose`, `maintainer`, `depends_on`, `used_by`, `linked_issues`

### Decision-specific additions:

`status` (active|pending|done|blocked|superseded), `priority` (1-5), `date`, `owner`, `due_date`, `context`

---

## 4 — Operations

### INGEST — Single Source

1. Read the source completely.
2. Create source summary in `wiki/` appropriate subfolder.
3. Create or update module/component/dependency pages as relevant.
4. Update `wiki/index.md`. Add entries for all new pages.
5. Update `wiki/hot.md` with this ingest's context.
6. Append to `wiki/log.md` (new entries at the TOP).
7. Check for contradictions. Flag with `> [!contradiction]` callouts on both pages.

### QUERY — Answering Questions

1. Read `wiki/hot.md` first.
2. Read `wiki/index.md` to find relevant pages.
3. Read those pages (3-5 typically).
4. Synthesize the answer in chat. Cite with wikilinks.
5. Offer to file as a wiki page in `wiki/questions/`.

### LINT — Health Check

Checks: orphan pages, dead links, stale claims, missing frontmatter, empty sections.

Output: `wiki/meta/lint-report-YYYY-MM-DD.md`. Ask before auto-fixing.

---

## 5 — Log Format

```markdown
## [YYYY-MM-DD] ingest | Source Title
- Source: `.raw/docs/filename.md`
- Pages created: [[Page 1]], [[Page 2]]
- Pages updated: [[Page 3]]
- Key insight: One sentence on what is new.
```

New entries go at TOP of log.md. Append-only — never edit past entries.

---

## 6 — Cross-Referencing

When updating Page A to mention Page B, check if Page B should link back. Bidirectional links make graph view useful.

---

## 7 — Context Window Management

- Read `hot.md` first. May already have the answer.
- Read `index.md` second.
- Read only 3-5 pages per query.
- Use PATCH for surgical edits. Never rewrite a whole file to change one field.
- Keep wiki pages short: 100-300 lines max. Split long pages.

---

## 8 — Conventions

### Naming

- **Filenames**: Title Case with spaces (`Feature Model Parser.md`)
- **Folders**: lowercase with dashes (`wiki/data-models/`)
- **Tags**: lowercase, hierarchical (`#domain/architecture`)
- **Unique filenames** so wikilinks work without paths

### Writing Style

- Declarative, present tense.
- Link liberally. Every mention of a wiki page gets a wikilink.
- Cite sources: `(Source: [[Page]])`.
- Flag uncertainty: `> [!gap] This needs more evidence.`
- Flag contradictions: `> [!contradiction] [[Page A]] claims X, but [[Page B]] says Y.`

---

*Based on Andrej Karpathy's LLM Wiki pattern. Plugin: claude-obsidian by AgriciDaniel.*
