package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Doclet;
import org.pageseeder.xmldoclet.Options;

import java.util.Collections;
import java.util.List;

public class AnnotatedOption extends XMLDocletOption {

  public AnnotatedOption(Options options) {
    super(options);
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
    // TODO

    // Annotated
//        if (has(options, "-annotated")) {
//            String annotation = get(options, "-annotated");
//            if (annotation != null) {
//                o.annotationFilter = annotation;
//                reporter.print(Diagnostic.Kind.NOTE, "Filtering classes annotated: "+annotation);
//            } else {
//                reporter.print(Diagnostic.Kind.WARNING, "'-annotated' option ignored - annotation not specified");
//            }
//        }

    return false;
  }
}
