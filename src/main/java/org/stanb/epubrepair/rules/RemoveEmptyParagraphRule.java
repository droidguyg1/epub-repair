package org.stanb.epubrepair.rules;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.stanb.epubrepair.repair.RepairContext;
import org.stanb.epubrepair.repair.RepairRule;

/**
 * Removes XHTML paragraph elements that contain no meaningful content.
 *
 * <p>A paragraph is empty when it contains no non-whitespace text and no child
 * elements. Paragraphs containing elements such as images are preserved.
 */
public final class RemoveEmptyParagraphRule implements RepairRule {

  public static final String ID = "remove-empty-paragraph";

  @Override
  public String id() {
    return ID;
  }

  @Override
  public void apply(RepairContext context) {
    Namespace namespace = context.document().getRootElement().getNamespace();
    List<Element> paragraphs = new ArrayList<>();

    context.document()
        .getRootElement()
        .getDescendants(new ElementFilter("p", namespace))
        .forEach(paragraphs::add);

    paragraphs.stream()
        .filter(this::isEmpty)
        .forEach(paragraph -> remove(paragraph, context));
  }

  private boolean isEmpty(Element paragraph) {
    return paragraph.getChildren().isEmpty()
        && paragraph.getText().isBlank();
  }

  private void remove(Element paragraph, RepairContext context) {
    paragraph.detach();
    context.recordChange(ID);
  }
}
