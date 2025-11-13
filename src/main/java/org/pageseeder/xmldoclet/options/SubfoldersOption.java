package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Reporter;

import java.util.List;

/**
 * Option to generate the documentation within subfolders.
 *
 * @author Christophe Lauret
 *
 * @version 1.0
 * @since 1.0
 */
public class SubfoldersOption extends XMLDocletOptionBase {

  private boolean enabled = false;

  public SubfoldersOption(Reporter reporter) {
    super(reporter);
  }

  @Override
  public int getArgumentCount() {
    return 0;
  }

  @Override
  public String getDescription() {
    return "Whether files are organised as subfolders or all in the same folder";
  }

  @Override
  public Kind getKind() {
    return Kind.STANDARD;
  }

  @Override
  public List<String> getNames() {
    return List.of("-subfolders");
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
