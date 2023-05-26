package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Reporter;
import org.pageseeder.xmldoclet.CustomTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TagOption extends XMLDocletOptionBase {

  private List<CustomTag> tags = new ArrayList<>();

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
    return "<name>:<scope>";
  }

  @Override
  public boolean process(String option, List<String> arguments) {
    String spec = arguments.get(0);
    int colon = spec.indexOf(':');
    // TODO Check spec is valid XML name
    String name = colon < 0? spec : spec.substring(0, colon);
    CustomTag tag = new CustomTag(name, false);
    if (colon >= 0) {
        // scope
        String scope = spec.substring(colon+1);
        colon = scope.indexOf(':');
        if (colon >= 0) {
            String title = scope.substring(colon+1);
            scope = scope.substring(0, colon);
            tag.setTitle(title);
        }
        tag.setScope(scope);
    }
    this.tags.add(tag);
    note("Adding custom tag "+tag);
    return true;
  }

  public List<CustomTag> getTags() {
    return this.tags;
  }

}
