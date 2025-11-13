package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Reporter;
import org.pageseeder.xmldoclet.CustomTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Option to specify custom tags to use for the documentation.
 *
 * @author Christophe Lauret
 *
 * @version 1.0
 * @since 1.0
 */
public final class TagOption extends XMLDocletOptionBase {

  private final List<CustomTag> tags = new ArrayList<>();

  public TagOption(Reporter reporter) {
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
    return Collections.singletonList("-tag");
  }

  @Override
  public String getParameters() {
    return "<name>:<scope>?:<title>?";
  }

  @Override
  public boolean process(String option, List<String> arguments) {
    String spec = arguments.get(0);
    int colon = spec.indexOf(':');
    String name = colon < 0 ? spec : spec.substring(0, colon);
    if (!CustomTag.isValidName(name)) {
      error("Invalid name for custom tag " + name);
      return false;
    }
    CustomTag tag = new CustomTag(name, false);
    if (colon >= 0) {
      // scope
      String scope = spec.substring(colon + 1);
      colon = scope.indexOf(':');
      if (colon >= 0) {
        String title = scope.substring(colon + 1);
        scope = scope.substring(0, colon);
        tag.setTitle(title);
      }
      if (!CustomTag.isValidScope(scope)) {
        warning("Invalid scope for custom tag " + name + ": " + scope);
      }
      tag.setScope(scope);
    }
    this.tags.add(tag);
    note("Adding custom tag " + tag);
    return true;
  }

  public List<CustomTag> getTags() {
    return this.tags;
  }



}
