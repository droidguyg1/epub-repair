package org.stanb.epubrepair.repair;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.jdom2.Document;

public final class RepairContext {

  private final Path path;
  private final Document document;
  private final Map<String, Integer> changesByNormalizer;
  private final Map<String, Integer> changesByRule = new HashMap<>();

  public RepairContext(Path path, Document document) {
    this(path, document, Map.of());
  }

  public RepairContext(
      Path path,
      Document document,
      Map<String, Integer> changesByNormalizer) {
    this.path = path;
    this.document = document;
    this.changesByNormalizer = Map.copyOf(changesByNormalizer);
  }

  public Path path() {
    return path;
  }

  public Document document() {
    return document;
  }

  public Map<String, Integer> changesByNormalizer() {
    return changesByNormalizer;
  }

  public int normalizationChanges() {
    return changesByNormalizer.values().stream()
        .mapToInt(Integer::intValue)
        .sum();
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
    return normalizationChanges()
        + changesByRule.values().stream().mapToInt(Integer::intValue).sum();
  }
}
