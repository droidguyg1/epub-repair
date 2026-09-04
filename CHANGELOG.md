# CHANGELOG.md

# Changelog

All notable changes to EPUB Repair are documented in this file.

The project follows milestone-based development. Each milestone is developed on its own feature branch, reviewed, tested, and merged independently.

---

# Planned / Unreleased

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

## Planned — Milestone 2D

### Remove unsupported XHTML attributes

**Status:** Planned

#### Motivation

Modern HTML editors occasionally introduce HTML5 attributes that are invalid in EPUB 2 XHTML.

Example:

```xml
<div aria-hidden="true">
````

EPUBCheck rejects these attributes.

#### Planned work

Create a repair rule that removes attributes unsupported by XHTML 1.1.

The initial implementation will likely begin with attributes encountered in the real EPUB corpus (for example, `aria-hidden`) and expand only as additional real-world cases are discovered.

Regression tests will accompany every newly supported attribute.

---

# Completed

## Completed — Milestone 2C

### Remove orphan body-level links

**Status:** Completed

#### Motivation

Some EPUBs contain standalone `<a>` elements directly under `<body>`.

Example:

```xml
<body>
  ...
  <a href="chapter2.xhtml">Next chapter</a>
  ...
</body>
```

This is invalid XHTML 1.1. Report each removed link on stdout, including its source file, link text, and target.

#### Completed work

* Added `RemoveOrphanBodyLinkRule`.
* Removed `<a>` elements that are direct children of `<body>`.
* Preserved links nested inside other elements.
* Reported each removed link to stdout with its source file, link text, and `href` target.
* Added unit tests covering removal, multiple links, nested links, no-op behavior, and idempotence.

Version: 0.6.0-SNAPSHOT

---

## Completed — Milestone 2B

### Improve `WrapOrphanTextRule`

**Status:** Completed

#### Motivation

Real EPUB content revealed that the current implementation incorrectly treated inline XHTML elements as paragraph boundaries.

Example input:

```html
Did the castle <i>help</i> Filipe to find their castle?

'Is everything alright, Ken?'
````

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

#### Completed work

* Refactored `WrapOrphanTextRule` to group direct-body phrasing content into paragraphs.
* Distinguished phrasing content from paragraph boundaries.
* Preserved inline XHTML elements and their formatting while wrapping orphan content.
* Added regression tests covering inline elements within orphan paragraphs, including leading, trailing, and multiple inline elements.
* Added a real-world regression test based on EPUB content containing inline formatting and NBSP-only paragraphs.
* Verified that the repair remains idempotent.

---

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
* Removed paragraphs containing only whitespace or non-breaking spaces (`U+00A0`).
* Preserved paragraphs containing child elements.
* Added regression tests for ordinary whitespace, non-breaking spaces, mixed whitespace, child elements, and idempotence.

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
