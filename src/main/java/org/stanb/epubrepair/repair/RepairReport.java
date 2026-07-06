package org.stanb.epubrepair.repair;

import java.util.LinkedHashMap;
import java.util.Map;

/** Records processing results for one application run. */
public final class RepairReport {

  private int filesProcessed;
  private int filesFailed;
  private final Map<String, Integer> changesByNormalizer = new LinkedHashMap<>();
  private final Map<String, Integer> changesByRule = new LinkedHashMap<>();

  public RepairReport(Iterable<? extends RepairRule> rules) {
    rules.forEach(rule -> changesByRule.put(rule.id(), 0));
  }

  public void recordProcessedFile(RepairContext context) {
    filesProcessed++;

    context.changesByNormalizer().forEach(
        (normalizerId, changes) ->
            changesByNormalizer.merge(normalizerId, changes, Integer::sum));

    context.changesByRule().forEach(
        (ruleId, changes) ->
            changesByRule.merge(ruleId, changes, Integer::sum));
  }

  public void recordFailedFile() {
    filesFailed++;
  }

  public int filesProcessed() {
    return filesProcessed;
  }

  public int filesFailed() {
    return filesFailed;
  }

  public int changesMade() {
    return changesByNormalizer.values().stream()
            .mapToInt(Integer::intValue)
            .sum()
        + changesByRule.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
  }

  public Map<String, Integer> changesByNormalizer() {
    return Map.copyOf(changesByNormalizer);
  }

  public Map<String, Integer> changesByRule() {
    return Map.copyOf(changesByRule);
  }

  public boolean hasFailures() {
    return filesFailed > 0;
  }
}
