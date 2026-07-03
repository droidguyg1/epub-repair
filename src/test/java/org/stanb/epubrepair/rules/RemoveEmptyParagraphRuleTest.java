package org.stanb.epubrepair.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringReader;
import java.nio.file.Path;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.jupiter.api.Test;
import org.stanb.epubrepair.repair.RepairContext;

final class RemoveEmptyParagraphRuleTest {

  @Test
  void removesEmptyAndWhitespaceOnlyParagraphs() throws Exception {
    Document document = parse("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>
            <p></p>
            <p>   </p>
            <p>
            </p>
            <p>Keep me</p>
          </body>
        </html>
        """);
    RepairContext context = contextFor(document);

    new RemoveEmptyParagraphRule().apply(context);

    Element body = bodyOf(document);
    assertEquals(1, body.getChildren("p", body.getNamespace()).size());
    assertEquals("Keep me", body.getChild("p", body.getNamespace()).getText());
    assertEquals(3, context.changesFor(RemoveEmptyParagraphRule.ID));
  }

  @Test
  void preservesParagraphsContainingChildElements() throws Exception {
    Document document = parse("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>
            <p><img src="separator.png" alt="" /></p>
            <p><span></span></p>
          </body>
        </html>
        """);
    RepairContext context = contextFor(document);

    new RemoveEmptyParagraphRule().apply(context);

    assertEquals(2, bodyOf(document).getChildren("p", bodyOf(document).getNamespace()).size());
    assertEquals(0, context.totalChanges());
  }

  @Test
  void isIdempotent() throws Exception {
    Document document = parse("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body><p></p><p>Keep me</p></body>
        </html>
        """);
    RemoveEmptyParagraphRule rule = new RemoveEmptyParagraphRule();

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

  private Element bodyOf(Document document) {
    return document.getRootElement()
        .getChild("body", document.getRootElement().getNamespace());
  }
}
