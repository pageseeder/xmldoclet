package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Taglet;
import jdk.javadoc.doclet.Reporter;

import java.util.Collections;
import java.util.List;

public class TagletOption extends XMLDocletOptionBase {

  public TagletOption(Reporter reporter) {
    super(reporter);
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
    String classes = arguments.get(0);
    for (String className : classes.split(":")) {
      try {
        // FIXME API has changed
        Class<?> clazz = Class.forName(className);
        Class<? extends Taglet> t = clazz.asSubclass(Taglet.class);
//        Method m = t.getMethod("register", Map.class);
//        m.invoke(null, o.taglets);
//        options.note("Using Taglet " + t.getName());
      } catch (Exception ex) {
        error("'-taglet' option reported error - :" + ex.getMessage());
        return false;
      }
    }
    return true;
  }
}
