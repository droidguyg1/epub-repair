# EPUB Repair Coding Style

This document defines the coding conventions used throughout the EPUB Repair project.

The purpose of these guidelines is consistency rather than dogma.

Whenever two reasonable alternatives exist, choose the one that improves readability and long-term maintainability.

---

# General Philosophy

Code is read far more often than it is written.

Optimize for the next developer reading the code rather than the original developer writing it.

The project favors:

* readability
* correctness
* maintainability
* simplicity

over cleverness or unnecessary brevity.

---

# Formatting

## Indentation

* Two spaces.
* Never use tabs.

The project uses `.editorconfig` to enforce indentation.

---

## Line Length

Avoid unnecessarily long lines.

Wrap method calls and fluent APIs in a readable manner.

---

## Braces

Always use braces.

Preferred:

```java
if (valid) {
  repair();
}
```

Avoid:

```java
if (valid)
  repair();
```

---

# Naming

Use descriptive names.

Prefer

```java
Document document;
```

over

```java
Document doc;
```

Prefer

```java
Element paragraph;
```

over

```java
Element e;
```

Names should explain purpose rather than type.

---

# Javadoc

Public classes and public methods require Javadoc.

Javadoc should explain:

* why the class or method exists
* important design decisions
* invariants
* exceptional behavior

Do not repeat what the method signature already states.

---

# Comments

Comments explain intent.

Avoid comments that merely describe the code.

Good:

```java
// Paragraphs are processed on a copy of the child list because
// repair rules may insert or remove nodes during traversal.
```

Poor:

```java
// Increment i
i++;
```

---

# Methods

Methods should perform one logical task.

A method should operate at one level of abstraction.

Prefer:

```java
readDocument();
applyRules();
writeDocument();
```

over one large method performing all three operations.

---

# Classes

Prefer small, focused classes.

Every class should have one clearly defined responsibility.

Favor composition over inheritance.

Declare classes `final` unless inheritance is required.

---

# Exceptions

Never declare:

```java
throws Exception
```

Use specific exception types.

Create project-specific exceptions where appropriate.

---

# Streams

Streams are encouraged when they improve readability.

Streams should express data flow.

Business logic should be implemented in well-named methods.

Preferred:

```java
files.stream()
    .filter(FileFinder::isXhtmlFile)
    .sorted()
    .toList();
```

Avoid embedding significant business logic inside lambda expressions.

If a lambda becomes difficult to read, extract a method.

---

# Immutability

Prefer immutable objects where practical.

Fields should be `final` whenever possible.

Local variables do not need to be declared `final`.

---

# Logging

Use the project logging framework.

Avoid scattered `System.out.println()` calls.

Console output is reserved for the application's command-line interface.

---

# XML

EPUB Repair processes XML.

The application should preserve:

* XML declaration
* namespaces
* comments
* processing instructions
* valid XHTML

Do not modify unrelated XML.

---

# Repair Rules

Repair rules should:

* solve one problem
* be independently testable
* report their own statistics
* avoid dependencies on other rules

---

# Technical Debt

Avoid accumulating technical debt.

If an architectural issue is discovered, prefer correcting it before adding new functionality.

---

# Readability

Write code that can be understood in one uninterrupted read.

When choosing between two equivalent implementations, prefer the one that is easier to understand.

---

# Final Principle

Every source file should leave the next developer thinking:

> Whoever wrote this cared.
