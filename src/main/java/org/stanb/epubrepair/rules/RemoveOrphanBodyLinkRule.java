package org.stanb.epubrepair.rules;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.stanb.epubrepair.repair.RepairContext;
import org.stanb.epubrepair.repair.RepairRule;

/**
 * Removes XHTML anchor elements that are direct children of XHTML body
 * elements.
 *
 * <p>Anchors nested inside other elements are preserved.
 */
public final class RemoveOrphanBodyLinkRule implements RepairRule {

  public static final String ID = "remove-orphan-body-link";

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

    bodies.forEach(body -> removeDirectLinks(body, context));
  }

  private void removeDirectLinks(Element body, RepairContext context) {
    List<Element> links = new ArrayList<>(
        body.getChildren("a", body.getNamespace()));

    for (Element link : links) {
        String target = link.getAttributeValue("href");

        System.out.println(
            context.path()
                + ": removed orphan body-level link: "
                + link.getTextNormalize()
                + " -> "
                + target);

      link.detach();
      context.recordChange(ID);
    }
  }
}