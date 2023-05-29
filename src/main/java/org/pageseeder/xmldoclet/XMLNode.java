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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.lang.model.element.Element;

/**
 * Represents an XML node.
 *
 * @author Christophe Lauret
 * @version 1.0
 */
public final class XMLNode {

  /**
   * Used in the toString method to provide a carriage-return + line-feed.
   */
  private static final String CRLF = System.getProperty("line.separator");

  /**
   * To print nowhere.
   */
  private static final PrintWriter VOID_WRITER = new PrintWriter(new VoidWriter());

  /**
   * The element name.
   */
  private final String _name;

  /**
   * The source document the node corresponds to.
   */
  private Element _doc = null;

  /**
   * The namespace URI of all nodes.
   */
  private static final String NAMESPACE_URI = "https://pageseeder.org/xmldoclet";

  /**
   * Sets the namespace prefix for this node.
   */
  private String _namespacePrefix = "";

  /**
   * The attributes
   */
  private final Map<String, String> _attributes;

  /**
   * The child nodes
   */
  private final List<XMLNode> _children;

  /**
   * The content, which may include markup.
   */
  private final StringBuilder _content;

  /**
   * The line in the source.
   */
  private final int _line;

  /**
   * Constructs the XMLNode.
   *
   * @param name The name of the element
   * @param element The source java document the node belongs to.
   */
  public XMLNode(String name, Element element, int line) {
    this._name = name;
    this._doc = element;
    this._attributes = new HashMap<>();
    this._children = new ArrayList<>();
    this._content = new StringBuilder();
    this._line = line;
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
      this._attributes.put(name, value);
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
    this._attributes.put(name, Boolean.toString(value));
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
      this._children.add(node);
      node.setDoc(this._doc);
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
      this._children.add(node);
      node.setDoc(this._doc);
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
    if (this._doc == null) {
      this._doc = element;
    }
    for (XMLNode child : this._children) {
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
      this._content.append(text);
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
    return this._attributes.get(name);
  }

  /**
   * @return The name of the node.
   */
  public String getName() {
    return this._name;
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
      String _xmlDeclaration = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>" + CRLF;

      if (nsPrefix != null && !"".equals(nsPrefix)) {
        this._namespacePrefix = nsPrefix;
        this.attribute("xmlns:" + this._namespacePrefix, NAMESPACE_URI);
        this._namespacePrefix = this._namespacePrefix + ":";
      }

      // Write out to the file
      File file = new File(dir, name);
      FileOutputStream os = new FileOutputStream(file);
      BufferedOutputStream bos = new BufferedOutputStream(os);
      OutputStreamWriter out = new OutputStreamWriter(bos, encoding);
      out.write(_xmlDeclaration);
      out.write(this.toString(""));
      out.close();

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
    out.append(tabs).append("<").append(this._namespacePrefix).append(this._name);

    // Serialise the attributes
    for (Entry<String, String> att : this._attributes.entrySet()) {
      out.append(" ").append(att.getKey()).append("=\"").append(encodeAttribute(att.getValue())).append("\"");
    }

    // Close if empty element (no text node AND no children)
    if (this._content.length() <= 0 && this._children.isEmpty()) {
      out.append(" />").append(CRLF);
      return out.toString();
    }

    // Close open tag
    out.append(">");
    if (!this._children.isEmpty()) {
      out.append(CRLF);
    }

    // This node has text
    if (this._content.length() > 0) {
      // Wrapping text in a separate node allows for good presentation of data with out adding extra data.
      out.append(encode(this._content.toString(), this._doc, this._line));
    }

    // Serialise children
    for (XMLNode node : this._children) {
      out.append(node.toString(tabs + "\t"));
    }

    // Close element
    if (!this._children.isEmpty()) {
      out.append(tabs);
    }
    out.append("</").append(this._namespacePrefix).append(this._name).append(">")
            .append(CRLF).append("class".equalsIgnoreCase(this._name) ? CRLF : "");

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
    if (text.indexOf('<') >= 0) return tidy(text, element, line);
    else return encodeElement(text);
  }

  /**
   * Encodes strings as XML. Check for <, >, ', ", &.
   *
   * @param in The input string.
   * @return The encoded string.
   */
  protected static String encodeElement(String in) {
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
  protected static String encodeAttribute(String in) {
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

  /**
   * Tidy the text for inclusion as a comment description.
   *
   * @param text the HTML body text to tidy
   * @param doc  The source java document the node belongs to.
   * @return the tidied HTML
   */
  private static String tidy(String text, Element doc, int line) {
    // TODO FIXME
    return "";
//    Tidy tidy = new Tidy();
//    tidy.setXmlOut(true);
//    tidy.setEncloseText(false);
//    tidy.setQuiet(true);
//    tidy.setEscapeCdata(false);
//    tidy.setIndentCdata(false);
//    tidy.setTrimEmptyElements(false);
//    tidy.setDropProprietaryAttributes(false);
//    tidy.setErrout(VOID_WRITER);
//
//    // Tidy wants a full HTML document...
//    StringBuilder in = new StringBuilder();
//    in.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
//    in.append(" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
//    in.append("<html><head><title>Remove</title></head><body>");
//    in.append(text);
//    in.append("</body></html>");
//
//    // Count new lines
//    int baseline = line;
//    if (line != -1) {
//      // TODO Compute the lines
//// FIXME     baseline = line - countLines(doc.getRawCommentText()) -2;
//    }
//    tidy.setMessageListener(new Listener(doc, baseline));
//
//    // Tidy
//    StringWriter w = new StringWriter();
//    tidy.parse(new StringReader(in.toString()), w);
//    String out = w.toString();
//
//    // Get output
//    int start = out.indexOf("<body>");
//    int end = out.indexOf("</body>");
//    if (start != -1 && end != -1) return out.substring(start+6, end);
//
//    // Second chance try with XML
//    tidy.setXmlTags(true);
//    in.setLength(0);
//    in.append(text);
//
//    // Tidy
//    w = new StringWriter();
//    tidy.parse(new StringReader(in.toString()), w);
//
//    // Report errors
//    return w.toString();
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

  /**
   * A listener to capture errors thrown by tidy.
   */
//  private static class Listener implements TidyMessageListener  {
//
//    /**
//     * Error code returned by Tidy for unknown attributes.
//     */
//    private static final int UNKNOWN_ATTRIBUTE = 48;
//
//    /**
//     * The current document being processing.
//     */
//    private final Element _doc;
//
//    /**
//     * The line where the source that is being tidied starts.
//     */
//    private final int _baseline;
//
//    /**
//     * Creates a new listener for tidy
//     *
//     * @param doc      The source java doc
//     * @param baseline The line where the source that is being tidied starts.
//     */
//    public Listener(Element doc, int baseline) {
//      this._doc = doc;
//      this._baseline = baseline;
//    }
//
//    @Override
//    public void messageReceived(TidyMessage message) {
//      int code = message.getErrorCode();
//      int line = this._baseline >= 0? this._baseline + message.getLine() : -1;
//      if (code != UNKNOWN_ATTRIBUTE) {
//        Level level = message.getLevel();
//        String prefix = "["+level+"]"+(level == Level.ERROR? "   " : " ");
//        System.err.println(prefix+this._doc.toString()+":" +(line != -1? "L"+line+" " : " ")+message.getMessage());
//      }
//    }
//
//  };

  /**
   * Does nothing.
   */
  private static class VoidWriter extends Writer {

    @Override
    public void write(char[] cbuf) {
    }

    @Override
    public void write(int c) {
    }

    @Override
    public void write(String str, int off, int len) {
    }

    @Override
    public void write(char[] cbuf, int off, int len)  {
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

  }
}
