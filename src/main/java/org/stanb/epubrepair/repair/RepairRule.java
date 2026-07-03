package org.stanb.epubrepair.repair;

/** Defines one independent XHTML repair. */
public interface RepairRule {

  /** @return the stable rule identifier */
  String id();

  /** Applies this rule to one XHTML document. */
  void apply(RepairContext context);
}
