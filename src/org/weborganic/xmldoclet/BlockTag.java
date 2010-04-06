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

    /**
     * {@inheritDoc} 
     */
    public String toString(Tag tag) {
      return "<author>"+tag.text()+"</author>";
    }

    /**
     * {@inheritDoc}
     */
    public String toString(Tag[] tags) {
      StringBuilder out = new StringBuilder();
      for (Tag t : tags) {
        out.append(toString(t));
      }
      return out.toString();
    }

  },

  DEPRECATED("deprecated", false){

    /**
     * {@inheritDoc} 
     */
    public String toString(Tag tag) {
      return tag.text();
    }

    /**
     * {@inheritDoc}
     */
    public String toString(Tag[] tags) {
      StringBuilder out = new StringBuilder();
      for (Tag t : tags) {
        out.append(toString(t));
      }
      return out.toString();
    }

  },

  SERIAL("serial", false){
    /**
     * {@inheritDoc} 
     */
    public String toString(Tag tag) {
      return tag.text();
    }

    /**
     * {@inheritDoc}
     */
    public String toString(Tag[] tags) {
      StringBuilder out = new StringBuilder();
      for (Tag t : tags) {
        out.append(toString(t));
      }
      return out.toString();
    }

  },

  SERIALDATA("serialData", false){
    /**
     * {@inheritDoc} 
     */
    public String toString(Tag tag) {
      return tag.text();
    }

    /**
     * {@inheritDoc}
     */
    public String toString(Tag[] tags) {
      StringBuilder out = new StringBuilder();
      for (Tag t : tags) {
        out.append(toString(t));
      }
      return out.toString();
    }

  },
  
  SERIALFIELD("serialField", false){
    /**
     * {@inheritDoc} 
     */
    public String toString(Tag tag) {
      return tag.text();
    }

    /**
     * {@inheritDoc}
     */
    public String toString(Tag[] tags) {
      StringBuilder out = new StringBuilder();
      for (Tag t : tags) {
        out.append(toString(t));
      }
      return out.toString();
    }

  },

  SINCE("since", false){
    /**
     * {@inheritDoc} 
     */
    public String toString(Tag tag) {
      return tag.text();
    }

    /**
     * {@inheritDoc}
     */
    public String toString(Tag[] tags) {
      StringBuilder out = new StringBuilder();
      for (Tag t : tags) {
        out.append(toString(t));
      }
      return out.toString();
    }

  },

  
  VERSION("version", false){

    /**
     * {@inheritDoc} 
     */
    public String toString(Tag tag) {
      return tag.text();
    }

    /**
     * {@inheritDoc}
     */
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
   * Whether this is an inline tag;
   */  
  private final boolean _inline;

  /**
   * Creates a new tag.
   * 
   * @param name   The name of the tag.
   * @param inline <code>true</code> for an inline tag; <code>false</code> otherwise.
   */
  BlockTag(String name, boolean inline) {
    this._name = name;
    this._inline = inline;
  }

  /** {@inheritDoc} */
  public String getName() {
    return this._name;
  }

  /** {@inheritDoc} */
  public boolean isInlineTag() {
    return this._inline;
  }

  /** {@inheritDoc} */
  public boolean inConstructor() {
    return true;
  }

  /** {@inheritDoc} */
  public boolean inField(){
    return true;
  }
  
  /** {@inheritDoc} */
  public boolean inMethod() {
    return true;
  }
  
  /** {@inheritDoc} */
  public boolean inOverview() {
    return true;
  }
  
  /** {@inheritDoc} */
  public boolean inPackage() {
    return true;
  }
  
  /** {@inheritDoc} */
  public boolean inType() {
    return true;
  }

}
