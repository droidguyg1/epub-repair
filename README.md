# EPUB Repair

**EPUB Repair** is a Java utility for repairing and normalizing EPUB 2 XHTML documents while preserving their XML structure and EPUB validity.

The project originated from editing EPUB books with Calibre, where malformed or inconsistent XHTML required repetitive manual repair. Rather than fixing each problem by hand, EPUB Repair provides a rule-based repair engine that applies deterministic, well-defined corrections.

The project targets **EPUB 2** documents, whose content files are XHTML 1.1 XML documents.

---

# Goals

The primary goals of EPUB Repair are:

* Repair malformed EPUB XHTML without altering unrelated content.
* Preserve EPUB validity.
* Preserve XML structure, namespaces, comments, and processing instructions.
* Apply only explicitly requested repairs.
* Produce deterministic output.
* Provide an extensible architecture for adding new repair rules.

The project emphasizes correctness over aggressive cleanup.

---

# Design Philosophy

EPUB Repair follows a conservative editing philosophy.

The XML document belongs to the user.

The application modifies only those parts of the document that are explicitly targeted by enabled repair rules.

If a document contains valid XHTML, EPUB Repair should leave it unchanged.

Running the program twice on the same document should produce identical output.

The program must never "pretty print" or reformat an entire document simply because it was opened and saved.

---

# Why XML Instead of HTML?

Although many EPUB content files use the `.html` extension, EPUB 2 specifies that these files are **XHTML**.

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

Initial repair rules include:

* Wrap orphan text nodes inside paragraph elements.
* Remove empty paragraphs.
* Remove invalid paragraph height declarations.
* Additional rules will be added over time.

Each repair is implemented as an independent rule.

---

# Architecture

```
org.stanb.epubrepair

    Main

    cli/

    repair/
        XHTMLRepair
        RepairContext
        RepairReport
        RepairRule

    rules/

    xml/

    io/

    util/
```

Every repair rule implements the common `RepairRule` interface.

The repair engine loads the enabled rules and applies them sequentially to each XHTML document.

Rules do not depend on one another.

---

# Repair Rules

Each repair rule must satisfy the following principles.

* Independent.
* Deterministic.
* Testable.
* Report its own statistics.
* Modify only what the rule is responsible for.

Rules must never rely on execution order except where explicitly documented.

---

# Processing Model

For every XHTML document:

```
Read XML

↓

Validate XML

↓

Apply enabled repair rules

↓

Write XML
```

The XML writer must preserve:

* XML declaration
* namespaces
* comments
* processing instructions
* empty XML elements
* EPUB validity

---

# Command Line

The initial interface is expected to be:

```
epubrepair <directory>
```

Future versions will support enabling individual repair rules:

```
epubrepair book --rule wrap-orphan-text

epubrepair book \
    --rule wrap-orphan-text \
    --rule remove-empty-paragraphs
```

---

# Testing

Every bug fixed becomes a permanent regression test.

The project maintains a corpus of XHTML documents representing real EPUB problems.

Whenever a repair rule is modified, these documents verify that previous repairs continue to work correctly.

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
* Clean package organization
* Logging instead of scattered console output

## Project Quality

* No technical debt by design.
* Refactor before adding new features when necessary.
* Every milestone produces a working application.
* Every milestone builds successfully.

---

# Git Workflow

The project follows a feature-branch workflow.

`main` always represents a stable, buildable state.

Each milestone is developed on its own feature branch.

Typical workflow:

```
feature/milestone-1a

↓

Development

↓

Testing

↓

Pull Request

↓

Merge into main
```

Direct commits to `main` are avoided.

Each commit should represent a single logical change.

Examples:

* Initial Maven project
* XML reader
* XML writer
* Repair framework
* Wrap orphan text rule

---

# Roadmap

## Milestone 1A

* Maven project
* XML reader
* XML writer
* XHTML round-trip
* Directory traversal
* Reporting

## Milestone 1B

* Repair framework
* Wrap orphan text rule

## Milestone 1C

* Remove empty paragraph rule

## Milestone 1D

* Remove paragraph height rule

Future milestones will add additional repair rules as new EPUB issues are discovered.

---

# Contributing

The project is intentionally designed around small, independent repair rules.

New functionality should normally be implemented as a new rule rather than by modifying existing rules.

Keeping rules isolated makes the project easier to test, review, and maintain.

---

# License

License to be determined.
