package org.stanb.epubrepair.xml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class XmlRoundTripTest {

  @TempDir
  Path temporaryDirectory;

  @Test
  void readsAndWritesNamespacedXhtml() throws IOException, JDOMException {
    Path file = temporaryDirectory.resolve("chapter.xhtml");
    Files.writeString(file, """
        <?xml version="1.0" encoding="UTF-8"?>
        <html xmlns="http://www.w3.org/1999/xhtml">
          <head><title>Test</title></head>
          <body><p>Hello.</p><img src="separator.png" alt="" /></body>
        </html>
        """, StandardCharsets.UTF_8);

    XmlReader reader = new XmlReader();
    new XmlWriter().write(reader.read(file).document(), file);
    Document document = reader.read(file).document();

    assertEquals("http://www.w3.org/1999/xhtml",
        document.getRootElement().getNamespaceURI());
  }
}
