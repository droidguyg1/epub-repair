package org.stanb.epubrepair.repair;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;

/** Holds the document and per-rule change counts for one XHTML file. */
public final class RepairContext {

  private final Path path;
  private final Document document;
  private final Map<String, Integer> changesByRule = new HashMap<>();

  public RepairContext(Path path, Document document) {
    this.path = path;
    this.document = document;
  }

  public Path path() {
    return path;
  }

  public Document document() {
    return document;
  }

  public void recordChange(String ruleId) {
    changesByRule.merge(ruleId, 1, Integer::sum);
  }

  public int changesFor(String ruleId) {
    return changesByRule.getOrDefault(ruleId, 0);
  }

  public Map<String, Integer> changesByRule() {
    return Map.copyOf(changesByRule);
  }

  public int totalChanges() {
    return changesByRule.values().stream()
        .mapToInt(Integer::intValue)
        .sum();
  }
}
