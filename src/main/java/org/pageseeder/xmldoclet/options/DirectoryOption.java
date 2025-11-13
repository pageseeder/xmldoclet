package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Reporter;

import java.io.File;
import java.util.List;

/**
 * Option to specify the output directory of the doclet.
 *
 * @author Christophe Lauret
 *
 * @version 1.0
 * @since 1.0
 */
public final class DirectoryOption extends XMLDocletOptionBase {

  private File directory = new File(".");

  public DirectoryOption(Reporter reporter) {
    super(reporter);
  }

  @Override
  public int getArgumentCount() {
    return 1;
  }

  @Override
  public String getDescription() {
    return "the directory where output is placed.";
  }

  @Override
  public Kind getKind() {
    return Kind.STANDARD;
  }

  @Override
  public List<String> getNames() {
    return List.of("-d");
  }

  @Override
  public String getParameters() {
    return "<directory>";
  }

  @Override
  public boolean process(String option, List<String> arguments) {
    this.directory = new File(arguments.get(0));
    return true;
  }

  public File getDirectory() {
    return this.directory;
  }

}
