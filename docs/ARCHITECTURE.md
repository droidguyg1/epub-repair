# EPUB Repair Architecture

```text
Main
 │
 ▼
XhtmlFileFinder
 │
 ▼
XhtmlRepair
 │
 ├───────────────────────────────┐
 ▼                               ▼
XmlReader                    RepairEngine
 │                               │
 ▼                               ▼
NormalizationPipeline        RepairRule(s)
 │                               │
 ▼                               │
PreParseNormalizer(s)            │
 │                               │
 ▼                               │
XmlReadResult ────────────────────┘
 │
 ▼
RepairContext
 │
 ▼
XmlWriter
```

## Processing boundary

`XmlReader` reads the source text and runs the `NormalizationPipeline` before JDOM parsing. Pre-parse normalizers repair only narrowly defined syntax defects that would otherwise prevent the XHTML from being parsed as XML.

`XmlReadResult` carries both the parsed JDOM document and per-normalizer change counts into `XhtmlRepair`. `RepairContext` then combines normalization statistics with changes reported by DOM-level repair rules.

`XhtmlRepair` writes the document when either pre-parse normalization or a repair rule made a change. This ensures normalization-only fixes are persisted.

## Current extension points

* `PreParseNormalizer` — source-text normalization before XML parsing.
* `RepairRule` — structured repair after a JDOM document exists.

Both extension points are designed for deterministic, conservative, idempotent transformations with independent reporting.
