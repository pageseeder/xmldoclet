package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Doclet;
import org.pageseeder.xmldoclet.Options;

import java.util.Collections;
import java.util.List;

public class ExtendsOption extends XMLDocletOption {

  public ExtendsOption(Options options) {
    super(options);
  }

  @Override
  public int getArgumentCount() {
    return 1;
  }

  @Override
  public String getDescription() {
    return "filter classes extending the specified class";
  }

  @Override
  public Kind getKind() {
    return Kind.STANDARD;
  }

  @Override
  public List<String> getNames() {
    return Collections.singletonList("-extends");
  }

  @Override
  public String getParameters() {
    return "<superclass>";
  }

  @Override
  public boolean process(String option, List<String> arguments) {
    // TODO
    // Extends
//        if (has(options, "-extends")) {
//            String superclass = get(options, "-extends");
//            if (superclass != null) {
//                o.extendsFilter = superclass;
//                reporter.print(Diagnostic.Kind.NOTE, "Filtering classes extending: "+superclass);
//            } else {
//                reporter.print(Diagnostic.Kind.WARNING, "'-extends' option ignored - superclass not specified");
//            }
//        }

    return false;
  }
}
