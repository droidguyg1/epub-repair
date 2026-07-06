package org.stanb.epubrepair.normalize;

public interface PreParseNormalizer {
  String id();
  String normalize(String source);
}
