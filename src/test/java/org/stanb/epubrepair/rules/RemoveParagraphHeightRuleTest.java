package org.stanb.epubrepair.rules;

import java.io.StringReader;
import java.nio.file.Path;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.stanb.epubrepair.repair.RepairContext;

final class RemoveParagraphHeightRuleTest {

  @Test
  void removesStyleAttributeWhenHeightIsTheOnlyDeclaration() throws Exception {
    Document document = parse("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body><p style="height: 24px;">Text</p></body>
        </html>
        """);
    RepairContext context = contextFor(document);

    new RemoveParagraphHeightRule().apply(context);

    assertNull(paragraphOf(document).getAttribute("style"));
    assertEquals(1, context.changesFor(RemoveParagraphHeightRule.ID));
  }

  @Test
  void removesHeightAndPreservesOtherDeclarations() throws Exception {
    Document document = parse("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>
            <p style="height: 24px; text-align: center; margin-top: 1em;">Text</p>
          </body>
        </html>
        """);
    RepairContext context = contextFor(document);

    new RemoveParagraphHeightRule().apply(context);

    assertEquals(
        "text-align: center; margin-top: 1em;",
        paragraphOf(document).getAttributeValue("style"));
    assertEquals(1, context.totalChanges());
  }

  @Test
  void leavesLineHeightAndNonParagraphHeightUntouched() throws Exception {
    Document document = parse("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>
            <p style="line-height: 1.4;">Text</p>
            <div style="height: 24px;">Text</div>
          </body>
        </html>
        """);
    RepairContext context = contextFor(document);

    new RemoveParagraphHeightRule().apply(context);

    assertEquals("line-height: 1.4;", paragraphOf(document).getAttributeValue("style"));
    assertEquals(0, context.totalChanges());
  }

  @Test
  void isIdempotent() throws Exception {
    Document document = parse("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body><p style="height: 24px; color: red;">Text</p></body>
        </html>
        """);
    RemoveParagraphHeightRule rule = new RemoveParagraphHeightRule();

    RepairContext firstRun = contextFor(document);
    rule.apply(firstRun);
    RepairContext secondRun = contextFor(document);
    rule.apply(secondRun);

    assertEquals(1, firstRun.totalChanges());
    assertEquals(0, secondRun.totalChanges());
  }

  private Document parse(String xml) throws Exception {
    return new SAXBuilder().build(new StringReader(xml));
  }

  private RepairContext contextFor(Document document) {
    return new RepairContext(Path.of("chapter.xhtml"), document);
  }

  private Element paragraphOf(Document document) {
    Element body = document.getRootElement()
        .getChild("body", document.getRootElement().getNamespace());
    return body.getChild("p", body.getNamespace());
  }
}
