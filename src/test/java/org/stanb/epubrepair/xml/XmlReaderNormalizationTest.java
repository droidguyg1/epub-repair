package org.stanb.epubrepair.xml;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jdom2.Document;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class XmlReaderNormalizationTest {

  @TempDir
  Path temporaryDirectory;

  @Test
  void parsesXhtmlContainingUnclosedEmptyElements() throws Exception {
    Path file = temporaryDirectory.resolve("chapter.html");
    Files.writeString(file, """
        <?xml version="1.0" encoding="UTF-8"?>
        <html xmlns="http://www.w3.org/1999/xhtml">
          <head>
            <title>Test</title>
            <link rel="stylesheet" href="stylesheet.css">
          </head>
          <body>
            <p>Before<br>after</p>
            <img src="separator.png" alt="">
          </body>
        </html>
        """, StandardCharsets.UTF_8);

    Document document = new XmlReader().read(file).document();

    assertEquals("html", document.getRootElement().getName());
  }
}
