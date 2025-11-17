# AGENTS.md - Developer Guide for AI Coding Agents

## Build, Lint & Test Commands
- **Build plugin**: `./gradlew buildPlugin`
- **Run all tests**: `./gradlew check` (includes tests + Kover coverage)
- **Run single test**: `./gradlew test --tests "se.isselab.HAnS.actionTests.AddActionTest.testAddActionIsEnabled"`
- **Run IDE with plugin**: `./gradlew runIde`
- **Code inspection**: `./gradlew qodana` (Qodana static analysis)
- **Plugin verification**: `./gradlew verifyPlugin`

## Code Style & Conventions
- **Language**: Java 21+ with Kotlin toolchain support
- **Package structure**: `se.isselab.HAnS.<feature>.<subpackage>`
- **Naming**: PascalCase for classes, camelCase for methods/variables
- **License headers**: Apache 2.0 license header required on all source files (see existing files for template)
- **Imports**: Organize with IntelliJ defaults; use explicit imports (no wildcards except for static)
- **Types**: Use IntelliJ Platform APIs (PsiElement, Document, etc.); prefer @NotNull/@Nullable annotations
- **Error handling**: Use IntelliJ logging (ILogger) for errors; handle exceptions gracefully
- **Testing**: Extend BasePlatformTestCase or ParsingTestCase; use myFixture for test setup
- **Feature annotations**: All code should be annotated with feature markers (`&begin[Feature]`, `&end[Feature]`, `&line[Feature]`)
- **Generated code**: Generated parser/lexer files go in `src/main/gen/` (excluded from version control)

## Contributing Guidelines
- PRs follow format: `[Folder]-[FeatureName]-[Contributor]-[Description]` (Folder: Feature/Bug)
- Annotate contributions with feature markers matching the `.feature-model`
- Tests required for new features when appropriate
