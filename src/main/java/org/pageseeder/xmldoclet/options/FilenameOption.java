package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Reporter;

import java.util.Collections;
import java.util.List;

/**
 * Option to specify the filename of the output.
 *
 * <p>Only used if single output.
 *
 * @see MultipleOption
 *
 * @author Christophe Lauret
 * @version 1.0
 */
public final class FilenameOption extends XMLDocletOptionBase {

  /**
   * The default filename for the output.
   */
  private static final String DEFAULT_FILENAME = "xmldoclet.xml";

  private String filename = DEFAULT_FILENAME;

  public FilenameOption(Reporter reporter) {
    super(reporter);
  }

  @Override
  public int getArgumentCount() {
    return 1;
  }

  @Override
  public String getDescription() {
    return "name of the file (used for single output only)";
  }

  @Override
  public Kind getKind() {
    return Kind.STANDARD;
  }

  @Override
  public List<String> getNames() {
    return Collections.singletonList("-filename");
  }

  @Override
  public String getParameters() {
    return "<filename>";
  }

  @Override
  public boolean process(String option, List<String> arguments) {
    this.filename = arguments.get(0);
    return true;
  }

  public String getFilename() {
    return this.filename;
  }

}
