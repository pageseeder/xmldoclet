package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.Reporter;

import javax.tools.Diagnostic;

/**
 * Base class for options
 *
 * @author Christophe Lauret
 * @version 1.0
 */
public abstract class XMLDocletOptionBase implements Doclet.Option {

  private final Reporter reporter;

  XMLDocletOptionBase(Reporter reporter) {
    this.reporter = reporter;
  }

  public void note(String message) {
    this.reporter.print(Diagnostic.Kind.NOTE, message);
  }

  public void warning(String message) {
    this.reporter.print(Diagnostic.Kind.WARNING, message);
  }

  public void error(String message) {
    this.reporter.print(Diagnostic.Kind.ERROR, message);
  }


}
