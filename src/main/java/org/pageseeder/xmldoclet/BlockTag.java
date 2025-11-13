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

import com.sun.source.doctree.*;
import jdk.javadoc.doclet.Taglet;

import javax.lang.model.element.Element;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * A collection of taglets to support the standard javadoc tags.
 *
 * @author Christophe Lauret
 * @version 1.0
 */
public enum BlockTag implements Taglet {

  /**
   * Taglet for the <code>@author</code> tag.
   */
  AUTHOR("author"),

  DEPRECATED("deprecated"),

  SERIAL("serial"),

  SERIAL_DATA("serialData"),

  SERIAL_FIELD("serialField"),

  SINCE("since"),

  VERSION("version");

  /**
   * The name of the tag
   */
  private final String name;

  /**
   * Creates a new tag.
   *
   * @param name   The name of the tag.
   */
  BlockTag(String name) {
    this.name = name;
  }

  public XMLNode toXMLNode(DocTree tag) {
    if (tag instanceof AuthorTree) {
      return new XMLNode("author").text(((AuthorTree)tag).getName().toString());
    }
    if (tag instanceof DeprecatedTree) {
      return new XMLNode("deprecated").text(((DeprecatedTree)tag).getBody().toString());
    }
    if (tag instanceof SinceTree) {
      String since = ((SinceTree) tag).getBody().toString();
      return new XMLNode(this.name).text(since);
    }
    if (tag instanceof ThrowsTree) {
      String throwsText = ((ThrowsTree) tag).getDescription().toString();
      return new XMLNode(this.name).text(throwsText);
    }
    if (tag instanceof UnknownBlockTagTree) {
      // TODO
      return new XMLNode(this.name).text(((UnknownBlockTagTree) tag).getContent().toString());
    }
    if (tag instanceof VersionTree) {
      String version = ((VersionTree) tag).getBody().toString();
      return new XMLNode("version").text(version);
    }
    if (tag instanceof SerialTree) {
      String description = ((SerialTree) tag).getDescription().toString();
      return new XMLNode("serial").text(description);
    }
    return new XMLNode(this.name).text(tag.toString());
  }

  /**
   * @return the set of locations in which a tag may be used
   */
  public Set<Location> getAllowedLocations() {
    return EnumSet.allOf(Location.class);
  }

  public String toString(DocTree tag) {
    return toXMLNode(tag).toString("");
  }

  public String toString(List<? extends DocTree> tags, Element element) {
    StringBuilder out = new StringBuilder();
    for (DocTree tag : tags) {
      out.append(toString(tag));
    }
    return out.toString();
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public boolean isInlineTag() {
    return false;
  }

}
