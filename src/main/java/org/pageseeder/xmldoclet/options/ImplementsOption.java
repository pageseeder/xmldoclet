package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Reporter;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Option to filter classes implementing the specified class.
 */
public class ImplementsOption extends XMLDocletOptionBase {

  public List<String> interfaces = new ArrayList<>();

  public ImplementsOption(Reporter reporter) {
    super(reporter);
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
    String interfaceName = arguments.get(0);
    note("Filtering classes implementing: "+interfaceName);
    this.interfaces.add(interfaceName);
    return true;
  }

  public List<String> getInterfaces() {
    return this.interfaces;
  }

  public boolean hasFilter() {
    return this.interfaces.size() > 0;
  }


  /**
   * Filters the included set of classes by checking whether the given class matches
   * one of the specified qualified interface names.
   *
   * @param element the class documentation.
   * @return <code>true</code> if the class should be included; <code>false</code> otherwise.
   */
  public boolean matches(TypeElement element) {
    if (this.interfaces.isEmpty()) return true;
    List<? extends TypeMirror> interfaces = element.getInterfaces();
    for (TypeMirror i : interfaces) {
      if (this.interfaces.contains(i.toString())) return true;
    }
    return false;
  }

}
