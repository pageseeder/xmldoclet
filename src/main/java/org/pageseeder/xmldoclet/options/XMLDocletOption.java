package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Doclet;
import org.pageseeder.xmldoclet.Options;

public abstract class XMLDocletOption implements Doclet.Option {

  protected Options options;

  XMLDocletOption(Options options ) {
    this.options = options;
  }

}
