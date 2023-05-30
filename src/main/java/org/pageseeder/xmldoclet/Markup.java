package org.pageseeder.xmldoclet;

import com.sun.source.doctree.*;
import jdk.javadoc.doclet.Taglet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Holds XML data
 */
public class Markup {

  private static final List<String> BLOCKS = Arrays.asList("p", "ul", "ol", "table", "pre");

  private final Stack<String> elements = new Stack<>();

  private final StringBuilder xml = new StringBuilder();

  private final Options options;

  private boolean hasBlocks;

  public Markup(Options options, boolean hasBlocks) {
    this.options = options;
    this.hasBlocks = hasBlocks;
  }

  public static String asString(List<? extends DocTree> trees, Options options, boolean hasBlocks) {
    Markup markup = new Markup(options, hasBlocks);
    markup.addAll(trees);
    return markup.toString();
  }

  void addAll(List<? extends DocTree> trees) {
    for (DocTree tree : trees) {
      add(tree);
    }
  }

  void add(DocTree tree) {
    if (tree.getKind() == DocTree.Kind.TEXT) {
      addText(tree.toString());
    } else if (tree.getKind() == DocTree.Kind.START_ELEMENT) {
      addStartElement((StartElementTree)tree);
    } else if (tree.getKind() == DocTree.Kind.END_ELEMENT) {
      addEndElement((EndElementTree)tree);
    } else if (tree instanceof InlineTagTree) {
      addInlineTag((InlineTagTree)tree);
    } else if (tree instanceof BlockTagTree) {
      addBlockTag((BlockTagTree)tree);
    } else {
      System.err.println(tree.getKind()+":"+tree);
      this.xml.append(tree);
    }
  }

  @Override
  public String toString() {
    closeAllElements();
    // TODO Close any element before returning
    return this.xml.toString();
  }

  /**
   * Add text to the markup.
   *
   * @param text The plain text
   */
  private void addText(String text) {
    String safe = XMLNode.encodeElement(text);
    // Ensure we start with a paragraph in block mode, unless the text is just whitespace
    if (this.hasBlocks && this.elements.isEmpty() && !text.matches("\\s+")) {
      this.elements.push("p");
      this.xml.append("<p>");
    }
    this.xml.append(safe);
  }

  /**
   * Add a start element to te markup
   *
   * @param start The start element.
   */
  private void addStartElement(StartElementTree start) {
    String name = start.getName().toString();
    String parent = getContext();
    if (BLOCKS.contains(name)) {
      // If block, check that all previous elements have been closed
      closeAllElements();
    } else if ("li".equals(name)) {
      // Ensure parent is list
      while (parent != null && !("ul".equals(parent) || "ol".equals(parent))) {
        closeElement();
        parent = getContext();
      }
      if (parent == null) {
        this.elements.push("ul");
        this.xml.append("<ul>");
      }
    } else if ("td".equals(name) || "th".equals(name)) {
      // Ensure parent is row
      while (parent != null && !"tr".equals(parent)) {
        closeElement();
        parent = getContext();
      }
      if (parent == null) {
        this.elements.push("table");
        this.elements.push("tr");
        this.xml.append("<table><tr>");
      }
    } else if ("tr".equals(name)) {
      // Ensure parent is table
      while (parent != null && !("thead".equals(parent) || "tbody".equals(parent) || "tfoot".equals(parent) || "table".equals(parent))) {
        closeElement();
        parent = getContext();
      }
      this.elements.push("table");
      this.xml.append("<table>");
    }

    this.elements.push(name);
    this.xml.append('<').append(name);
    for (DocTree t : start.getAttributes()) {
      AttributeTree attribute = (AttributeTree)t;
      this.xml.append(' ').append(attribute.getName().toString()).append('=').append('"')
          .append(XMLNode.encodeAttribute(attribute.getValue().toString())).append('"');
    }
    xml.append('>');
  }

  /**
   * Add an end element to the markup
   *
   * @param end The end element.
   */
  private void addEndElement(EndElementTree end) {
    if (!elements.isEmpty()) {
      String name = end.getName().toString();
      String parent = getContext();
      String closed = null;
      while (parent != null && !name.equals(closed)) {
        closed = closeElement();
      }
      if (!name.equals(closed)) {
        System.err.println("Unable to close "+name);
      }
    }
  }

  private void addInlineTag(InlineTagTree inlineTag) {
    Taglet taglet = this.options.getTagletForName("@"+inlineTag.getTagName());
    if (taglet != null) {
      String val = taglet.toString(Collections.singletonList(inlineTag), null);
      System.err.println(inlineTag.getKind()+":"+inlineTag+" -> "+taglet+" => "+val);
      this.xml.append(taglet.toString(Collections.singletonList(inlineTag), null));
    } else {
      // TODO Unexpected tag
      this.xml.append(inlineTag.toString());
    }
  }

  private void addBlockTag(BlockTagTree blockTag) {
    Taglet taglet = this.options.getTagletForName(blockTag.getTagName());
    System.err.println("(b) "+blockTag.getKind()+":"+blockTag.getTagName()+" -> "+taglet);
    //      TODO
    if (taglet != null) {
      this.xml.append(taglet.toString(Collections.singletonList(blockTag), null));
    } else {
      this.xml.append(blockTag.toString());
    }
  }

  private String closeElement() {
    String name = elements.pop();
    xml.append("</").append(name).append('>');
    return name;
  }

  private void closeAllElements() {
    while (!this.elements.isEmpty()) {
      closeElement();
    }
  }

  private String getContext() {
    return this.elements.empty() ? null : this.elements.peek();
  }


}
