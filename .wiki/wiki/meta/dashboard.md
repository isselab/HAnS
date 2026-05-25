---
type: meta
title: Dashboard
---
# Wiki Dashboard

## Recent Activity
```dataview
TABLE type, status, updated FROM "wiki" SORT updated DESC LIMIT 15
```

## Seed Pages (Need Development)
```dataview
LIST FROM "wiki" WHERE status = "seed" SORT updated ASC
```

## Modules by Status
```dataview
TABLE status, purpose FROM "wiki/modules" WHERE type = "module" SORT status ASC
```

## Dependencies Missing Risk Assessment
```dataview
LIST FROM "wiki/dependencies" WHERE !risk OR risk = ""
```
