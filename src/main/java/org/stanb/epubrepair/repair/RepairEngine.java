package org.stanb.epubrepair.repair;

import java.util.List;

/** Applies an ordered set of repair rules to an XHTML document. */
public final class RepairEngine {

  private final List<RepairRule> rules;

  public RepairEngine(List<RepairRule> rules) {
    this.rules = List.copyOf(rules);
  }

  public void applyRules(RepairContext context) {
    rules.forEach(rule -> rule.apply(context));
  }
}
