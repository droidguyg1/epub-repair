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

  @Test
  // Regression test for a real EPUB where orphan text containing inline
  // formatting was separated by empty paragraphs inserted by Calibre.
  void repairsRealWorldOrphanTextWithInlineFormatting() throws Exception {
    Path file = temporaryDirectory.resolve("chapter.xhtml");

    Files.writeString(file, """
      <?xml version="1.0" encoding="UTF-8"?>
      <html xmlns="http://www.w3.org/1999/xhtml">
        <head><title>Test</title></head>
        <body>
          Did the castle <i class="calibre7">help</i> Filipe?
          <p class="calibre1"
            style="margin:0pt; border:0pt; height:0pt">\u00A0</p>
          <p class="calibre1"
            style="margin:0pt; border:0pt; height:1em">\u00A0</p>
          'Is everything alright, Ken?'
          <p class="calibre1"
            style="margin:0pt; border:0pt; height:0pt">\u00A0</p>
        </body>
      </html>
      """, StandardCharsets.UTF_8);

    XhtmlRepair repair = new XhtmlRepair();

    RepairContext firstRun = repair.process(file);

    assertEquals(5, firstRun.totalChanges());
    assertEquals(2, firstRun.changesFor(WrapOrphanTextRule.ID));
    assertEquals(3, firstRun.changesFor(RemoveEmptyParagraphRule.ID));
    assertEquals(0, firstRun.changesFor(RemoveParagraphHeightRule.ID));

    String repaired = Files.readString(file);

    Document document =
        new SAXBuilder().build(new StringReader(repaired));

    Element root = document.getRootElement();
    Namespace namespace = root.getNamespace();
    Element body = root.getChild("body", namespace);

    assertEquals(2, body.getChildren("p", namespace).size());

    Element firstParagraph = body.getChildren("p", namespace).get(0);
    Element secondParagraph = body.getChildren("p", namespace).get(1);

    assertEquals("Did the castle Filipe?", firstParagraph.getTextNormalize());
    assertEquals(
        "help",
        firstParagraph.getChild("i", namespace).getText());

    assertEquals(
        "'Is everything alright, Ken?'",
        secondParagraph.getTextNormalize());

    assertEquals(
        1,
        firstParagraph.getChildren("i", namespace).size());

    assertEquals(
        "help",
        firstParagraph
            .getChildren("i", namespace)
            .get(0)
            .getText());

    RepairContext secondRun = repair.process(file);

    assertEquals(0, secondRun.totalChanges());
    assertEquals(repaired, Files.readString(file));
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

  private String getAllText(Element element) {
    return element.getText()
        + element.getChildren().stream()
            .map(this::getAllText)
            .collect(java.util.stream.Collectors.joining());
  }
}
