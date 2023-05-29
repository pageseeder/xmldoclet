package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.Taglet;

import java.util.Collections;
import java.util.List;

/**
 * Option to specify custom taglet implementations to use for the documentation.
 *
 * @author Christophe Lauret
 * @version 1.0
 */
public final class TagletOption extends XMLDocletOptionBase {

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
