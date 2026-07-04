package org.stanb.epubrepair.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.stanb.epubrepair.repair.RepairContext;
import org.stanb.epubrepair.repair.RepairRule;

/** Removes inline CSS height declarations from XHTML paragraph elements. */
public final class RemoveParagraphHeightRule implements RepairRule {

  public static final String ID = "remove-paragraph-height";

  private static final Pattern HEIGHT_PROPERTY =
      Pattern.compile("(?i)^\\s*height\\s*:.*$");

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

    paragraphs.forEach(paragraph -> removeHeight(paragraph, context));
  }

  private void removeHeight(Element paragraph, RepairContext context) {
    String style = paragraph.getAttributeValue("style");

    if (style == null) {
      return;
    }

    List<String> declarations = splitDeclarations(style);
    boolean removed = declarations.removeIf(this::isHeightDeclaration);

    if (!removed) {
      return;
    }

    String repairedStyle = String.join("; ", declarations);

    if (repairedStyle.isBlank()) {
      paragraph.removeAttribute("style");
    } else {
      paragraph.setAttribute("style", repairedStyle + ";");
    }

    context.recordChange(ID);
  }

  private List<String> splitDeclarations(String style) {
    List<String> declarations = new ArrayList<>();

    for (String declaration : style.split(";")) {
      String trimmedDeclaration = declaration.trim();

      if (!trimmedDeclaration.isEmpty()) {
        declarations.add(trimmedDeclaration);
      }
    }

    return declarations;
  }

  private boolean isHeightDeclaration(String declaration) {
    return HEIGHT_PROPERTY.matcher(declaration).matches();
  }
}
