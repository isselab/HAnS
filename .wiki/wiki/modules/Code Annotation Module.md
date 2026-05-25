---
type: module
title: Code Annotation Module
created: 2026-05-25
updated: 2026-05-25
tags:
  - domain/annotation
status: mature
related:
  - "[[Annotation Syntax]]"
  - "[[Feature Location Module]]"
  - "[[Annotation Processing Flow]]"
sources:
  - .raw/CodeAnnotationInjector.java
---

# Code Annotation Module

Package: `se.isselab.HAnS.featureAnnotation.codeAnnotation`

## CodeAnnotationInjector

Implements `MultiHostInjector`. Registered in `plugin.xml` as a `multiHostInjector`.

### What it targets

`elementsToInjectIn()` returns `[PsiComment.class]`. The injector is called for every PSI comment in every file.

**Early exits:**
- Skips `PsiDocCommentBase` (Javadoc).
- Skips comments whose text does not match `.*(&begin|&line|&end).*`.

### Injection logic

1. Retrieve `Commenter` for the host language via `LanguageCommenters.INSTANCE.forLanguage()`. Falls back to `//` / `/*` / `*/` if no commenter is registered.
2. Calls `registrar.startInjecting(CodeAnnotationLanguage.INSTANCE)`.
3. Determines `TextRange` to inject based on token type:

| Token type | `TextRange` injected |
|---|---|
| `END_OF_LINE_COMMENT` | `[lineCommentPrefix.length, textLength)` — strips leading `//` |
| `C_STYLE_COMMENT` | `[blockCommentPrefix.length, textLength - blockCommentSuffix.length)` — strips `/*` and `*/` |
| Other | `[0, textLength)` — full text |

4. Calls `registrar.doneInjecting()`.

> [!key-insight] The injector strips comment delimiters so the injected `CodeAnnotations` language fragment sees only the annotation text (e.g. ` &begin[Feature]`), not `// &begin[Feature]`.

## PSI types produced by CodeAnnotations grammar

These types are defined in the generated PSI for the `CodeAnnotations` language:

| PSI class | Role |
|---|---|
| `CodeAnnotationBeginmarker` | Represents `&begin[...]` marker |
| `CodeAnnotationEndmarker` | Represents `&end[...]` marker |
| `CodeAnnotationLinemarker` | Represents `&line[...]` marker |
| `CodeAnnotationFeature` | Feature name within a marker |
| `CodeAnnotationLpq` | Fully-qualified LPQ feature reference |
| `CodeAnnotationParameter` | Parameter inside brackets |

`FeatureLocationManager.getMarkerType()` uses Java 16+ `switch` pattern matching on these types:
```java
case CodeAnnotationBeginmarker ignored -> MarkerType.BEGIN
case CodeAnnotationEndmarker   ignored -> MarkerType.END
case CodeAnnotationLinemarker  ignored -> MarkerType.LINE
case null, default             -> MarkerType.NONE
```

## Injection host resolution

When `FeatureLocationManager` processes a `CodeAnnotationFile` reference, it checks whether the element's injection host is a `PsiComment`. This handles both:
- Regular Java/Kotlin files where the annotation is directly in a comment.
- XML/other files where the `CodeAnnotations` fragment is injected into a host comment (e.g. an XML `<!-- &begin[Feature] -->`).

## Related

- [[Annotation Syntax]] — user-facing syntax for `&begin`/`&end`/`&line`
- [[Feature Location Module]] — `FeatureLocationManager` consumes the PSI types produced here
- [[Annotation Processing Flow]] — end-to-end flow from comment to metrics
