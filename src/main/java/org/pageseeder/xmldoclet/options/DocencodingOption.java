package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Doclet;
import org.pageseeder.xmldoclet.Options;

import javax.tools.Diagnostic;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

public class DocencodingOption extends XMLDocletOption {

  public DocencodingOption(Options options) {
    super(options);
  }

  @Override
  public int getArgumentCount() {
    return 1;
  }

  @Override
  public String getDescription() {
    return "the output encoding of the XML files";
  }

  @Override
  public Kind getKind() {
    return Kind.STANDARD;
  }

  @Override
  public List<String> getNames() {
    return Collections.singletonList("-docencoding");
  }

  @Override
  public String getParameters() {
    return "<encoding>";
  }

  @Override
  public boolean process(String option, List<String> arguments) {
    // TODO

//        // Output encoding
//        if (has(options, "-docencoding")) {
//            String encoding = get(options, "-docencoding");
//            if (encoding == null) {
//                reporter.print(Diagnostic.Kind.ERROR, "Missing value for <name>, usage:");
//                reporter.print(Diagnostic.Kind.ERROR, "-docencoding <name> \t Output encoding name");
//                return null;
//            } else {
//                o.encoding = Charset.forName(encoding);
//                reporter.print(Diagnostic.Kind.NOTE, "Output encoding: "+o.encoding);
//            }
//        }
    return false;
  }
}
