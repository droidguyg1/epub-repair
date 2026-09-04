package org.stanb.epubrepair.rules;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.stanb.epubrepair.repair.RepairContext;
import org.stanb.epubrepair.repair.RepairRule;

/** Removes the unsupported XHTML {@code aria-hidden} attribute. */
public final class RemoveAriaHiddenAttributeRule implements RepairRule {

  public static final String ID = "remove-aria-hidden-attribute";

  private static final String ARIA_HIDDEN = "aria-hidden";

  @Override
  public String id() {
    return ID;
  }

  @Override
  public void apply(RepairContext context) {
    Namespace namespace = context.document().getRootElement().getNamespace();
    List<Element> elements = new ArrayList<>();

    context.document()
        .getRootElement()
        .getDescendants(new ElementFilter(namespace))
        .forEach(elements::add);

    elements.forEach(element -> removeAriaHidden(element, context));
  }

  private void removeAriaHidden(Element element, RepairContext context) {
    if (element.getAttribute(ARIA_HIDDEN) == null) {
      return;
    }

    element.removeAttribute(ARIA_HIDDEN);
    context.recordChange(ID);
  }
}