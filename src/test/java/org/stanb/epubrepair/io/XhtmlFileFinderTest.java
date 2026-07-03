package org.stanb.epubrepair.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class XhtmlFileFinderTest {

  @TempDir
  Path temporaryDirectory;

  @Test
  void findsHtmlAndXhtmlFilesRecursively() throws IOException {
    Path nested = Files.createDirectory(temporaryDirectory.resolve("nested"));
    Path html = Files.createFile(temporaryDirectory.resolve("chapter.html"));
    Path xhtml = Files.createFile(nested.resolve("notes.XHTML"));
    Files.createFile(temporaryDirectory.resolve("stylesheet.css"));

    List<Path> files = new XhtmlFileFinder().find(temporaryDirectory);

    assertEquals(List.of(html, xhtml).stream().sorted().toList(), files);
  }
}
