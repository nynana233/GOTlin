# GitHub Copilot Instructions

This document provides specific instructions for GitHub Copilot and other AI assistants working on the StarkK project.

## Conventional Commits Requirement

**All commit messages MUST strictly follow the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification v1.0.0.**

### Commit Message Format

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

### Commit Types

Use one of the following types for every commit:

| Type | Purpose | Example |
|------|---------|---------|
| `feat` | A new feature | `feat(client): add house query filters` |
| `fix` | A bug fix | `fix(parser): resolve Link header parsing` |
| `docs` | Documentation changes only | `docs: update API reference` |
| `style` | Code style changes (formatting, semicolons, etc.) | `style: reformat imports` |
| `refactor` | Code refactoring (not a fix or feature) | `refactor(client): simplify response handling` |
| `perf` | Performance improvements | `perf(cache): optimize memory usage` |
| `test` | Adding or updating tests | `test: add pagination edge cases` |
| `chore` | Build process, dependencies, tooling | `chore(deps): update gradle to 8.0` |
| `ci` | CI/CD configuration changes | `ci: add GitHub Actions workflow` |

### Scope Guidelines

Scope is optional but recommended. Use parentheses to indicate the scope:

- **Valid scopes**: `client`, `parser`, `api`, `cache`, `auth`, `ui`, `test`, `build`, `docs`
- **Format**: `type(scope): description`
- **Examples**:
  - `feat(client): add query filters`
  - `fix(parser): handle malformed responses`
  - `refactor(cache): improve invalidation logic`

### Description Guidelines

- **Imperative mood**: Use "add" not "added", "fix" not "fixed"
- **No period at end**: Description should not end with a period
- **Lowercase**: Start with lowercase (unless using proper nouns)
- **Concise**: Keep under 50 characters when possible (hard limit: 72 characters)
- **Specific**: Describe WHAT was changed, not HOW
- **Clear**: Avoid vague terms like "update", "improve", "handle"

**Good descriptions:**
- `add house query filters to client`
- `fix Link header parsing for pagination`
- `refactor response validation logic`
- `improve error message clarity`

**Bad descriptions:**
- `Update code`
- `Fix bug`
- `Modify stuff`
- `Handle things better`

### Breaking Changes

When a change breaks backward compatibility, add `!` before the colon:

```
feat!: redesign authentication API

This is a breaking change that removes support for token-based auth.
```

Or with scope:
```
feat(api)!: change response format from XML to JSON
```

### Optional Body

Include a body for complex changes:

```
feat(client): add advanced filtering options

Implement filtering by multiple criteria with AND/OR operators.
This enables more powerful query capabilities while maintaining
backwards compatibility through optional parameters.
```

### Optional Footer

Use footers for related issues:

```
fix(parser): handle edge case in pagination

Fixes #123
Related-to: #456
```

### Real-World Examples

```
feat(client): add house query filters
```

```
fix(parser): resolve Link header parsing for last page

The parser was incorrectly handling the final page marker
when the Link header contained multiple relations.
```

```
docs: update API documentation with v2 endpoints
```

```
test(cache): add invalidation edge case tests
```

```
refactor(client): simplify response handling

Consolidate duplicate validation logic and improve error reporting.
```

```
perf(cache): optimize memory allocation in LRU cache

Reduce GC pressure by 40% through better memory pooling.
```

```
chore(deps): update Kotlin to 1.9.0
```

```
ci: add GitHub Actions workflow for automated testing
```

## When Making Changes

### Step-by-Step Process

1. **Identify the change type**: Is this a feature, fix, documentation, refactor, test, etc.?
2. **Determine the scope**: What area of the code is affected? (client, parser, api, etc.)
3. **Craft the description**: Be specific about what changed
4. **Write the commit message**: `<type>(<scope>): <description>`
5. **Add body if needed**: Explain WHY the change was made
6. **Validate**: Ensure it matches the Conventional Commits spec

### File Change Examples

#### Adding a Feature
```
Files changed: src/main/kotlin/StarkKClient.kt

Commit message:
feat(client): add house query filters

Body:
Implement filtering capabilities for house queries with support for
price range, location, and amenities. Filters can be combined and
are optional for backwards compatibility.
```

#### Fixing a Bug
```
Files changed: src/main/kotlin/LinkHeaderParser.kt

Commit message:
fix(parser): handle malformed Link headers gracefully

Body:
Add null-safety checks and validation for Link header parsing to
prevent crashes when headers are malformed or missing.
```

#### Writing Tests
```
Files changed: src/test/kotlin/PaginationTest.kt

Commit message:
test(pagination): add edge case tests for last page

Body:
Add comprehensive tests for pagination edge cases including:
- Missing Link header
- Empty result set
- Single page results
- Multiple rel values
```

#### Updating Documentation
```
Files changed: starkk/readme.md, docs/API.md

Commit message:
docs: update API documentation with v2 endpoints
```

#### Refactoring Code
```
Files changed: src/main/kotlin/ResponseValidator.kt

Commit message:
refactor(validator): consolidate validation logic

Body:
Extract common validation patterns into shared utilities to reduce
code duplication and improve maintainability.
```

## Validation Checklist

Before committing, ensure:

- [ ] Commit type is one of: feat, fix, docs, style, refactor, perf, test, chore, ci
- [ ] Scope (if used) is in lowercase within parentheses
- [ ] Description starts with lowercase (unless proper noun)
- [ ] Description uses imperative mood ("add", not "added")
- [ ] No period at end of description
- [ ] Description is under 72 characters
- [ ] Breaking changes are marked with `!`
- [ ] Body explains WHY, not WHAT (WHAT is in the description)
- [ ] Footers reference issues correctly (Fixes #123, Related-to: #456)
- [ ] Message matches Conventional Commits v1.0.0 specification

## Related Resources

- [Conventional Commits Official Spec](https://www.conventionalcommits.org/en/v1.0.0/)
- [Contributing Guide](./starkk/contributing.md)
- [Project Repository](https://github.com/nanayaw/StarkK)

## Questions?

Refer to the [Contributing Guide](./starkk/contributing.md) for project-specific guidelines and the [Conventional Commits specification](https://www.conventionalcommits.org/en/v1.0.0/) for the authoritative format definition.

