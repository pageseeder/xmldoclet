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

import javax.lang.model.element.Element;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents an XML node.
 *
 * @author Christophe Lauret
 *
 * @version 1.0
 * @since 1.0
 */
public final class XMLNode {

  /**
   * Used in the toString method to provide a carriage-return + line-feed.
   */
  private static final String CRLF = System.lineSeparator();

  /**
   * The element name.
   */
  private final String name;

  /**
   * The source document the node corresponds to.
   */
  private Element doc;

  /**
   * The namespace URI of all nodes.
   */
  private static final String NAMESPACE_URI = "https://pageseeder.org/xmldoclet";

  /**
   * Sets the namespace prefix for this node.
   */
  private String namespacePrefix = "";

  /**
   * The attributes
   */
  private final Map<String, String> attributes;

  /**
   * The child nodes
   */
  private final List<XMLNode> children;

  /**
   * The content, which may include markup.
   */
  private final StringBuilder content;

  /**
   * The line in the source.
   */
  private final int line;

  /**
   * Constructs the XMLNode.
   *
   * @param name The name of the element
   * @param element The source java document the node belongs to.
   */
  public XMLNode(String name, Element element, int line) {
    this.name = name;
    this.doc = element;
    this.attributes = new HashMap<>();
    this.children = new ArrayList<>();
    this.content = new StringBuilder();
    this.line = line;
  }

  /**
   * Constructs the XMLNode.
   *
   * @param name The name of the element
   * @param element The source java document the node belongs to.
   */
  public XMLNode(String name, Element element) {
    this(name, element, -1);
  }

  /**
   * Constructs the XMLNode.
   *
   * @param name The name of the element
   */
  public XMLNode(String name) {
    this(name, null);
  }

  /**
   * Adds an attribute to the node
   *
   * @param name  the name of the attribute.
   * @param value the value for the attribute
   */
  public XMLNode attribute(String name, String value) {
    if (value != null) {
      this.attributes.put(name, value);
    }
    return this;
  }

  /**
   * Adds an attribute to the node
   *
   * @param name  the name of the attribute
   * @param value the value for the attribute
   */
  public XMLNode attribute(String name, boolean value) {
    this.attributes.put(name, Boolean.toString(value));
    return this;
  }

  /**
   * Adds a list of child nodes.
   *
   * @param nodes The nodes to add.
   * @return this node for chaining.
   */
  public XMLNode child(List<XMLNode> nodes) {
    for (XMLNode node : nodes) {
      this.children.add(node);
      node.setDoc(this.doc);
    }
    return this;
  }

  /**
   * Adds a child node.
   *
   * @param node The node
   * @return this node for chaining.
   */
  public XMLNode child(XMLNode node) {
    if (node != null) {
      this.children.add(node);
      node.setDoc(this.doc);
    }
    return this;
  }

  /**
   * Set the doc for the node and its descendants.
   *
   * @param element the doc
   */
  private void setDoc(Element element) {
    if (element == null) return;
    if (this.doc == null) {
      this.doc = element;
    }
    for (XMLNode child : this.children) {
      child.setDoc(element);
    }
  }

  /**
   * Adds text to the content of the node.
   *
   * @param text The text.
   * @return this node for chaining.
   */
  public XMLNode text(String text) {
    if (text != null) {
      this.content.append(text);
    }
    return this;
  }

  /**
   * Returns the specified attributed.
   *
   * @param name The key for the value to be retrieved.
   * @return The value stored in the attribute hash for the given key.
   */
  public String getAttribute(String name) {
    return this.attributes.get(name);
  }

  /**
   * @return The name of the node.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Saves this XML node to the directory specified.
   *
   * @param dir  the directory to save this node to.
   * @param name the name of the file
   *
   * @param encoding the character encoding used for the output.
   */
  public void save(File dir, String name, Charset encoding, String nsPrefix) {
    try {
      String xmlDeclaration = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>" + CRLF;

      if (nsPrefix != null && !nsPrefix.isEmpty()) {
        this.namespacePrefix = nsPrefix;
        this.attribute("xmlns:" + this.namespacePrefix, NAMESPACE_URI);
        this.namespacePrefix = this.namespacePrefix + ":";
      }

      if (!dir.exists()) {
        dir.mkdirs();
      }

      // Write out to the file
      File file = new File(dir, name);
      try (OutputStreamWriter out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), encoding)) {
        out.write(xmlDeclaration);
        out.write(this.toString(""));
      }

    } catch (IOException ex) {
      System.err.println("Could not create '" + dir +File.pathSeparator+ name + "'");
      ex.printStackTrace();
    }
  }

  /**
   * Converts the XML node to a String.
   *
   * @param tabs The tabs used for indentation.
   * @return the String representation of this node and its children.
   */
  public String toString(String tabs) {
    StringBuilder out = new StringBuilder();

    // Open element
    out.append(tabs).append("<").append(this.namespacePrefix).append(this.name);

    // Serialise the attributes
    for (Entry<String, String> att : this.attributes.entrySet()) {
      out.append(" ").append(att.getKey()).append("=\"").append(encodeAttribute(att.getValue())).append("\"");
    }

    // Close if empty element (no text node AND no children)
    if (this.content.length() <= 0 && this.children.isEmpty()) {
      out.append(" />").append(CRLF);
      return out.toString();
    }

    // Close open tag
    out.append(">");
    if (!this.children.isEmpty()) {
      out.append(CRLF);
    }

    // This node has text
    if (this.content.length() > 0) {
      // Wrapping text in a separate node allows for good presentation of data with out adding extra data.
      out.append(encode(this.content.toString(), this.doc, this.line));
    }

    // Serialise children
    for (XMLNode node : this.children) {
      out.append(node.toString(tabs + "\t"));
    }

    // Close element
    if (!this.children.isEmpty()) {
      out.append(tabs);
    }
    out.append("</").append(this.namespacePrefix).append(this.name).append(">")
            .append(CRLF).append("class".equalsIgnoreCase(this.name) ? CRLF : "");

    return out.toString();
  }

  /**
   * Encodes strings as XML. Check for <, >, ', ", &.
   *
   * @param text The input string.
   * @param element The source java document the node belongs to.
   * @return The encoded string.
   */
  private static String encode(String text, Element element, int line) {
    if (text.indexOf('<') >= 0) return text;
    else return encodeElement(text);
  }

  /**
   * Encodes strings as XML. Check for {@literal  <, >, ', ", & }.
   *
   * @param in The input string.
   * @return The encoded string.
   */
  static String encodeElement(String in) {
    final int length = in.length();
    StringBuilder out = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      char c = in.charAt(i);
      switch (c) {
        case '&': out.append("&amp;"); break;
        case '<': out.append("&lt;"); break;
        default:  out.append(c);
      }
    }
    return out.toString();
  }

  /**
   * Encodes strings as XML. Check for <, >, ', ", &.
   *
   * @param in The input string.
   * @return The encoded string.
   */
  static String encodeAttribute(String in) {
    final int length = in.length();
    StringBuilder out = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      char c = in.charAt(i);
      switch (c) {
        case '\'': out.append("&apos;"); break;
        case '"':  out.append("&quot;"); break;
        case '>':  out.append("&gt;"); break;
        case '<':  out.append("&lt;"); break;
        default:   out.append(c);
      }
    }
    return out.toString();
  }

  private static int countLines(String text) {
    int lineCount = 0;
    int i = text.indexOf('\n');
    while (i != -1) {
      lineCount++;
      i = text.indexOf('\n', i+1);
    }
    return lineCount;
  }

}
