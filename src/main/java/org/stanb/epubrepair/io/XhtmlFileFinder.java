package org.stanb.epubrepair.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/** Finds .html and .xhtml files for processing. */
public final class XhtmlFileFinder {

  public List<Path> find(Path path) throws IOException {
    if (!Files.exists(path)) {
      throw new IllegalArgumentException("Path does not exist: " + path);
    }
    if (Files.isRegularFile(path)) {
      return isXhtmlFile(path) ? List.of(path) : List.of();
    }
    try (Stream<Path> paths = Files.walk(path)) {
      return paths.filter(Files::isRegularFile)
          .filter(this::isXhtmlFile)
          .sorted()
          .toList();
    }
  }

  private boolean isXhtmlFile(Path path) {
    String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
    return name.endsWith(".html") || name.endsWith(".xhtml");
  }
}
