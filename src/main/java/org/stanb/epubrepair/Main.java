package org.stanb.epubrepair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.jdom2.JDOMException;
import org.stanb.epubrepair.io.XhtmlFileFinder;
import org.stanb.epubrepair.repair.RepairContext;
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

    System.exit(run(Path.of(args[0])));
  }

  private static int run(Path inputPath) {
    XhtmlFileFinder finder = new XhtmlFileFinder();
    XhtmlRepair repair = new XhtmlRepair();
    RepairReport report = new RepairReport(repair.rules());

    final List<Path> files;
    try {
      files = finder.find(inputPath);
    } catch (IOException | IllegalArgumentException exception) {
      System.err.println("Unable to inspect input path: " + exception.getMessage());
      return 2;
    }

    for (Path file : files) {
      try {
        RepairContext context = repair.process(file);
        report.recordProcessedFile(context);
        System.out.printf("%s: %d change(s)%n", file, context.totalChanges());
      } catch (IOException | JDOMException exception) {
        report.recordFailedFile();
        System.err.println("Failed to process " + file + ": " + exception.getMessage());
      }
    }

    printReport(report);
    return report.hasFailures() ? 1 : 0;
  }

  private static void printReport(RepairReport report) {
    System.out.printf("Files processed: %d%n", report.filesProcessed());
    System.out.printf("Files failed:    %d%n", report.filesFailed());
    System.out.printf("Changes made:    %d%n", report.changesMade());
    System.out.println();
    System.out.println("Changes by rule:");

    for (Map.Entry<String, Integer> entry : report.changesByRule().entrySet()) {
      System.out.printf("  %s: %d%n", entry.getKey(), entry.getValue());
    }
  }
}
