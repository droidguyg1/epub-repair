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
 * Wraps non-whitespace text nodes that are direct children of XHTML body
 * elements in paragraph elements.
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

    for (Content child : children) {
      if (child instanceof Text text && !text.getText().isBlank()) {
        int index = body.indexOf(text);
        text.detach();

        Element paragraph = new Element("p", body.getNamespace());
        paragraph.addContent(text);
        body.addContent(index, paragraph);

        context.recordChange(ID);
      }
    }
  }
}
