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