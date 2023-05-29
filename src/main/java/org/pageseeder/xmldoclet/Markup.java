package org.pageseeder.xmldoclet;

import com.sun.source.doctree.*;
import jdk.javadoc.doclet.Taglet;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Holds XML data
 */
public class Markup {

  private static List<String> BLOCKS = Arrays.asList("p", "ul", "ol", "table", "pre");

  private Stack<String> elements = new Stack<>();

  private StringBuilder xml = new StringBuilder();

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
    System.out.println(tree.getKind()+":"+tree.toString());

    // Plain text
    if (tree.getKind() == DocTree.Kind.TEXT) {
      if (this.elements.isEmpty()) {
        this.elements.push("p");
        this.xml.append("<p>");
      }
      String text = XMLNode.encodeElement(tree.toString());
      this.xml.append(text);
    }

    // Start element
    else if (tree.getKind() == DocTree.Kind.START_ELEMENT) {
      StartElementTree start = (StartElementTree)tree;
      String name = start.getName().toString();
      if (BLOCKS.contains(name)) {
        closeAllElements();
      } else if ("li".equals(name)) {
        // TODO Auto close lists
      }

      // TODO If block, check that all inline have been closed
      this.elements.push(name);
      this.xml.append('<').append(name);
      for (DocTree t : start.getAttributes()) {
        AttributeTree attribute = (AttributeTree)t;
        this.xml.append(' ').append(attribute.getName().toString()).append('=').append('"')
            .append(XMLNode.encodeAttribute(attribute.getValue().toString())).append('"');
      }
      xml.append('>');
    }

    // End element
    else if (tree.getKind() == DocTree.Kind.END_ELEMENT) {
      if (!elements.isEmpty()) {
        closeElement();
// TODO Warn if element don't match!
//        EndElementTree end = (EndElementTree) tree;
//        String name = end.getName().toString();
      }
    }

    // Inline tag
    else if (tree instanceof InlineTagTree) {
      InlineTagTree inline = (InlineTagTree)tree;
      Taglet taglet = this.options.getTagletForName(inline.getTagName());
//      TODO
      this.xml.append(tree.toString());
    }

    // Block tag
    else if (tree instanceof BlockTagTree) {
      BlockTagTree block = (BlockTagTree)tree;
      Taglet taglet = this.options.getTagletForName(block.getTagName());
      //      TODO
      this.xml.append(tree.toString());
    }

    // Anything else
    else {
      this.xml.append(tree.toString());
    }

  }

  @Override
  public String toString() {
    closeAllElements();
    // TODO Close any element before returning
    return this.xml.toString();
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

}
