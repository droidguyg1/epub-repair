package org.stanb.epubrepair.repair;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.stanb.epubrepair.rules.RemoveEmptyParagraphRule;
import org.stanb.epubrepair.rules.RemoveParagraphHeightRule;
import org.stanb.epubrepair.rules.WrapOrphanTextRule;


final class XhtmlRepairTest {

  @TempDir
  Path temporaryDirectory;

  @Test
  void appliesAllRulesAndIsIdempotent() throws Exception {
    Path file = temporaryDirectory.resolve("chapter.xhtml");
    Files.writeString(file, """
        <?xml version="1.0" encoding="UTF-8"?>
        <html xmlns="http://www.w3.org/1999/xhtml">
          <head><title>Test</title></head>
          <body>
            Orphan text
            <p></p>
            <p style="height: 24px; text-align: center;">Existing paragraph</p>
          </body>
        </html>
        """, StandardCharsets.UTF_8);

    XhtmlRepair repair = new XhtmlRepair();

    RepairContext firstRun = repair.process(file);
    assertEquals(3, firstRun.totalChanges());
    assertEquals(1, firstRun.changesFor(WrapOrphanTextRule.ID));
    assertEquals(1, firstRun.changesFor(RemoveEmptyParagraphRule.ID));
    assertEquals(1, firstRun.changesFor(RemoveParagraphHeightRule.ID));

    String afterFirstRun = Files.readString(file);
    assertEquals("Orphan text", getParagraphTextNormalized(afterFirstRun, 0));
    assertEquals("Existing paragraph", getParagraphTextNormalized(afterFirstRun, 1));
    assertFalse(afterFirstRun.contains("<p />"));
    assertTrue(afterFirstRun.contains("style=\"text-align: center;\""));
    assertFalse(afterFirstRun.contains("height: 24px"));

    RepairContext secondRun = repair.process(file);
    assertEquals(0, secondRun.totalChanges());
    assertEquals(afterFirstRun, Files.readString(file));
  }

  private String getParagraphTextNormalized(String afterFirstRun, int paragraphNumber) throws Exception {
    Document repairedDocument =
    new SAXBuilder().build(new StringReader(afterFirstRun));

    Element root = repairedDocument.getRootElement();
    Namespace namespace = root.getNamespace();
    Element body = root.getChild("body", namespace);
    Element firstParagraph = body.getChildren("p", namespace).get(paragraphNumber);
    return firstParagraph.getTextNormalize();
  }
}
