package org.stanb.epubrepair.rules;

import java.io.StringReader;
import java.nio.file.Path;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;
import org.stanb.epubrepair.repair.RepairContext;

final class WrapOrphanTextRuleTest {

  @Test
  void wrapsDirectBodyTextInParagraph() throws Exception {
    Document document = new SAXBuilder().build(new StringReader("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>Orphan text<p>Existing paragraph</p></body>
        </html>
        """));
    RepairContext context = new RepairContext(Path.of("chapter.xhtml"), document);

    new WrapOrphanTextRule().apply(context);

    var body = document.getRootElement()
        .getChild("body", document.getRootElement().getNamespace());
    assertEquals("p", body.getChildren().get(0).getName());
    assertEquals("Orphan text", body.getChildren().get(0).getText());
    assertEquals(1, context.changesFor(WrapOrphanTextRule.ID));
  }

  @Test
  void leavesWhitespaceAndNestedTextUnchanged() throws Exception {
    Document document = new SAXBuilder().build(new StringReader("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>
            <div>Nested text</div>
          </body>
        </html>
        """));
    RepairContext context = new RepairContext(Path.of("chapter.xhtml"), document);

    new WrapOrphanTextRule().apply(context);

    assertEquals(0, context.totalChanges());
  }

  @Test
  void wrapsInlineElementsIntoSingleParagraph() throws Exception {
    Document document = new SAXBuilder().build(new StringReader("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>Did the castle <i>help</i> Filipe?</body>
        </html>
        """));

    RepairContext context =
        new RepairContext(Path.of("chapter.xhtml"), document);

    new WrapOrphanTextRule().apply(context);

    Element body = document.getRootElement()
        .getChild("body", document.getRootElement().getNamespace());

    assertEquals(1, body.getChildren().size());

    Element paragraph = body.getChild("p", body.getNamespace());

    assertEquals(3, paragraph.getContent().size());

    assertInstanceOf(Text.class, paragraph.getContent().get(0));
    assertInstanceOf(Element.class, paragraph.getContent().get(1));
    assertInstanceOf(Text.class, paragraph.getContent().get(2));

    Element emphasis = (Element) paragraph.getContent().get(1);

    assertEquals("i", emphasis.getName());
    assertEquals("help", emphasis.getText());

    assertEquals(1, context.changesFor(WrapOrphanTextRule.ID));
  }

  @Test
  void wrapsMultipleInlineElementsIntoSingleParagraph() throws Exception {
    Document document = new SAXBuilder().build(new StringReader("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body><b>Bold</b> and <i>italic</i>.</body>
        </html>
        """));

    RepairContext context =
        new RepairContext(Path.of("chapter.xhtml"), document);

    new WrapOrphanTextRule().apply(context);

    Element body = document.getRootElement()
        .getChild("body", document.getRootElement().getNamespace());

    assertEquals(1, body.getChildren().size());

    Element paragraph = body.getChild("p", body.getNamespace());

    assertEquals(4, paragraph.getContent().stream()
        .filter(Content.class::isInstance)
        .count());

    assertEquals(1, context.changesFor(WrapOrphanTextRule.ID));
  }

  @Test
  void wrapsLeadingInlineElementIntoParagraph() throws Exception {
    Document document = new SAXBuilder().build(new StringReader("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body><i>Prologue</i> begins here.</body>
        </html>
        """));

    RepairContext context =
        new RepairContext(Path.of("chapter.xhtml"), document);

    new WrapOrphanTextRule().apply(context);

    Element body = document.getRootElement()
        .getChild("body", document.getRootElement().getNamespace());

    Element paragraph = body.getChild("p", body.getNamespace());

    assertEquals("i",
        ((Element) paragraph.getContent().get(0)).getName());

    assertEquals(1, context.changesFor(WrapOrphanTextRule.ID));
  }

  @Test
  void wrapsTrailingInlineElementIntoParagraph() throws Exception {
    Document document = new SAXBuilder().build(new StringReader("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>See <i>Appendix</i></body>
        </html>
        """));

    RepairContext context =
        new RepairContext(Path.of("chapter.xhtml"), document);

    new WrapOrphanTextRule().apply(context);

    Element body = document.getRootElement()
        .getChild("body", document.getRootElement().getNamespace());

    Element paragraph = body.getChild("p", body.getNamespace());

    Element emphasis =
        (Element) paragraph.getContent().get(1);

    assertEquals("i", emphasis.getName());

    assertEquals(1, context.changesFor(WrapOrphanTextRule.ID));
  }
}
