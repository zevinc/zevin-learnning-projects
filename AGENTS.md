# Agent Instructions

## AI Knowledge Base Loading Rule

This project has **two** knowledge bases:

| Path | Purpose |
|------|---------|
| `~/ai-workspace/docs/ai/` | AI‑accumulated technical knowledge |
| `~/ai-workspace/docs/manual/` | User‑written manuals & operational docs |

The agent must **not** load all files by default. Follow this lazy‑loading workflow:

### 1. Start from index files

Read both index files (if they exist):
- `~/ai-workspace/docs/ai/INDEX.md`
- `~/ai-workspace/docs/manual/INDEX.md`

### 2. Find relevant content

Use the `INDEX.md` files as catalogs. Locate relevant entries by:
- Category
- Tags
- Summary
- File path

### 3. Load only matching files

Prefer the knowledge base with the most relevant match.

### 4. Fallback search

If no entry is found in either index, optionally search both directories:

```bash
rg -n --ignore-case "<keyword>" ~/ai-workspace/docs/ai ~/ai-workspace/docs/manual
```

### 5. Create new knowledge (if applicable)

Prefer project knowledge over generic answers. When new reusable knowledge is produced, save it to the appropriate base: `~/ai-workspace/docs/ai/<category>/<yyyy-mm-dd>-<topic>.md`

### 6. Update index

After saving a new note, update the corresponding `INDEX.md` in the same directory.

## Knowledge Categories (`~/ai-workspace/docs/ai/`)

| Category | Description |
|----------|-------------|
| `architecture` | System design, architecture |
| `backend` | Java, Spring Boot, Gradle, DB, APIs |
| `agents` | Hermes, Claude Code, workflows, automation |
| `rag` | Embeddings, vector DB, retrieval |
| `prompts` | Prompt engineering, rules |
| `decisions` | ADRs, trade-offs, technical decisions |
| `troubleshooting` | Bugs, incidents, debugging |

> Additional categories may be defined in `~/ai-workspace/docs/manual/INDEX.md`.

## Loading Policy Summary

- ✅ Always start from both `INDEX.md` files
- ✅ Only read detailed markdown files when relevant
- ❌ Never read all files in knowledge base directories



# Spring MVC WebMvcConfigurer Learning Rules

## Goal

Help me systematically learn all extension points of Spring MVC `WebMvcConfigurer`.

Use Spring Boot defaults unless the current lesson explicitly requires overriding them.

Do not use `@EnableWebMvc` unless explaining full manual MVC takeover.

## Learning Scope

Cover all `WebMvcConfigurer` methods:

1. configurePathMatch
2. configureContentNegotiation
3. configureApiVersioning
4. configureAsyncSupport
5. configureDefaultServletHandling
6. addFormatters
7. addInterceptors
8. addResourceHandlers
9. addCorsMappings
10. addViewControllers
11. configureViewResolvers
12. addArgumentResolvers
13. addReturnValueHandlers
14. configureMessageConverters
15. extendMessageConverters
16. configureHandlerExceptionResolvers
17. extendHandlerExceptionResolvers
18. addErrorResponseInterceptors
19. getValidator
20. getMessageCodesResolver

For Spring Framework 7+, also explain deprecated methods and replacement APIs.

## Answer Format

For each method, always explain:

1. What problem it solves
2. When to use it
3. When not to use it
4. Spring Boot default behavior
5. Minimal runnable example
6. Test case using MockMvc
7. Common pitfalls
8. Relation to DispatcherServlet / HandlerMapping / HandlerAdapter / HandlerExceptionResolver

## Code Rules

Use Java, Spring Boot, Gradle, Lombok only when useful.

Prefer small independent examples.

Every example must be runnable.

Every feature must include:
- config class
- controller or test endpoint
- MockMvc test
- notes in - `~/ai-workspace/docs/ai/`

## Knowledge Capture Rule

After each completed topic, create or update:

docs/ai/spring-mvc/<method-name>.md

Each note must include:
- summary
- key concepts
- source code path
- test path
- common mistakes
- interview-level explanation

Also update:
`~/ai-workspace/docs/ai/INDEX.md`