package org.pageseeder.xmldoclet.tags;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import jdk.javadoc.doclet.Taglet;

import javax.lang.model.element.Element;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class GlossaryTaglet implements Taglet {

  @Override
  public Set<Location> getAllowedLocations() {
    return EnumSet.allOf(Location.class);
  }

  @Override
  public boolean isInlineTag() {
    return true;
  }

  @Override
  public String getName() {
    return "glossary";
  }

  public String toString(UnknownInlineTagTree tag) {
    return "<glossary>"+tag.getContent()+"</glossary>";
  }

  @Override
  public String toString(List<? extends DocTree> tags, Element element) {
    StringBuilder out = new StringBuilder();
    for (DocTree tag : tags) {
      if (tag instanceof UnknownInlineTagTree) {
        out.append(toString((UnknownInlineTagTree)tag));
      }
    }
    return out.toString();
  }
}
