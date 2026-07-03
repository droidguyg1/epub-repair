# Contributing to EPUB Repair

Thank you for contributing to EPUB Repair.

The project is intended to be a long-lived, production-quality EPUB repair utility. The emphasis is on correctness, maintainability, and preserving EPUB validity.

---

# Project Goals

The project repairs EPUB 2 XHTML documents while preserving their XML structure.

The application should never modify a document unless a repair rule explicitly requires the change.

---

# Development Principles

## Build Quality

The project must always build successfully.

Every commit pushed to a feature branch should compile with

```bash
mvn clean verify
```

The `main` branch must always remain in a releasable state.

---

## Java Version

Java 17 is required.

---

## Code Quality

* Production-quality code.
* One public class per source file.
* Javadoc on every public class and public method.
* Descriptive names.
* No wildcard imports.
* No public mutable fields.
* Favor composition over inheritance.
* Prefer immutable objects where practical.

---

## XML Philosophy

EPUB Repair edits XML documents.

It is intentionally conservative.

The application modifies only those nodes required by enabled repair rules.

Formatting, namespaces, comments, and processing instructions should be preserved whenever possible.

---

# Repair Rules

Each repair rule should:

* solve exactly one problem
* be independently testable
* report its own statistics
* avoid knowledge of other repair rules

Whenever practical, a new repair should be implemented as a new rule instead of modifying an existing one.

---

# Testing

Every bug fixed becomes a regression test.

Whenever a repair rule is added or modified, corresponding test documents should be added.

---

# Git Workflow

Development takes place on feature branches.

Typical workflow:

1. Create a feature branch.
2. Implement one logical change.
3. Build.
4. Test.
5. Submit a Pull Request.
6. Merge into `main`.

Direct commits to `main` should be avoided.

---

# Commit Messages

Each commit should represent one logical change.

Good examples:

* Initial Maven project
* Add XML reader
* Implement WrapOrphanTextRule
* Add RemoveEmptyParagraphRule

Avoid generic messages such as:

* misc
* fixes
* updates

---

# Pull Requests

Each Pull Request should describe:

* the problem being solved
* the implemented solution
* testing performed
* any behavioral changes

---

# Architecture Decisions

Major architectural decisions belong in the `docs/adr` directory.

Before introducing significant architectural changes, consider whether a new Architecture Decision Record should be added.

---

# Philosophy

Readable code is preferred over clever code.

Correctness is preferred over brevity.

Maintainability is preferred over premature optimization.

# Development Process and Rules

## Before committing

- Review every changed file.
- Ensure every change is intentional.
- Remove commented-out code.
- Remove temporary debugging code.
- Ensure new public classes and methods have Javadoc.
- Run: `mvn clean verify`

## Milestone

- A milestone is not complete until mvn clean verify passes and the packaged JAR passes an end-to-end test against representative real input.