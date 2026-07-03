package org.stanb.epubrepair.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringReader;
import java.nio.file.Path;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
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
}
