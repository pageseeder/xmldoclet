package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Reporter;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Option to filter classes with the specified annotation.
 *
 * @author Christophe Lauret
 * @version 1.0
 */
public final class AnnotatedOption extends XMLDocletOptionBase {

  private final List<String> annotations = new ArrayList<>();

  public AnnotatedOption(Reporter reporter) {
    super(reporter);
  }

  @Override
  public int getArgumentCount() {
    return 1;
  }

  @Override
  public String getDescription() {
    return "filter classes with the specified annotation";
  }

  @Override
  public Kind getKind() {
    return Kind.STANDARD;
  }

  @Override
  public List<String> getNames() {
    return Collections.singletonList("-annotated");
  }

  @Override
  public String getParameters() {
    return "<annotation>";
  }

  @Override
  public boolean process(String option, List<String> arguments) {
    String annotation = arguments.get(0);
    note("Filtering classes matching annotation: "+annotation);
    this.annotations.add(annotation);
    return true;
  }

  public List<String> getAnnotations() {
    return this.annotations;
  }

  public boolean hasFilter() {
    return !this.annotations.isEmpty();
  }

  /**
   * Filters the included set of classes by checking whether the specified class matches
   * one of the annotations specified in this option.
   *
   * @param element     the class documentation.
   * @return <code>true</code> if the class should be included; <code>false</code> otherwise.
   */
  public boolean matches(TypeElement element) {
    if (this.annotations.isEmpty()) return true;
    List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
    for (AnnotationMirror i : annotationMirrors) {
      // TODO Previous version supported matching the simple name also
      String name = i.getAnnotationType().asElement().toString();
      if (this.annotations.contains(name)) return true;
    }
    return false;
  }
}
