package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Doclet;
import org.pageseeder.xmldoclet.Options;

import java.util.Collections;
import java.util.List;

public class FilenameOption extends XMLDocletOption {

  public FilenameOption(Options options) {
    super(options);
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
    // TODO
//        // Extends
//        if (has(options, "-filename")) {
//            String name = get(options, "-filename");
//            if (name != null && !o.multipleFiles) {
//                o.filename = name;
//                reporter.print(Diagnostic.Kind.NOTE, "Using file name: "+name);
//            } else {
//                reporter.print(Diagnostic.Kind.WARNING, "'-filename' option ignored");
//            }
//        }
    return false;
  }
}
