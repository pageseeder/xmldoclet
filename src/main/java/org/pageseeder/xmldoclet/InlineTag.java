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

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * A collection of taglets to support the standard javadoc inline tags.
 *
 * <p>These will replace text within comments using XHTML for links.
 *
 * @author Christophe Lauret
 * @version 9 May 2012
 */
public enum InlineTag implements Taglet {

  /**
   * Equivalent to "@literal" but wrapping the content in {@literal <code>}.
   *
   * <p>Displays text in code font without interpreting the text as HTML markup or nested javadoc tags.
   *
   * <p>If you want the same functionality without the code font, use the "@literal" tag.
   *
   * <p>Same as: {@code <code><![CDATA["+tag.text()+"]]></code>}
   */
  CODE("code") {

    @Override
    public String toString(Tag tag) {
      return "<code><![CDATA["+tag.text()+"]]></code>";
    }

  },

  /**
   * Represents the relative path to the generated document's (destination) root directory from any generated page.
   *
   * <p>It is useful when you want to include a file that you want to reference from all generated pages.
   *
   * <p>This tag is valid in all doc comments.
   *
   * <p>Always "".
   *
   * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html#docRoot">@docRoot tag</a>
   */
  DOCROOT("docRoot") {

    @Override
    public String toString(Tag tag) {
      // TODO Accommodate wwhen options for path are different (i.e. in subfolders)
      return "";
    }

  },

  /**
   * Inherits (copies) documentation from the "nearest" inheritable class or implementable interface into the current
   * doc comment at this tag's location.
   *
   * This allows you to write more general comments higher up the inheritance tree, and to write around the copied
   * text.
   *
   * This tag is valid only in these places in a doc comment:
   * <ul>
   *   <li>In the main description block of a method. In this case, the main description is copied from a class
   *   or interface up the hierarchy.</li>
   *   <li>In the text arguments of the @return, @param and @throws tags of a method. In this case, the tag text is
   *   copied from the corresponding tag up the hierarchy.</li>
   * </ul>
   *
   * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html#inheritDoc">@inheritDoc tag</a>
   */
  INHERITDOC("inheritDoc") {

    @Override
    public String toString(Tag tag) {
      return "<div class=\"inherited\">"+tag.text()+"</div>";
    }

  },

  /**
   * Inserts an in-line link with visible text label that points to the documentation for the specified package,
   * class or member name of a referenced class.
   *
   * This tag is valid in all doc comments: overview, package, class, interface, constructor, method and field,
   * including the text portion of any tag.
   *
   * This syntax this tag is:
   * <pre>package.class#member label</pre>
   *
   * <p>If you need to use "}" inside the label, use the HTML entity notation <code>&amp;#125;</code>.
   *
   * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html#link">@link tag</a>
   */
  LINK("link"){

    @Override
    public String toString(Tag tag) {
      return toLinkString(tag, "link");
    }

  },

  /**
   * Identical to "link", except the link's label is displayed in plain text than code font.
   *
   * Useful when the label is plain text.
   *
   * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html#linkplain">@linkplain tag</a>
   */
  LINKPLAIN("linkplain") {

    @Override
    public String toString(Tag tag) {
      return toLinkString(tag, "linkplain");
    }

  },

  /**
   * Displays text without interpreting the text as HTML markup or nested javadoc tags.
   *
   * <p>Literal tags are simply wrapped in CDATA sections.
   *
   * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html#literal">@literal tag</a>
   */
  LITERAL("literal") {

    @Override
    public String toString(Tag tag) {
      return "<![CDATA["+tag.text()+"]]>";
    }

  },

  /**
   * When {@value} is used (without any argument) in the doc comment of a static field,
   * it displays the value of that constant.
   *
   * <p>When used with argument package.class#field in any doc comment, it displays the value
   * of the specified constant.
   *
   * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html#value">@value tag</a>
   */
  VALUE("value"){

    @Override
    public String toString(Tag tag) {
      return "<var>"+tag.text()+"</var>";
    }

  };

  // enum class methods ===============================================================================================

  /**
   * The name of the tag
   */
  private final String _name;

  /**
   * Creates a new tag.
   *
   * @param name   The name of the tag.
   */
  InlineTag(String name) {
    this._name = name;
  }

  @Override
  public String getName() {
    return this._name;
  }

  @Override
  public boolean isInlineTag() {
    return true;
  }

  @Override
  public boolean inConstructor() {
    return true;
  }

  @Override
  public boolean inField(){
    return true;
  }

  @Override
  public boolean inMethod() {
    return true;
  }

  @Override
  public boolean inOverview() {
    return true;
  }

  @Override
  public boolean inPackage() {
    return true;
  }

  @Override
  public boolean inType() {
    return true;
  }

  @Override
  public String toString(Tag[] tags) {
    StringBuilder out = new StringBuilder();
    for (Tag t : tags) {
      out.append(toString(t));
    }
    return out.toString();
  }

  // Utility methods for links
  // ----------------------------------------------------------------------------------------------

  /**
   * @param tag The tag to analyse
   * @return the package.class#member component of the tag.
   */
  private static String getLinkSpec(Tag tag) {
    String text = tag.text();
    int space = text.indexOf(' ');
    return space >= 0? text.substring(0, space) : text;
  }

  /**
   * @param tag The tag to analyse
   * @return the package component of the tag.
   */
  private static String getLinkPackage(Tag tag) {
    String spec = getLinkSpec(tag);
    int dot = spec.lastIndexOf('.');
    if (dot >= 0) // Package was included in reference
    return spec.substring(0, dot);
    else {
      // Get package from doc
      Doc doc = tag.holder();
      spec = doc.toString();
      if (doc.isClass() || doc.isMethod() || doc.isConstructor() || doc.isAnnotationType() || doc.isEnum()) {
        dot = spec.lastIndexOf('.');
      }
      return dot >= 0? spec.substring(0, dot) : spec;
    }
  }

  /**
   * @param tag The tag to analyse
   * @return the class name component of the tag.
   */
  private static String getClassName(Tag tag) {
    String name = getLinkSpec(tag);
    // remove package
    int dot = name.lastIndexOf('.');
    if (dot >= 0) { name = name.substring(dot+1); }
    // remove member
    int hash = name.indexOf('#');
    if (hash >= 0) { name = name.substring(0, hash); }
    if (name.length() == 0) {
      Doc doc = tag.holder();
      name = doc.toString();
      if (doc.isClass() || doc.isMethod() || doc.isConstructor() || doc.isAnnotationType() || doc.isEnum()) {
        // TODO
        dot = name.lastIndexOf('.');
      }
    }
    return name;
  }

  /**
   * @param tag The tag to analyse
   * @return the member component of the tag.
   */
  private static String getLinkMember(Tag tag) {
    String spec = getLinkSpec(tag);
    int hash = spec.indexOf('#');
    return hash >= 0? spec.substring(hash+1) : null;
  }

  /**
   * Returns the HTML link from the specified tag
   *
   * @param tag the tag to process.
   * @param css the css class.
   * @return the corresponding HTML
   */
  public static String toLinkString(Tag tag, String css) {
    // extract spec and label
    String text = tag.text();
    int space = text.indexOf(' ');
    String spec  = (space > 0)? text.substring(0, space) : text;
    String label = (space > 0)? text.substring(space+1) : text;

    // analyse spec
    String p = getLinkPackage(tag);
    String c = getClassName(tag);
    String m = getLinkMember(tag);

    // generate HTML link
    StringBuilder html = new StringBuilder();
    html.append("<a href=\"").append(spec).append("\" title=\"").append(label).append('"');
    html.append(" class=\"").append(css).append('"');
    html.append(" data-package=\"").append(p).append('"');
    html.append(" data-class=\"").append(c).append('"');
    if (m != null) {
      html.append(" data-method=\"").append(m).append('"');
    }
    html.append('>').append(label).append("</a>");
    return html.toString();
  }
}
