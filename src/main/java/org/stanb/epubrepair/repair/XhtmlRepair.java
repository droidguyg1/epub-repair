package org.stanb.epubrepair.repair;

import java.io.IOException;
import java.nio.file.Path;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.stanb.epubrepair.xml.XmlReader;
import org.stanb.epubrepair.xml.XmlWriter;

/** Coordinates reading and writing a single XHTML document. */
public final class XhtmlRepair {

  private final XmlReader xmlReader = new XmlReader();
  private final XmlWriter xmlWriter = new XmlWriter();

  public void process(Path path) throws IOException, JDOMException {
    Document document = xmlReader.read(path);
    xmlWriter.write(document, path);
  }
}
