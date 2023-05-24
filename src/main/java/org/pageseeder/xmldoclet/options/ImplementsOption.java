package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Doclet;
import org.pageseeder.xmldoclet.Options;

import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.List;

public class ImplementsOption extends XMLDocletOption {

  public ImplementsOption(Options options) {
    super(options);
  }

  @Override
  public int getArgumentCount() {
    return 1;
  }

  @Override
  public String getDescription() {
    return "filter classes implementing the specified interface";
  }

  @Override
  public Kind getKind() {
    return Kind.STANDARD;
  }

  @Override
  public List<String> getNames() {
    return Collections.singletonList("-implements");
  }

  @Override
  public String getParameters() {
    return "<interface>";
  }

  @Override
  public boolean process(String option, List<String> arguments) {
    // TODO

//        if (has(options, "-implements")) {
//            String iface = get(options, "-implements");
//            if (iface != null) {
//                o.implementsFilter = iface;
//                reporter.print(Diagnostic.Kind.NOTE, "Filtering classes implementing: "+iface);
//            } else {
//                reporter.print(Diagnostic.Kind.WARNING, "'-implements' option ignored - interface not specified");
//            }
//        }

    return false;
  }
}
