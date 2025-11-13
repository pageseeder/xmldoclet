package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Reporter;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

/**
 * Option to filter classes implementing the specified class.
 *
 * @author Christophe Lauret
 *
 * @version 1.0
 * @since 1.0
 */
public class ImplementsOption extends XMLDocletOptionBase {

  private final List<String> interfaces = new ArrayList<>();

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
    return List.of("-implements");
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

  /**
   * Retrieves the list of interface names specified for filtering classes.
   *
   * @return a list of interface names used for filtering.
   */
  public List<String> getInterfaces() {
    return this.interfaces;
  }

  /**
   * Checks if there are any filters defined for the interfaces.
   *
   * @return true if there is at least one interface filter defined; false otherwise.
   */
  public boolean hasFilter() {
    return !this.interfaces.isEmpty();
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
    List<? extends TypeMirror> mirrors = element.getInterfaces();
    for (TypeMirror i : mirrors) {
      if (this.interfaces.contains(i.toString())) return true;
    }
    return false;
  }

}
