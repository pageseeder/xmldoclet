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
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import jdk.javadoc.doclet.Taglet;
import org.eclipse.jdt.annotation.Nullable;

import javax.lang.model.element.Element;
import java.util.*;

/**
 * A custom tag for tags specified using the -tag option.
 *
 * @author Christophe Lauret
 * @version 1.0
 */
public final class CustomTag implements Taglet {

  /**
   * The custom tag must be a valid XML name regex per XML 1.0 spec
   * and a valid Java identifier, but allowing `.`
   */
  private static final String VALID_NAME_REGEX = "[A-Z_a-z][A-Za-z0-9_.]*";

  /**
   * The name of the tag.
   */
  private final String name;

  /**
   * The title/header/description for the tag.
   */
  private @Nullable String title;

  /** Whether the tag appears inline or not. */
  private final boolean isInline;

  private EnumSet<Location> allowedLocations = EnumSet.allOf(Location.class);

  /**
   * Creates a custom tag.
   *
   * @param name   the name of the tag.
   * @param isInline <code>true</code> for inline tags; <code>false</code> otherwise.
   */
  public CustomTag(String name, boolean isInline) {
    this.name = Objects.requireNonNull(name);
    this.isInline = isInline;
  }

  /**
   * Creates a custom tag.
   *
   * @param name the name of the tag.
   * @param isInline <code>true</code> for inline tags; <code>false</code> otherwise.
   * @param title the title of the tag
   */
  public CustomTag(String name, boolean isInline, String title) {
    this.name = Objects.requireNonNull(name);
    this.isInline = isInline;
    this.title = title;
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
   *
   * @param scope the scope to use.
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
    return this.name;
  }

  /**
   * Sets the title to use for this tag.
   *
   * @param title the title to use for this tag.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return the title to use for this tag.
   */
  public @Nullable String getTitle() {
    return this.title;
  }

  @Override
  public boolean isInlineTag() {
    return this.isInline;
  }

  private String toString(UnknownBlockTagTree block) {
    return toString(block.getContent());
  }

  private String toString(UnknownInlineTagTree inline) {
    return toString(inline.getContent());
  }

  public String toString(List<? extends DocTree> contents) {
    String element = this.isInline ? "span" : "div";
    StringBuilder out = new StringBuilder();
    out.append('<').append(element);
    out.append(" class=\"").append(this.name).append('"');
    if (this.title != null) {
      out.append(" title=\"").append(this.title).append('"');
    }
    out.append('>');
    out.append(contents);
    out.append("</").append(element).append('>');
    return out.toString();
  }

  public String toString(List<? extends DocTree> tags, Element element) {
    StringBuilder out = new StringBuilder();
    for (DocTree t : tags) {
      if (t instanceof UnknownBlockTagTree) {
        UnknownBlockTagTree block = (UnknownBlockTagTree)t;
        out.append(toString(block.getContent()));
      }
      if (t instanceof UnknownInlineTagTree) {
        UnknownInlineTagTree inline = (UnknownInlineTagTree)t;
        out.append(toString(inline.getContent()));
      }
    }
    return out.toString();
  }

  @Override
  public String toString() {
    return this.name;
  }

  /**
   * Validates whether the provided name is valid based on a predefined format.
   *
   * @param name the name to validate, which may be {@code null}.
   * @return {@code true} if the name is not {@code null} and matches the expected format;
   *         {@code false} otherwise.
   */
  public static boolean isValidName(@Nullable String name) {
    return name != null && name.matches(VALID_NAME_REGEX);
  }

  /**
   * Validates whether the given scope is valid by ensuring it contains
   * only allowed characters ('a', 'o', 'p', 't', 'c', 'm', 'f') without duplicates.
   *
   * @param scope the input string representing the scope to validate.
   * @return true if the scope is valid (contains only allowed characters
   *         and no duplicates); false otherwise.
   */
  public static boolean isValidScope(String scope) {
    // Only allowed characters
    String allowed = "aoptcmf";
    // Ensure no duplicates and only allowed characters
    boolean[] used = new boolean[allowed.length()];
    for (int i = 0; i < scope.length(); i++) {
      char ch = scope.charAt(i);
      int idx = allowed.indexOf(ch);
      if (idx == -1) return false; // Invalid char
      if (used[idx]) return false; // Duplicate char
      used[idx] = true;
    }
    return true;
  }

}
