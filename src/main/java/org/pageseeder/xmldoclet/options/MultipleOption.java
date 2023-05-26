package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Reporter;

import java.util.Collections;
import java.util.List;

public class MultipleOption extends XMLDocletOptionBase {

  private boolean enabled = false;

  public MultipleOption(Reporter reporter) {
    super(reporter);
  }

  @Override
  public int getArgumentCount() {
    return 0;
  }

  @Override
  public String getDescription() {
    return "Whether to save as multiple files.";
  }

  @Override
  public Kind getKind() {
    return Kind.STANDARD;
  }

  @Override
  public List<String> getNames() {
    return Collections.singletonList("-multiple");
  }

  @Override
  public String getParameters() {
    return "";
  }

  @Override
  public boolean process(String option, List<String> arguments) {
    this.enabled = true;
    return true;
  }

  public boolean enabled() {
    return this.enabled;
  }

}
