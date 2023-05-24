package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Doclet;
import org.pageseeder.xmldoclet.Options;

import java.util.Collections;
import java.util.List;

public class SubfoldersOption extends XMLDocletOption {

  public SubfoldersOption(Options options) {
    super(options);
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
    return Collections.singletonList("-subfolders");
  }

  @Override
  public String getParameters() {
    return "";
  }

  @Override
  public boolean process(String option, List<String> arguments) {
    // TODO
    return false;
  }
}
