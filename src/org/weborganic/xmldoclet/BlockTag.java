package org.weborganic.xmldoclet;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * A collection of taglets to support the standard javadoc tags.
 *
 * @author Christophe Lauret
 * @version 3 April 2010
 */
public enum BlockTag implements Taglet {

  /**
   * Taglet for the <code>@author</code> tag.
   */
  AUTHOR("author", false) {

    @Override
    public XMLNode toXMLNode(Tag tag) {
      return new XMLNode("author").text(tag.text());
    }

    @Override
    public String toString(Tag tag) {
      return "<author>"+tag.text()+"</author>";
    }

    @Override
    public String toString(Tag[] tags) {
      StringBuilder out = new StringBuilder();
      for (Tag t : tags) {
        out.append(toString(t));
      }
      return out.toString();
    }

  },

  DEPRECATED("deprecated", false){

    @Override
    public XMLNode toXMLNode(Tag tag) {
      return new XMLNode("deprecated").text(tag.text());
    }

    @Override
    public String toString(Tag tag) {
      return tag.text();
    }

    @Override
    public String toString(Tag[] tags) {
      StringBuilder out = new StringBuilder();
      for (Tag t : tags) {
        out.append(toString(t));
      }
      return out.toString();
    }

  },

  SERIAL("serial", false){

    @Override
    public XMLNode toXMLNode(Tag tag) {
      return new XMLNode("serial").text(tag.text());
    }

    @Override
    public String toString(Tag tag) {
      return tag.text();
    }

    @Override
    public String toString(Tag[] tags) {
      StringBuilder out = new StringBuilder();
      for (Tag t : tags) {
        out.append(toString(t));
      }
      return out.toString();
    }

  },

  SERIALDATA("serialData", false){

    @Override
    public XMLNode toXMLNode(Tag tag) {
      return new XMLNode("serialData").text(tag.text());
    }

    @Override
    public String toString(Tag tag) {
      return tag.text();
    }

    @Override
    public String toString(Tag[] tags) {
      StringBuilder out = new StringBuilder();
      for (Tag t : tags) {
        out.append(toString(t));
      }
      return out.toString();
    }

  },

  SERIALFIELD("serialField", false){

    @Override
    public XMLNode toXMLNode(Tag tag) {
      return new XMLNode("serialField").text(tag.text());
    }

    @Override
    public String toString(Tag tag) {
      return tag.text();
    }

    @Override
    public String toString(Tag[] tags) {
      StringBuilder out = new StringBuilder();
      for (Tag t : tags) {
        out.append(toString(t));
      }
      return out.toString();
    }

  },

  SINCE("since", false){

    @Override
    public XMLNode toXMLNode(Tag tag) {
      return new XMLNode("since").text(tag.text());
    }

    @Override
    public String toString(Tag tag) {
      return tag.text();
    }

    @Override
    public String toString(Tag[] tags) {
      StringBuilder out = new StringBuilder();
      for (Tag t : tags) {
        out.append(toString(t));
      }
      return out.toString();
    }

  },


  VERSION("version", false){

    @Override
    public XMLNode toXMLNode(Tag tag) {
      return new XMLNode("version").text(tag.text());
    }

    @Override
    public String toString(Tag tag) {
      return "<version>"+tag.text()+"</version>";
    }

    @Override
    public String toString(Tag[] tags) {
      StringBuilder out = new StringBuilder();
      for (Tag t : tags) {
        out.append(toString(t));
      }
      return out.toString();
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
   * @param inline <code>true</code> for an inline tag; <code>false</code> otherwise.
   */
  BlockTag(String name, boolean inline) {
    this._name = name;
  }

  /**
   * Returns the XML node corresponding to this taglet.
   */
  public abstract XMLNode toXMLNode(Tag tag);

  @Override
  public String getName() {
    return this._name;
  }

  @Override
  public boolean isInlineTag() {
    return false;
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

}
