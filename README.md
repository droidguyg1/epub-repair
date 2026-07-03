# EPUB Repair

**EPUB Repair** is a Java utility for repairing and normalizing EPUB 2 XHTML documents while preserving their XML structure and EPUB validity.

The project originated from editing EPUB books with Calibre, where malformed or inconsistent XHTML required repetitive manual repair. Rather than fixing each problem by hand, EPUB Repair provides a rule-based repair engine that applies deterministic, well-defined corrections.

The project targets **EPUB 2** documents, whose content files are XHTML XML documents.

---

# Goals

The primary goals of EPUB Repair are:

* Repair malformed EPUB XHTML without altering unrelated content.
* Preserve EPUB validity.
* Preserve XML structure, namespaces, comments, and processing instructions.
* Apply only explicitly defined repairs.
* Produce deterministic output.
* Produce readable, consistently formatted XHTML for visual inspection.
* Provide an extensible architecture for adding new repair rules.

The project emphasizes correctness over aggressive cleanup.

---

# Design Philosophy

EPUB Repair follows a conservative editing philosophy.

The XML document belongs to the user.

The application modifies only those parts of the document that are explicitly targeted by repair rules.

If a document requires no repair, EPUB Repair does not rewrite it.

Running the program twice on the same repaired document should produce no additional changes.

When a document is changed, the XML writer produces consistently formatted, human-readable XHTML so that the result can be visually inspected.

---

# Why XML Instead of HTML?

Although many EPUB content files use the `.html` extension, EPUB 2 content documents are XHTML.

For example:

```xml
<?xml version="1.0" encoding="utf-8"?>
<html xmlns="http://www.w3.org/1999/xhtml">
```

These are XML documents, not HTML documents.

During early experimentation, jsoup proved to be an excellent HTML parser but an unsuitable serializer for EPUB XHTML because it rewrote XML declarations and normalized documents using HTML rules.

EPUB Repair therefore uses an XML parser and writer so that documents remain valid XHTML after processing.

---

# Supported EPUB Versions

Current target:

* EPUB 2.0
* EPUB 2.0.1

Future support for EPUB 3 may be added, but EPUB 2 compatibility is the primary objective.

---

# Current Repair Rules

Current repair rules include:

* `wrap-orphan-text` — wraps orphan text nodes inside paragraph elements.
* `remove-empty-paragraph` — removes paragraphs containing no meaningful content.

Planned rules include:

* Remove invalid paragraph height declarations.
* Additional rules discovered through real EPUB repair work.

Each repair is implemented as an independent rule and reports its own change count.

---

# Architecture

```text
org.stanb.epubrepair

    Main

    io/
        XhtmlFileFinder

    repair/
        RepairContext
        RepairEngine
        RepairReport
        RepairRule
        XhtmlRepair

    rules/
        WrapOrphanTextRule
        RemoveEmptyParagraphRule

    xml/
        XmlReader
        XmlWriter
```

Every repair rule implements the common `RepairRule` interface.

The repair engine applies an ordered list of rules to each XHTML document.

Each rule reports its changes through the document's `RepairContext`. The application aggregates those changes into a `RepairReport` for the complete run.

Rules are designed to remain independent and must not rely on execution order except where explicitly documented.

---

# Repair Rules

Each repair rule must satisfy the following principles:

* Independent.
* Deterministic.
* Idempotent.
* Testable.
* Report its own statistics.
* Modify only what the rule is responsible for.

Rules must never rely on execution order except where explicitly documented.

---

# Processing Model

For every XHTML document:

```text
Find XHTML file

↓

Read XML

↓

Create repair context

↓

Apply repair rules

↓

Write XML only if changes were made

↓

Aggregate per-rule statistics
```

The XML layer must preserve valid XHTML structure, including:

* XML declaration
* namespaces
* comments
* processing instructions
* valid XML element syntax
* EPUB validity

Changed documents are written in a consistent, human-readable format for visual inspection.

---

# Command Line

Build and verify the project:

```bash
mvn clean verify
```

The Maven build produces a self-contained executable JAR containing EPUB Repair and its runtime dependencies.

Run EPUB Repair against a single XHTML file or a directory:

```bash
java -jar target/epub-repair-0.3.0-SNAPSHOT.jar <file-or-directory>
```

Directories are searched recursively for `.html` and `.xhtml` files.

The application reports the number of changes made to each file and prints an aggregate summary:

```text
Files processed: 12
Files failed:     0
Changes made:     37

Changes by rule:
  wrap-orphan-text: 30
  remove-empty-paragraph: 7
```

Rules that execute but make no changes are still included with a count of zero.

Future versions may support enabling individual repair rules from the command line.

---

# Testing

Every bug fixed should become a permanent regression test.

Repair rules are tested independently and through integration tests.

Tests verify:

* The intended repair is performed.
* Unrelated content is preserved.
* Rule-specific change counts are correct.
* Multiple rules work together correctly.
* A second run makes no additional changes.

Real XHTML files are also used for end-to-end testing.

A milestone is not complete until:

```bash
mvn clean verify
```

passes and the packaged executable JAR passes an end-to-end test against representative real input.

---

# Development Principles

The project follows these engineering principles.

## Code Quality

* Java 17
* Maven
* Production-quality code
* One public class per file
* Javadoc for public classes and methods
* Descriptive naming
* Acronyms treated as words in identifiers, such as `XmlReader` and `XhtmlRepair`
* Two-space indentation
* Clean package organization
* Modern Java APIs at project boundaries
* Implementation-specific adaptation kept inside implementation classes

## Project Quality

* Avoid technical debt by design.
* Refactor before adding new features when necessary.
* Prefer simple solutions over premature abstractions.
* Every milestone produces a working application.
* Every milestone builds successfully.
* Every milestone produces a runnable self-contained JAR.
* Every milestone passes automated tests and a manual end-to-end test.

---

# Git Workflow

The project follows a feature-branch workflow.

`main` always represents a stable, buildable state.

Each milestone is developed on its own feature branch.

Typical workflow:

```text
feature/milestone-1c

↓

Development

↓

Review

↓

mvn clean verify

↓

JAR end-to-end test

↓

Pull Request

↓

GitHub Actions

↓

Merge into main
```

Direct commits to `main` are avoided.

Each commit should represent a single logical change.

GitHub Actions runs the Maven verification build for pushes and pull requests according to the repository workflow configuration.

---

# Roadmap

## Milestone 1A — Complete

* Maven project
* XML reader
* XML writer
* XHTML round-trip
* Recursive directory traversal
* Reporting
* Self-contained executable JAR

## Milestone 1B — Complete

* Repair framework
* Repair context
* Repair engine
* Per-rule reporting
* Wrap orphan text rule

## Milestone 1C — In Progress

* Remove empty paragraph rule

## Milestone 1D

* Remove paragraph height rule

Future milestones will add additional repair rules as new EPUB issues are discovered.

---

# Contributing

The project is intentionally designed around small, independent repair rules.

New repair functionality should normally be implemented as a new rule rather than by expanding the responsibility of an existing rule.

Keeping rules isolated makes the project easier to test, review, maintain, and extend.

See `docs/CONTRIBUTING.md` and `docs/CODING_STANDARDS.md` for the project's development process and coding conventions.

---

# License

Licensed under the Apache License 2.0.
