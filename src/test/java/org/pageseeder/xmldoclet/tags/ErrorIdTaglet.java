package org.pageseeder.xmldoclet.tags;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import jdk.javadoc.doclet.Taglet;

import javax.lang.model.element.Element;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class ErrorIdTaglet implements Taglet {

  @Override
  public Set<Location> getAllowedLocations() {
    return EnumSet.allOf(Location.class);
  }

  @Override
  public boolean isInlineTag() {
    return false;
  }

  @Override
  public String getName() {
    return "error-id";
  }

  public String toString(UnknownBlockTagTree tag) {
    String text = tag.getContent().toString();
    int x = text.indexOf(' ');
    if (x >= 0) {
      return "<error id=\""+text.substring(0, x)+"\">"+text.substring(x).trim()+"</error>";
    } else {
      return "<error>"+text+"</error>";
    }
  }

  @Override
  public String toString(List<? extends DocTree> tags, Element element) {
    StringBuilder out = new StringBuilder();
    for (DocTree tag : tags) {
      if (tag instanceof UnknownBlockTagTree) {
        out.append(toString((UnknownBlockTagTree)tag));
      }
    }
    return out.toString();
  }
}
