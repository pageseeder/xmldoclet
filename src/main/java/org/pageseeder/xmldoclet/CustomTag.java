/*
 * Copyright 2010-2015 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.xmldoclet;

import com.sun.source.doctree.DocTree;
import jdk.javadoc.doclet.Taglet;

import javax.lang.model.element.Element;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A custom tag for tags specified using the -tag option.
 *
 * @author Christophe Lauret
 * @version 1.0
 */
public final class CustomTag implements Taglet {

  /**
   * The name of the tag.
   */
  private final String _name;

  /**
   * The title/header/description for the tag.
   */
  private String _title;

  /** Whether the tag appears inline or not. */
  private final boolean _inline;

  private EnumSet<Location> allowedLocations = EnumSet.allOf(Location.class);

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
    if (scope.indexOf('a') >= 0) {
      this.allowedLocations = EnumSet.allOf(Location.class);
      return;
    }
    HashSet<Location> locations = new HashSet<>();
    if (scope.indexOf('c') >= 0) locations.add(Location.CONSTRUCTOR);
    if (scope.indexOf('f') >= 0) locations.add(Location.FIELD);
    if (scope.indexOf('m') >= 0) locations.add(Location.METHOD);
    if (scope.indexOf('o') >= 0) locations.add(Location.OVERVIEW);
    if (scope.indexOf('p') >= 0) locations.add(Location.PACKAGE);
    if (scope.indexOf('t') >= 0) locations.add(Location.TYPE);
    this.allowedLocations = EnumSet.copyOf(locations);
  }

  @Override
  public Set<Location> getAllowedLocations() {
    return this.allowedLocations;
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

  public String toString(DocTree tag, Element el) {
    String element = this._inline? "span" : "div";
    StringBuilder out = new StringBuilder();
    out.append('<').append(element);
    out.append(" class=\"").append(this._name).append('"');
    if (this._title != null) {
      out.append(" title=\"").append(this._title).append('"');
    }
    out.append('>');
// FIXME   out.append(tag.text());
    out.append("TODO");
    out.append("</").append(element).append('>');
    return out.toString();
  }

  public String toString(List<? extends DocTree> tags, Element element) {
    StringBuilder out = new StringBuilder();
    for (DocTree t : tags) {
      out.append(toString(t, element));
    }
    return out.toString();
  }
}
