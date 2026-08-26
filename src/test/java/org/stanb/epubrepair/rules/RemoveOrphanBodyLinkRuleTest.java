package org.stanb.epubrepair.rules;

import java.io.StringReader;
import java.nio.file.Path;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.stanb.epubrepair.repair.RepairContext;

final class RemoveOrphanBodyLinkRuleTest {

  @Test
  void removesDirectBodyLevelLink() throws Exception {
    Document document = parse("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>
            <p>Before</p>
            <a href="chapter2.xhtml">Next chapter 1</a>
            <p>After</p>
          </body>
        </html>
        """);
    RepairContext context = contextFor(document);

    new RemoveOrphanBodyLinkRule().apply(context);

    Element body = bodyOf(document);

    assertEquals(2, body.getChildren("p", body.getNamespace()).size());
    assertEquals(
        0,
        body.getChildren("a", body.getNamespace()).size());
    assertEquals(1, context.changesFor(RemoveOrphanBodyLinkRule.ID));
  }

  @Test
  void removesMultipleDirectBodyLevelLinks() throws Exception {
    Document document = parse("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>
            <a href="chapter2.xhtml">Next chapter 2</a>
            <p>Content</p>
            <a href="chapter3.xhtml">Chapter 3</a>
          </body>
        </html>
        """);
    RepairContext context = contextFor(document);

    new RemoveOrphanBodyLinkRule().apply(context);

    Element body = bodyOf(document);

    assertEquals(
        0,
        body.getChildren("a", body.getNamespace()).size());
    assertEquals(1, body.getChildren("p", body.getNamespace()).size());
    assertEquals(2, context.changesFor(RemoveOrphanBodyLinkRule.ID));
  }

  @Test
  void preservesNestedLinks() throws Exception {
    Document document = parse("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>
            <p>
              <a href="chapter2.xhtml">Next chapter 2</a>
            </p>
            <div>
              <a href="chapter3.xhtml">Chapter 3</a>
            </div>
          </body>
        </html>
        """);
    RepairContext context = contextFor(document);

    new RemoveOrphanBodyLinkRule().apply(context);

    Element body = bodyOf(document);

    assertEquals(0, context.totalChanges());
    assertEquals(
        1,
        body.getChild("p", body.getNamespace())
            .getChildren("a", body.getNamespace())
            .size());
    assertEquals(
        1,
        body.getChild("div", body.getNamespace())
            .getChildren("a", body.getNamespace())
            .size());
  }

  @Test
  void doesNothingWhenThereAreNoDirectBodyLevelLinks() throws Exception {
    Document document = parse("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>
            <p>Content</p>
          </body>
        </html>
        """);
    RepairContext context = contextFor(document);

    new RemoveOrphanBodyLinkRule().apply(context);

    assertEquals(0, context.totalChanges());
  }

  @Test
  void isIdempotent() throws Exception {
    Document document = parse("""
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>
            <a href="chapter2.xhtml">Next chapter 3</a>
            <p>Content</p>
          </body>
        </html>
        """);
    RemoveOrphanBodyLinkRule rule = new RemoveOrphanBodyLinkRule();

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