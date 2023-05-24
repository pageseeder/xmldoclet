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
  AUTHOR("author") {

    @Override
    public String toString(DocTree tag, Element element) {
// FIXME      return "<author>"+tag.text()+"</author>";
      return "<author>TODO</author>";
    }

  },

  DEPRECATED("deprecated"),

  SERIAL("serial"),

  SERIALDATA("serialData"),

  SERIALFIELD("serialField"),

  SINCE("since"),

  VERSION("version"){

    @Override
    public String toString(DocTree tag, Element element) {
// FIXME     return "<version>"+tag.text()+"</version>";
      return "<version>TODO</version>";
    }

  };

  /**
   * The name of the tag
   */
  private final String _name;

  /**
   * Creates a new tag.
   *
   * @param name   The name of the tag.
   */
  BlockTag(String name) {
    this._name = name;
  }

  public XMLNode toXMLNode(DocTree tag, Element element) {
// FIXME    return new XMLNode(this._name).text(tag.text());
    return new XMLNode(this._name).text("TODO");
  }

  /**
   * @return the set of locations in which a tag may be used
   */
  public Set<Location> getAllowedLocations() {
    return EnumSet.allOf(Location.class);
  }

  public String toString(DocTree tag, Element element) {
//    return tag.text();
    return "TODO";
  }

  public String toString(List<? extends DocTree> tags, Element element) {
    StringBuilder out = new StringBuilder();
    for (DocTree tag : tags) {
      out.append(toString(tag, element));
    }
    return out.toString();
  }

  @Override
  public String getName() {
    return this._name;
  }

  @Override
  public boolean isInlineTag() {
    return false;
  }

}
