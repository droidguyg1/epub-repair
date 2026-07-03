package org.stanb.epubrepair.repair;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.stanb.epubrepair.rules.RemoveEmptyParagraphRule;
import org.stanb.epubrepair.rules.WrapOrphanTextRule;

final class XhtmlRepairTest {

  @TempDir
  Path temporaryDirectory;

  @Test
  void appliesAllRulesAndIsIdempotent() throws Exception {
    Path file = temporaryDirectory.resolve("chapter.xhtml");
    Files.writeString(file, """
        <?xml version="1.0" encoding="UTF-8"?>
        <html xmlns="http://www.w3.org/1999/xhtml">
          <head><title>Test</title></head>
          <body>Orphan text<p></p><p>Existing paragraph</p></body>
        </html>
        """, StandardCharsets.UTF_8);

    XhtmlRepair repair = new XhtmlRepair();

    RepairContext firstRun = repair.process(file);
    assertEquals(2, firstRun.totalChanges());
    assertEquals(1, firstRun.changesFor(WrapOrphanTextRule.ID));
    assertEquals(1, firstRun.changesFor(RemoveEmptyParagraphRule.ID));

    String afterFirstRun = Files.readString(file);
    assertTrue(afterFirstRun.contains("<p>Orphan text</p>"));
    assertTrue(afterFirstRun.contains("<p>Existing paragraph</p>"));
    assertFalse(afterFirstRun.contains("<p />"));

    RepairContext secondRun = repair.process(file);
    assertEquals(0, secondRun.totalChanges());
    assertEquals(afterFirstRun, Files.readString(file));
  }
}
