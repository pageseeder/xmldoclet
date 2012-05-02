package org.weborganic.xmldoclet;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * A custom tag for tags specified using the -tag option.
 *
 * @author Christophe Lauret
 * @version 2 May 2012
 */
public final class CustomTag implements Taglet {

  /**
   * The name of the tag.
   */
  private String _name;

  /**
   * The title/header/description for the tag.
   */
  private String _title;

  /**
   * Whether the tag appears inline or not.
   */
  private boolean _inline;

  private boolean _inConstructor = true;

  private boolean _inField = true;

  private boolean _inMethod = true;

  private boolean _inOverview = true;

  private boolean _inPackage = true;

  private boolean _inType = true;

  /**
   * Creates a custom tag.
   *
   * @param name   the name of the tag.
   * @param inline <code>true</code> for inline tags; <code>false</code> otherwise.
   */
  public CustomTag(String name, boolean inline) {
    this._name = name;
    this._inline = inline;
  }

  /**
   * Creates a custom tag.
   *
   * @param name the name of the tag.
   * @param inline <code>true</code> for inline tags; <code>false</code> otherwise.
   * @param title the title of the tag
   */
  public CustomTag(String name, boolean inline, String title) {
    this._name = name;
    this._inline = inline;
    this._title = title;
  }

  /**
   * Reuses the same scope attributes as used by the standard Javadoc doclet.
   *
   * <pre>
   *  a (all)
   *  o (overview)
   *  p (packages)
   *  t (types, that is classes and interfaces)
   *  c (constructors)
   *  m (methods)
   *  f (fields)
   * </pre>
   */
  public void setScope(String scope) {
    this._inConstructor = scope.indexOf('a') >= 0 || scope.indexOf('c') >= 0;
    this._inField       = scope.indexOf('a') >= 0 || scope.indexOf('f') >= 0;
    this._inMethod      = scope.indexOf('a') >= 0 || scope.indexOf('m') >= 0;
    this._inOverview    = scope.indexOf('a') >= 0 || scope.indexOf('o') >= 0;
    this._inPackage     = scope.indexOf('a') >= 0 || scope.indexOf('p') >= 0;
    this._inType        = scope.indexOf('a') >= 0 || scope.indexOf('t') >= 0;
  }

  @Override
  public boolean inConstructor() {
    return this._inConstructor;
  }

  @Override
  public boolean inField() {
    return this._inField;
  }

  @Override
  public boolean inMethod() {
    return this._inMethod;
  }

  @Override
  public boolean inOverview() {
    return this._inOverview;
  }

  @Override
  public boolean inPackage() {
    return this._inPackage;
  }

  @Override
  public boolean inType() {
    return this._inType;
  }

  @Override
  public String getName() {
    return this._name;
  }

  /**
   * Sets the title to use for this tag.
   *
   * @param title the title to use for this tag.
   */
  public void setTitle(String title) {
    this._title = title;
  }

  /**
   * @return the title to use for this tag.
   */
  public String getTitle() {
    return this._title;
  }

  @Override
  public boolean isInlineTag() {
    return this._inline;
  }

  @Override
  public String toString(Tag tag) {
    String element = this._inline? "span" : "div";
    StringBuilder out = new StringBuilder();
    out.append('<').append(element);
    out.append(" class=\"").append(this._name).append('"');
    if (this._title != null)
      out.append(" title=\"").append(this._title).append('"');
    out.append('>');
    out.append(tag.text());
    out.append("</").append(element).append('>');
    return out.toString();
  }

  @Override
  public String toString(Tag[] tags) {
    StringBuilder out = new StringBuilder();
    for (Tag t : tags) {
      out.append(toString(t));
    }
    return out.toString();
  }
}
