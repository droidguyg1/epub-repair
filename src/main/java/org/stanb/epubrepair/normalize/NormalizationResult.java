package org.stanb.epubrepair.normalize;

import java.util.Map;

public record NormalizationResult(
    String source,
    Map<String, Integer> changesByNormalizer) {

  public NormalizationResult {
    changesByNormalizer = Map.copyOf(changesByNormalizer);
  }

  public int totalChanges() {
    return changesByNormalizer.values().stream()
        .mapToInt(Integer::intValue)
        .sum();
  }
}
