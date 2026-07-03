package org.stanb.epubrepair.repair;

/** Records processing results for one application run. */
public final class RepairReport {

  private int filesProcessed;
  private int filesFailed;

  public void recordProcessedFile() {
    filesProcessed++;
  }

  public void recordFailedFile() {
    filesFailed++;
  }

  public int filesProcessed() {
    return filesProcessed;
  }

  public int filesFailed() {
    return filesFailed;
  }

  public boolean hasFailures() {
    return filesFailed > 0;
  }
}
