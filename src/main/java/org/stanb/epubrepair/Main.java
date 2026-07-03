package org.stanb.epubrepair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.jdom2.JDOMException;
import org.stanb.epubrepair.io.XhtmlFileFinder;
import org.stanb.epubrepair.repair.RepairReport;
import org.stanb.epubrepair.repair.XhtmlRepair;

/** Command-line entry point for EPUB Repair. */
public final class Main {

  private Main() {
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: java -jar epub-repair.jar <file-or-directory>");
      System.exit(2);
    }

    int exitCode = run(Path.of(args[0]));
    System.exit(exitCode);
  }

  private static int run(Path inputPath) {
    XhtmlFileFinder finder = new XhtmlFileFinder();
    XhtmlRepair repair = new XhtmlRepair();
    RepairReport report = new RepairReport();

    final List<Path> files;
    try {
      files = finder.find(inputPath);
    } catch (IOException | IllegalArgumentException exception) {
      System.err.println("Unable to inspect input path: " + exception.getMessage());
      return 2;
    }

    for (Path file : files) {
      try {
        repair.process(file);
        report.recordProcessedFile();
      } catch (IOException | JDOMException exception) {
        report.recordFailedFile();
        System.err.println("Failed to process " + file + ": " + exception.getMessage());
      }
    }

    System.out.printf("Files processed: %d%n", report.filesProcessed());
    System.out.printf("Files failed:    %d%n", report.filesFailed());
    return report.hasFailures() ? 1 : 0;
  }
}
