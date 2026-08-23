package org.stanb.epubrepair.rules;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Text;
import org.jdom2.filter.ElementFilter;
import org.stanb.epubrepair.repair.RepairContext;
import org.stanb.epubrepair.repair.RepairRule;

/**
 * Wraps orphan body content in paragraph elements.
 *
 * <p>Direct children of {@code <body>} that are phrasing content are grouped
 * into paragraph elements. Existing block-level elements are preserved
 * unchanged.
 */
public final class WrapOrphanTextRule implements RepairRule {

  public static final String ID = "wrap-orphan-text";

  @Override
  public String id() {
    return ID;
  }

  @Override
  public void apply(RepairContext context) {
    Namespace namespace = context.document().getRootElement().getNamespace();

    List<Element> bodies = new ArrayList<>();

    context.document()
        .getRootElement()
        .getDescendants(new ElementFilter("body", namespace))
        .forEach(bodies::add);

    bodies.forEach(body -> wrapOrphanText(body, context));
  }

  private void wrapOrphanText(Element body, RepairContext context) {
    List<Content> children = new ArrayList<>(body.getContent());

    List<Content> paragraphContent = new ArrayList<>();

    for (Content child : children) {
      if (isParagraphBoundary(child)) {
        flushParagraph(body, paragraphContent, context);
      } else {
        paragraphContent.add(child);
      }
    }

    flushParagraph(body, paragraphContent, context);
  }

  private void flushParagraph(
      Element body,
      List<Content> paragraphContent,
      RepairContext context) {

    if (!containsMeaningfulContent(paragraphContent)) {
      paragraphContent.clear();
      return;
    }

    int insertIndex = body.indexOf(paragraphContent.get(0));

    Element paragraph = new Element("p", body.getNamespace());

    for (Content content : List.copyOf(paragraphContent)) {
      content.detach();
      paragraph.addContent(content);
    }

    body.addContent(insertIndex, paragraph);

    context.recordChange(ID);

    paragraphContent.clear();
  }

  private boolean containsMeaningfulContent(List<Content> content) {
    for (Content node : content) {
      if (node instanceof Text text) {
        if (!text.getText().isBlank()) {
          return true;
        }
      } else {
        return true;
      }
    }

    return false;
  }

  private boolean isParagraphBoundary(Content content) {
    return !isPhrasingContent(content);
  }

  private boolean isPhrasingContent(Content content) {
    if (content instanceof Text) {
      return true;
    }

    if (content instanceof Element element) {
      return isPhrasingElement(element);
    }

    return false;
  }

  private boolean isPhrasingElement(Element element) {
    return switch (element.getName()) {
      case "a",
          "abbr",
          "acronym",
          "b",
          "big",
          "br",
          "cite",
          "code",
          "dfn",
          "em",
          "i",
          "img",
          "kbd",
          "label",
          "q",
          "samp",
          "small",
          "span",
          "strong",
          "sub",
          "sup",
          "tt",
          "var" -> true;

      default -> false;
    };
  }
}