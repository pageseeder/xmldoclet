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

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.Taglet;
import org.pageseeder.xmldoclet.options.*;

/**
 * Container for the options for the XML Doclet.
 *
 * @author Christophe Lauret
 *
 * @version 21 June 2010
 */
public final class Options {

  /**
   * An empty array constant for reuse.
   */
  private static final String[] EMPTY_ARRAY = new String[]{};

  /**
   * The default encoding for the output
   */
  private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

  /**
   * The default filename for the output.
   */
  private static final String DEFAULT_FILENAME = "xmldoclet.xml";

  /**
   * Determines whether the output is a single file or multiple files.
   *
   * <p>Populated from the command line via the "-multiple" flag.
   */
  private boolean multipleFiles = false;

  /**
   * Determines whether files are organised as subfolders or all in the same folder.
   *
   * <p>Populated from the command line via the "-subfolders" flag.
   */
  private boolean subFolders = false;

  /**
   * Determines the directory where output is placed.
   *
   * <p>Populated from the command line via the "-d [directory]" flag.
   */
  private File directory;

  /**
   * The output encoding of the XML files.
   */
  private Charset encoding = DEFAULT_CHARSET;

  /**
   * Filter classes extending the specified class.
   */
  private String extendsFilter = null;

  /**
   * Filter classes implementing the specified class.
   */
  private String implementsFilter = null;

  /**
   * Filter classes with the specified annotation.
   */
  private String annotationFilter = null;

  /**
   * The taglets loaded by this doclet.
   */
  private final Map<String, Taglet> taglets = new HashMap<>();

  /**
   * Name of the file - used for single output only.
   *
   * <p>Populated from the command line via the "-filename [file]" flag.
   */
  private String filename = DEFAULT_FILENAME;

  /**
   * Creates new options.
   */
  public Options() {
    // Load the standard taglets
    for (BlockTag t : BlockTag.values()) {
      this.taglets.put(t.getName(), t);
    }
    for (InlineTag t : InlineTag.values()) {
      this.taglets.put("@"+t.getName(), t);
    }
  }

  /**
   * Indicates whether these options should use multiple files.
   */
  public boolean useMultipleFiles() {
    return this.multipleFiles;
  }

  /**
   * Indicates whether to organise files as subfolders for packages.
   */
  public boolean useSubFolders() {
    return this.subFolders;
  }

  /**
   * Returns the charset to use to encode the output.
   *
   * @return the charset to use to encode the output.
   */
  public Charset getEncoding() {
    return this.encoding;
  }

  /**
   * Returns the directory where to store the files.
   *
   * @return where to store the files.
   */
  public File getDirectory() {
    return this.directory;
  }

  /**
   * Returns the name of the file for single output.
   *
   * @return the name of the file for single output.
   */
  public String getFilename() {
    return this.filename;
  }

  /**
   * Returns the taglet instance for the specified tag name.
   *
   * @param name The name of the tag.
   * @return The corresponding <code>Taglet</code> or <code>null</code>.
   */
  public Taglet getTagletForName(String name) {
    for (String n : this.taglets.keySet()) {
      if (n.equals(name)) return this.taglets.get(n);
    }
    return null;
  }

  /**
   * Indicates whether these options specify a filter.
   *
   * @return <code>true</code> if the class must implement or extends or have a specific annotation.
   *         <code>false</code> otherwise.
   */
  public boolean hasFilter() {
    return this.extendsFilter != null || this.implementsFilter != null || this.annotationFilter != null;
  }

  /**
   * Filters the included set of classes by checking whether the given class matches all of
   * the specified '-extends', '-implements' and '-annotated' options.
   *
   * @param doc the class documentation.
   * @return <code>true</code> if the class should be included; <code>false</code> otherwise.
   */
  public boolean filter(TypeElement doc) {
    boolean included = true;

    // Extends
    if (this.extendsFilter != null) {
      included = filterExtends(doc, this.extendsFilter);
    }
    // Implements
    if (this.implementsFilter != null) {
      included = included && filterImplements(doc, this.implementsFilter);
    }
    // Annotation
    if (this.annotationFilter != null) {
      included = included && filterAnnotated(doc, this.annotationFilter);
    }

    // No filtering
    return included;
  }

  @Override
  public String toString() {
    return super.toString();
  }

  public void setMultipleFiles(boolean multipleFiles) {
    this.multipleFiles = multipleFiles;
  }

  public void setAnnotationFilter(String annotationFilter) {
    this.annotationFilter = annotationFilter;
  }

  public void setDirectory(File directory) {
    this.directory = directory;
  }

  public void setEncoding(Charset encoding) {
    this.encoding = encoding;
  }

  public void setExtendsFilter(String extendsFilter) {
    this.extendsFilter = extendsFilter;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public void setImplementsFilter(String implementsFilter) {
    this.implementsFilter = implementsFilter;
  }

  public void setSubFolders(boolean subFolders) {
    this.subFolders = subFolders;
  }

  public String getExtendsFilter() {
    return extendsFilter;
  }

  // static methods for use by Doclet =============================================================

  public Set<? extends Doclet.Option> allOptions() {
    Set<Doclet.Option> options = new HashSet<>();
    options.add(new AnnotatedOption(this));
    options.add(new DirectoryOption(this));
    options.add(new DocencodingOption(this));
    options.add(new ExtendsOption(this));
    options.add(new FilenameOption(this));
    options.add(new ImplementsOption(this));
    options.add(new MultipleOption(this));
    options.add(new SubfoldersOption(this));
    options.add(new TagletOption(this));
    options.add(new TagOption(this));
    return options;
  }

  /**
   * Filters the included set of classes by checking whether the given class matches the '-extends' option.
   *
   * @param doc  the class documentation.
   * @param base the class to extend.
   * @return <code>true</code> if the class should be included; <code>false</code> otherwise.
   */
  private static boolean filterExtends(TypeElement doc, String base) {
    TypeMirror superclass = doc.getSuperclass();
    return superclass != null && base.equals(superclass.toString());
  }

  /**
   * Filters the included set of classes by checking whether the given class matches the '-implements' option.
   *
   * @param doc   the class documentation.
   * @param iface the interface to implement.
   * @return <code>true</code> if the class should be included; <code>false</code> otherwise.
   */
  private static boolean filterImplements(TypeElement doc, String iface) {
    List<? extends TypeMirror> interfaces = doc.getInterfaces();
    for (TypeMirror i : interfaces) {
      if (iface.equals(i.toString())) return true;
    }
    return false;
  }

  /**
   * Filters the included set of classes by checking whether the given class matches the '-annotated' option.
   *
   * @param doc        the class documentation.
   * @param annotation the annotation to match.
   * @return <code>true</code> if the class should be included; <code>false</code> otherwise.
   */
  private static boolean filterAnnotated(TypeElement doc, String annotation) {
    List<? extends AnnotationMirror> annotations = doc.getAnnotationMirrors();
    for (AnnotationMirror i : annotations) {
      // TODO if (annotation.equals(i.getAnnotationType().qualifiedName())) return true;
      if (annotation.equals(i.getAnnotationType().asElement().getSimpleName().toString())) return true;
    }
    return false;
  }
}
