package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Doclet;
import org.pageseeder.xmldoclet.CustomTag;
import org.pageseeder.xmldoclet.Options;

import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.List;

public class TagOption extends XMLDocletOption {

  public TagOption(Options options) {
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
    return Collections.singletonList("-tag");
  }

  @Override
  public String getParameters() {
    return "<tag1>:<tag2>:...";
  }

  @Override
  public boolean process(String option, List<String> arguments) {
    // TODO

    // Custom Tags
//        if (has(options, "-tag")) {
//            List<String> tags = getAll(options, "-tag");
//            for (String def : tags) {
//                int colon = def.indexOf(':');
//                String name = colon < 0? def : def.substring(0, colon);
//                CustomTag tag = new CustomTag(name, false);
//                if (colon >= 0) {
//                    // scope
//                    String scope = def.substring(colon+1);
//                    colon = scope.indexOf(':');
//                    if (colon >= 0) {
//                        String title = scope.substring(colon+1);
//                        scope = scope.substring(0, colon);
//                        tag.setTitle(title);
//                    }
//                    tag.setScope(scope);
//                }
//                o.taglets.put(name, new CustomTag(name, true));
//                reporter.print(Diagnostic.Kind.NOTE, "Using Tag "+name);
//            }
//        }

    return false;
  }
}
