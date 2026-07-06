package org.stanb.epubrepair.xml;

import java.util.Map;
import org.jdom2.Document;

public record XmlReadResult(
    Document document,
    Map<String, Integer> changesByNormalizer) {

  public XmlReadResult {
    changesByNormalizer = Map.copyOf(changesByNormalizer);
  }
}
