package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Doclet;
import org.pageseeder.xmldoclet.Options;

import java.util.Collections;
import java.util.List;

public class TagletOption extends XMLDocletOption {

  public TagletOption(Options options) {
    super(options);
  }

  @Override
  public int getArgumentCount() {
    return 1;
  }

  @Override
  public String getDescription() {
    return "custom tags";
  }

  @Override
  public Kind getKind() {
    return Kind.STANDARD;
  }

  @Override
  public List<String> getNames() {
    return Collections.singletonList("-taglet");
  }

  @Override
  public String getParameters() {
    return "<classname1>:<classname2>:...";
  }

  @Override
  public boolean process(String option, List<String> arguments) {
    // TODO

//        // Taglets
//        if (has(options, "-taglet")) {
//            String classes = get(options, "-taglet");
//            if (classes != null) {
//                for (String c : classes.split(":")) {
//                    try {
//                        Class<?> x = Class.forName(c);
//                        Class<? extends Taglet> t = x.asSubclass(Taglet.class);
//                        Method m = t.getMethod("register", Map.class);
//                        m.invoke(null, o.taglets);
//                        reporter.print(Diagnostic.Kind.NOTE, "Using Taglet "+t.getName());
//                    } catch (Exception ex) {
//                        reporter.print(Diagnostic.Kind.ERROR, "'-taglet' option reported error - :"+ex.getMessage());
//                    }
//                }
//            } else {
//                reporter.print(Diagnostic.Kind.WARNING, "'-taglet' option ignored - classes not specified");
//            }
//        }
    return false;
  }
}
