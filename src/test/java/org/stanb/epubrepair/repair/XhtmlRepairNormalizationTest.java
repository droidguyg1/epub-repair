package org.stanb.epubrepair.repair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class XhtmlRepairNormalizationTest {

  @TempDir
  Path temporaryDirectory;

  @Test
  void persistsNormalizationWhenNoRepairRuleChangesTheDocument() throws Exception {
    Path file = temporaryDirectory.resolve("chapter.html");
    Files.writeString(file, """
        <?xml version="1.0" encoding="UTF-8"?>
        <html xmlns="http://www.w3.org/1999/xhtml">
          <head><link rel="stylesheet" href="stylesheet.css"></head>
          <body><p>Already valid content</p></body>
        </html>
        """, StandardCharsets.UTF_8);

    XhtmlRepair repair = new XhtmlRepair();
    RepairContext firstRun = repair.process(file);
    String afterFirstRun = Files.readString(file);

    assertEquals(1, firstRun.normalizationChanges());
    assertTrue(afterFirstRun.contains(
        "<link rel=\"stylesheet\" href=\"stylesheet.css\" />"));

    RepairContext secondRun = repair.process(file);
    assertEquals(0, secondRun.totalChanges());
    assertEquals(afterFirstRun, Files.readString(file));
  }
}
