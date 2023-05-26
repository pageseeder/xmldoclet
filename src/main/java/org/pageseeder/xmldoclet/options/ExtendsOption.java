package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Reporter;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Filter classes extending the specified class.
 */
public class ExtendsOption extends XMLDocletOptionBase {

  public List<String> superclasses = new ArrayList<>();

  public ExtendsOption(Reporter reporter) {
    super(reporter);
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
    String superclass = arguments.get(0);
    this.superclasses.add(superclass);
    note("Filtering classes extending: "+superclass);
    return true;
  }

  public List<String> getSuperclasses() {
    return this.superclasses;
  }

  public boolean hasFilter() {
    return this.superclasses.size() > 0;
  }

  /**
   * Indicates whether the specified class matches of the superclasses in this option.
   *
   * @param element the class element
   * @return <code>true</code> if the class should be included; <code>false</code> otherwise.
   */
  public boolean matches(TypeElement element) {
    if (this.superclasses.isEmpty()) return true;
    TypeMirror superclass = element.getSuperclass();
    return superclass != null && this.superclasses.contains(superclass.toString());
  }

}
