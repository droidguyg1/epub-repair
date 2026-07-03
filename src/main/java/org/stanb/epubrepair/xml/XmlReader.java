package org.stanb.epubrepair.xml;

import java.io.IOException;
import java.nio.file.Path;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Reads EPUB XHTML documents into a JDOM {@link Document}.
 *
 * <p>This class is responsible only for parsing XML. It does not perform
 * validation or document repair.
 */
public final class XmlReader {

  private final SAXBuilder saxBuilder;

  /** Creates a new XML reader using the project's default parser configuration. */
  public XmlReader() {
    saxBuilder = new SAXBuilder();
  }

  public Document read(Path path) throws IOException, JDOMException {
    return saxBuilder.build(path.toFile());
  }
}
