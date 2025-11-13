package org.pageseeder.xmldoclet;

import com.sun.source.doctree.*;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.Taglet;
import org.eclipse.jdt.annotation.Nullable;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.util.*;

/**
 * Holds XML data
 */
public class Markup {

  private static final List<String> BLOCKS = List.of("p", "ul", "ol", "table", "pre", "h1", "h2", "h3", "h4", "h5", "h6");

  private final Deque<String> elements = new ArrayDeque<>();

  private final StringBuilder xml = new StringBuilder();

  private final Options options;

  private final Reporter reporter;

  private final boolean hasBlocks;

  private @Nullable Element typeElement = null;

  public Markup(Options options, Reporter reporter, boolean hasBlocks) {
    this.options = options;
    this.reporter = reporter;
    this.hasBlocks = hasBlocks;
  }

  /**
   * Converts the given element and associated documentation trees into a formatted string
   * representation according to the provided options.
   *
   * @param element    The element to be represented as a string.
   * @param trees      A list of documentation trees associated with the element.
   * @param options    Configuration options affecting the output.
   * @param reporter   A reporter used for logging or error reporting during processing.
   * @param hasBlocks  Indicates whether block elements are present in the input data.
   *
   * @return A formatted string representation of the element and its associated documentation trees.
   */
  public static String toString(Element element, List<? extends DocTree> trees, Options options, Reporter reporter, boolean hasBlocks) {
    Markup markup = new Markup(options, reporter, hasBlocks);
    markup.typeElement = element;
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
      if (!this.elements.contains(name)) {
        this.reporter.print(Diagnostic.Kind.WARNING, typeElement, "Found </"+name+"> but element was never open, ignoring");
      } else {
        while (parent != null && !name.equals(closed)) {
          closed = closeElement();
        }
      }
    }
  }

  private void addInlineTag(InlineTagTree inlineTag) {
    Taglet taglet = this.options.getTagletForName("@"+inlineTag.getTagName());
    if (taglet != null) {
      String val = taglet.toString(Collections.singletonList(inlineTag), null);
      this.xml.append(taglet.toString(Collections.singletonList(inlineTag), null));
    } else {
      // TODO Unexpected tag
      this.xml.append(inlineTag.toString());
    }
  }

  private void addBlockTag(BlockTagTree blockTag) {
    Taglet taglet = this.options.getTagletForName(blockTag.getTagName());
    //      TODO
    if (taglet != null) {
      this.xml.append(taglet.toString(Collections.singletonList(blockTag), null));
    } else {
      this.xml.append(blockTag);
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

  private @Nullable String getContext() {
    return this.elements.isEmpty() ? null : this.elements.peek();
  }

}
