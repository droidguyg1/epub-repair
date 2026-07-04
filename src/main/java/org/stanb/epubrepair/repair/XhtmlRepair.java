package org.stanb.epubrepair.repair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.stanb.epubrepair.rules.RemoveEmptyParagraphRule;
import org.stanb.epubrepair.rules.RemoveParagraphHeightRule;
import org.stanb.epubrepair.rules.WrapOrphanTextRule;
import org.stanb.epubrepair.xml.XmlReader;
import org.stanb.epubrepair.xml.XmlWriter;

/** Coordinates repair of a single XHTML document. */
public final class XhtmlRepair {

  private final XmlReader xmlReader = new XmlReader();
  private final XmlWriter xmlWriter = new XmlWriter();
  private final List<RepairRule> rules = List.of(
      new WrapOrphanTextRule(),
      new RemoveEmptyParagraphRule(),
      new RemoveParagraphHeightRule());
  private final RepairEngine repairEngine = new RepairEngine(rules);
  
  public List<RepairRule> rules() {
    return rules;
  }

  public RepairContext process(Path path) throws IOException, JDOMException {
    Document document = xmlReader.read(path);
    RepairContext context = new RepairContext(path, document);

    repairEngine.applyRules(context);

    if (context.totalChanges() > 0) {
      xmlWriter.write(document, path);
    }

    return context;
  }
}
