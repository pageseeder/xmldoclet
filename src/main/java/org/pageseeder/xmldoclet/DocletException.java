package org.pageseeder.xmldoclet;

import org.eclipse.jdt.annotation.Nullable;

import javax.lang.model.element.Element;

/**
 * This exception is thrown to indicate an error related to the processing of a doclet element.
 *
 * <p>It serves as a wrapper for issues that occur during operations involving doclet elements,
 * such as generation of documentation or processing metadata.
 *
 * @author Christophe Lauret
 *
 * @version 1.0
 * @since 1.0
 */
public class DocletException extends Exception {

  private final transient @Nullable Element element;

  public DocletException(@Nullable Element element, String message) {
    super(message);
    this.element = element;
  }

  public DocletException(@Nullable Element element, String message, Throwable cause) {
    super(message, cause);
    this.element = element;
  }

  public @Nullable Element getElement() {
    return element;
  }
}
