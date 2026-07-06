package org.stanb.epubrepair.xml;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.stanb.epubrepair.normalize.NormalizationPipeline;
import org.stanb.epubrepair.normalize.NormalizationResult;
import org.stanb.epubrepair.normalize.SelfCloseElementNormalizer;

public final class XmlReader {

  private final SAXBuilder saxBuilder = new SAXBuilder();
  private final NormalizationPipeline normalizationPipeline =
      new NormalizationPipeline(List.of(new SelfCloseElementNormalizer()));

  public XmlReadResult read(Path path) throws IOException, JDOMException {
    String source = Files.readString(path, StandardCharsets.UTF_8);
    NormalizationResult result = normalizationPipeline.normalize(source);
    Document document = saxBuilder.build(new StringReader(result.source()));
    return new XmlReadResult(document, result.changesByNormalizer());
  }
}
