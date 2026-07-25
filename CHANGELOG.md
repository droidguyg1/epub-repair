# CHANGELOG.md

# Changelog

All notable changes to EPUB Repair are documented in this file.

The project follows milestone-based development. Each milestone is developed on its own feature branch, reviewed, tested, and merged independently.

---

# Unreleased

## Planned — Milestone 2B

### Improve `WrapOrphanTextRule`

**Status:** Planned

#### Motivation

Real EPUB content revealed that the current implementation incorrectly treats inline XHTML elements as paragraph boundaries.

Example input:

```html
Did the castle <i>help</i> Filipe to find their castle?<br/><br/>
'Is everything alright, Ken?'
```

Current output:

```xml
<p>Did the castle</p>

<i>help</i>

<p>Filipe to find their castle?</p>
```

Desired output:

```xml
<p>
  Did the castle
  <i>help</i>
  Filipe to find their castle?
</p>

<p>
  'Is everything alright, Ken?'
</p>
```

#### Planned work

* Treat inline XHTML elements as part of the current paragraph.
* Distinguish phrasing content from paragraph boundaries.
* Preserve inline formatting while wrapping orphan content.
* Add regression tests covering inline elements within orphan paragraphs.

---

## Planned — Milestone 2C

### Remove orphan body-level links

**Status:** Planned

#### Motivation

Some EPUBs contain standalone `<a>` elements directly under `<body>`.

Example:

```xml
<body>
  ...
  <a href="chapter2.xhtml">Next chapter</a>
</body>
```

This is invalid XHTML 1.1.

#### Planned work

Introduce an optional repair rule that removes or otherwise handles standalone body-level anchor elements.

Because these links may represent intentional navigation or editorial content, this rule should be implemented conservatively and may ultimately become configurable rather than enabled unconditionally.

---

## Planned — Milestone 2D

### Remove unsupported XHTML attributes

**Status:** Planned

#### Motivation

Modern HTML editors occasionally introduce HTML5 attributes that are invalid in EPUB 2 XHTML.

Example:

```xml
<div aria-hidden="true">
```

EPUBCheck rejects these attributes.

#### Planned work

Create a repair rule that removes attributes unsupported by XHTML 1.1.

The initial implementation will likely begin with attributes encountered in the real EPUB corpus (for example, `aria-hidden`) and expand only as additional real-world cases are discovered.

Regression tests will accompany every newly supported attribute.

---

# Completed

## Milestone 2A

* Added pre-parse normalization framework.
* Added `NormalizationPipeline`.
* Added `PreParseNormalizer`.
* Added `SelfCloseElementNormalizer`.
* Added `XmlReadResult`.
* Added normalization statistics.
* Persist normalization-only changes.
* Added per-normalizer reporting.
* Added regression tests for malformed XHTML empty elements.
* Fixed whitespace handling when inserting self-closing tags.

---

## Milestone 1D

* Added `RemoveParagraphHeightRule`.

---

## Milestone 1C

* Added `RemoveEmptyParagraphRule`.

---

## Milestone 1B

* Added repair framework.
* Added `RepairContext`.
* Added `RepairEngine`.
* Added `RepairReport`.
* Added `WrapOrphanTextRule`.

---

## Milestone 1A

* Initial project infrastructure.
* XML reader.
* XML writer.
* XHTML round-trip support.
* Recursive file discovery.
* Reporting.
* Executable shaded JAR.
