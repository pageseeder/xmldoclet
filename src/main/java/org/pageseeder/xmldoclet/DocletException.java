package org.pageseeder.xmldoclet;

import org.eclipse.jdt.annotation.Nullable;

import javax.lang.model.element.Element;

public class DocletException extends Exception {

  private final @Nullable Element element;

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
