package org.stanb.epubrepair.repair;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.jupiter.api.Test;
import org.stanb.epubrepair.rules.WrapOrphanTextRule;

final class RepairReportTest {

  @Test
  void aggregatesChangesByRuleAndKeepsZeroCountRules() {
    RepairRule rule = new WrapOrphanTextRule();
    RepairReport report = new RepairReport(List.of(rule));

    RepairContext context =
        new RepairContext(Path.of("chapter.xhtml"), new Document(new Element("html")));
    context.recordChange(rule.id());
    context.recordChange(rule.id());

    report.recordProcessedFile(context);

    assertEquals(1, report.filesProcessed());
    assertEquals(2, report.changesMade());
    assertEquals(2, report.changesByRule().get(rule.id()));
  }

  @Test
  void reportsZeroForExecutedRuleWithNoChanges() {
    RepairRule rule = new WrapOrphanTextRule();
    RepairReport report = new RepairReport(List.of(rule));

    assertEquals(0, report.changesByRule().get(rule.id()));
  }
}
