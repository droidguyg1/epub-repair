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

final class RemoveAriaHiddenAttributeRuleTest {

  @Test
  void removesAriaHiddenAttribute() throws Exception {
    Document document = parse("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>
            <div aria-hidden="true">Hidden content</div>
          </body>
        </html>
        """);
    RepairContext context = contextFor(document);

    new RemoveAriaHiddenAttributeRule().apply(context);

    Element div = bodyOf(document).getChild("div", document.getRootElement().getNamespace());

    assertNull(div.getAttribute("aria-hidden"));
    assertEquals("Hidden content", div.getText());
    assertEquals(1, context.changesFor(RemoveAriaHiddenAttributeRule.ID));
  }

  @Test
  void preservesOtherAttributesAndContent() throws Exception {
    Document document = parse("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>
            <div class="calibre1" aria-hidden="true">
              <img src="separator.png" alt="" class="calibre6" />
            </div>
          </body>
        </html>
        """);
    RepairContext context = contextFor(document);

    new RemoveAriaHiddenAttributeRule().apply(context);

    Element div = bodyOf(document).getChild("div", document.getRootElement().getNamespace());
    Element img = div.getChild("img", document.getRootElement().getNamespace());

    assertNull(div.getAttribute("aria-hidden"));
    assertEquals("calibre1", div.getAttributeValue("class"));
    assertEquals("separator.png", img.getAttributeValue("src"));
    assertEquals("", img.getAttributeValue("alt"));
    assertEquals("calibre6", img.getAttributeValue("class"));
    assertEquals(1, context.changesFor(RemoveAriaHiddenAttributeRule.ID));
  }

  @Test
  void removesMultipleAriaHiddenAttributes() throws Exception {
    Document document = parse("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>
            <div aria-hidden="true">First</div>
            <span aria-hidden="false">Second</span>
            <p>Keep me</p>
          </body>
        </html>
        """);
    RepairContext context = contextFor(document);

    new RemoveAriaHiddenAttributeRule().apply(context);

    Element body = bodyOf(document);
    Element div = body.getChild("div", document.getRootElement().getNamespace());
    Element span = body.getChild("span", document.getRootElement().getNamespace());

    assertNull(div.getAttribute("aria-hidden"));
    assertNull(span.getAttribute("aria-hidden"));
    assertEquals("Keep me", body.getChild("p", document.getRootElement().getNamespace()).getText());
    assertEquals(2, context.changesFor(RemoveAriaHiddenAttributeRule.ID));
  }

  @Test
  void isIdempotent() throws Exception {
    Document document = parse("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>
            <div aria-hidden="true">Content</div>
          </body>
        </html>
        """);
    RemoveAriaHiddenAttributeRule rule = new RemoveAriaHiddenAttributeRule();

    RepairContext firstRun = contextFor(document);
    rule.apply(firstRun);

    RepairContext secondRun = contextFor(document);
    rule.apply(secondRun);

    assertEquals(1, firstRun.totalChanges());
    assertEquals(0, secondRun.totalChanges());
  }

  // private

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

