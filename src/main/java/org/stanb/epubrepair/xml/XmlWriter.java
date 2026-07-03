package org.stanb.epubrepair.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/** Writes JDOM documents as EPUB XHTML without pretty-printing them. */
public final class XmlWriter {

  private final XMLOutputter outputter;

  /** Creates an XML writer using raw XML output formatting. */
  public XmlWriter() {
    Format format = Format.getPrettyFormat();
    format.setIndent("  ");
    format.setLineSeparator("\n");

    outputter = new XMLOutputter(format);
  }

  public void write(Document document, Path path) throws IOException {
    try (OutputStream outputStream = Files.newOutputStream(path)) {
      outputter.output(document, outputStream);
    }
  }
}
