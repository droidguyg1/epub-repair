package org.stanb.epubrepair.normalize;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NormalizationPipeline {

  private final List<PreParseNormalizer> normalizers;

  public NormalizationPipeline(List<PreParseNormalizer> normalizers) {
    this.normalizers = List.copyOf(normalizers);
  }

  public NormalizationResult normalize(String source) {
    String normalized = source;
    Map<String, Integer> counts = new LinkedHashMap<>();

    for (PreParseNormalizer normalizer : normalizers) {
      String next = normalizer.normalize(normalized);
      counts.put(normalizer.id(), next.equals(normalized) ? 0 : 1);
      normalized = next;
    }

    return new NormalizationResult(normalized, counts);
  }
}
