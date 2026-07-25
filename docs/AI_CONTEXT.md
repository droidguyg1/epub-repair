# AI_CONTEXT.md

# EPUB Repair — AI Development Context

> This document captures the current state, architecture, conventions, and development philosophy of the EPUB Repair project.
>
> Its purpose is to allow development to continue seamlessly in a new ChatGPT conversation without relying on previous chat history.

---

# Project

**Name**

EPUB Repair

**Package**

```
org.stanb.epubrepair
```

**Language**

Java 17

**Build**

Maven

**License**

Apache License 2.0

Repository follows Git Flow with GitHub Pull Requests.

---

# Project Goal

EPUB Repair repairs malformed EPUB 2 XHTML files while preserving valid XML structure.

The project deliberately performs **conservative**, **deterministic** repairs.

If something is not explicitly defined as a repair, EPUB Repair does not change it.

Correctness is always preferred over aggressive cleanup.

---

# Current Version

Current development version:

```
0.5.0-SNAPSHOT
```

---

# Development Philosophy

The project follows several core principles.

## Conservative repairs

Never rewrite more than necessary.

## Deterministic

The same input always produces the same output.

## Idempotent

Running EPUB Repair twice produces no additional changes.

## Independent rules

Repair rules must not depend on each other.

Execution order should not matter unless explicitly documented.

## Production quality

Prototype code is not accepted.

Every class should be production-ready.

---

# Architecture

Current processing pipeline:

```
Find XHTML files

↓

Read source text

↓

Run pre-parse normalizers

↓

Parse XML (JDOM)

↓

Create RepairContext

↓

Run RepairEngine

↓

Write XML (only if changes occurred)

↓

Aggregate statistics

↓

Print report
```

---

# Pre-Parse Normalization

Normalizers execute **before XML parsing**.

Purpose:

Repair syntax that prevents JDOM from parsing the document.

Normalizers operate on source text.

Repair rules operate on the parsed XML tree.

Current interfaces:

```
PreParseNormalizer
NormalizationPipeline
NormalizationResult
XmlReadResult
```

Current implementation:

```
SelfCloseElementNormalizer
```

Supported XHTML empty elements:

* area
* base
* br
* col
* hr
* img
* input
* link
* meta
* param

Normalizers are:

* deterministic
* conservative
* idempotent

---

# Repair Engine

Repair rules execute after XML parsing.

Current rules:

```
wrap-orphan-text

remove-empty-paragraph

remove-paragraph-height
```

Each rule:

* independent
* idempotent
* deterministic
* reports its own changes

---

# Reporting

RepairContext records:

* normalizer changes
* repair rule changes

RepairReport aggregates:

* processed files
* failed files
* changes by normalizer
* changes by repair rule
* total changes

Current console output:

```
Files processed: N
Files failed:    N
Changes made:    N

Changes by normalizer:
  ...

Changes by rule:
  ...
```

---

# XML

Library:

```
JDOM2
```

Important decision:

The project intentionally does **not** use jsoup.

Reason:

jsoup serializes XHTML using HTML rules and changes XML semantics.

---

# XML Writer

Pretty-print output.

Readable by humans.

Preserves:

* namespaces
* XML declaration
* comments
* processing instructions

Only writes files when:

* a normalizer changed the source

OR

* a repair rule modified the document

---

# Package Structure

```
org.stanb.epubrepair

Main

io/

normalize/

repair/

rules/

xml/
```

---

# Coding Standards

See:

```
docs/CODING_STANDARDS.md
```

Important conventions:

* Java 17
* Production quality
* Public Javadoc
* Two-space indentation
* Acronyms become words

Examples:

```
XmlReader

XmlWriter

XhtmlRepair
```

NOT

```
XMLReader

XMLWriter
```

Streams are encouraged when they improve readability.

Prefer simple imperative code over clever functional code.

---

# Development Workflow

Every milestone has:

* dedicated feature branch
* version bump
* CHANGELOG update
* tests
* manual end-to-end test
* Pull Request
* GitHub Actions verification
* merge to main

Typical workflow:

```
feature/milestone-x

↓

development

↓

review

↓

mvn clean verify

↓

manual JAR test

↓

PR

↓

GitHub Actions

↓

merge
```

---

# Quality Gates

A milestone is complete only if:

```
mvn clean verify
```

passes

AND

GitHub Actions passes

AND

manual end-to-end testing succeeds

AND

the executable JAR works.

---

# Testing Philosophy

Every discovered bug becomes a regression test.

Tests include:

* unit tests
* integration tests
* real EPUB end-to-end testing

Whitespace-sensitive XML should generally be verified structurally using JDOM rather than brittle string comparisons.

---

# Lessons Learned

## JDOM iterators

Do not modify descendants while iterating.

Copy affected elements first.

## Pretty printing

Pretty output is essential for manual verification.

## Public APIs

Changing return types (for example, `XmlReader.read()`) requires updating every caller and test.

## Real corpus first

Roadmap should be driven by actual EPUB defects rather than hypothetical problems.

Real files are the best specification.

---

# Implemented Milestones

## Milestone 0

Project infrastructure

GitHub

CI

Coding standards

Documentation

Executable shaded JAR

---

## Milestone 1A

XML reader

XML writer

Recursive file discovery

Round-trip support

---

## Milestone 1B

Repair engine

RepairContext

RepairReport

Wrap orphan text

---

## Milestone 1C

Remove empty paragraphs

---

## Milestone 1D

Remove paragraph height

---

## Milestone 2A

Pre-parse normalization

SelfCloseElementNormalizer

Normalization reporting

Normalization persistence

Normalization-only rewrites

---

# Current Status

Latest completed milestone:

```
Milestone 2A
```

Repository state:

* merged to main

Version:

```
0.5.0-SNAPSHOT
```

---

# Future Development

Future milestones should come from:

1. Real EPUB validation failures

2. Real malformed XHTML

3. Real repetitive repair work

Avoid speculative architecture.

Implement only what the corpus demonstrates is needed.

---

# Working Style

The preferred collaboration pattern is:

1. User identifies a real EPUB issue.
2. Agree on the design.
3. AI generates a complete milestone batch.
4. User reviews in VS Code.
5. User builds with Maven.
6. Compilation and test issues are fixed.
7. Manual end-to-end testing is performed.
8. Pull Request is created.
9. GitHub Actions verifies the build.
10. PR is merged.

This workflow has proven both efficient and reliable.

---

# When Starting a New Chat

Begin with something like:

> Continue development of EPUB Repair.
>
> Read AI_CONTEXT.md first.
>
> We are continuing from Milestone 2A.

Also provide:

* README.md
* ARCHITECTURE.md
* CHANGELOG.md
* AI_CONTEXT.md

Optionally include:

* docs/CODING_STANDARDS.md

These documents provide enough context to continue development without relying on previous conversation history.
