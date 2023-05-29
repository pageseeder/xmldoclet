package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Reporter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Option to specify the character encoding of the XML output.
 *
 * <p>Defaults to UTF-8.
 *
 * @author Christophe Lauret
 * @version 1.0
 */
public final class DocencodingOption extends XMLDocletOptionBase {

  private Charset charset = StandardCharsets.UTF_8;

  public DocencodingOption(Reporter reporter) {
    super(reporter);
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
    String encoding = arguments.get(0);
    try {
      this.charset = Charset.forName(encoding);
      note("Output encoding: "+charset);
    } catch (IllegalArgumentException ex) {
      error("Unsupported encoding encoding value - must match available charset");
      return false;
    }
    return true;
  }

  public Charset getCharset() {
    return this.charset;
  }

}
