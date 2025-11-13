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
import java.util.*;

import javax.lang.model.element.TypeElement;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.Taglet;
import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.xmldoclet.options.*;

/**
 * Container for the options for the XML Doclet.
 *
 * @author Christophe Lauret
 * @version 1.0
 */
public final class Options {

  /**
   * The taglets loaded by this doclet.
   */
  private final Map<String, Taglet> taglets = new HashMap<>();

  private final Reporter reporter;

  private final AnnotatedOption annotatedOption;

  private final DirectoryOption directoryOption;

  private final DocencodingOption docencodingOption;

  private final ExtendsOption extendsOption;

  private final FilenameOption filenameOption;

  private final ImplementsOption implementsOption;

  private final MultipleOption multipleOption;

  private final SubfoldersOption subfoldersOption;

  private final TagletOption tagletOption;

  private final TagOption tagOption;

  /**
   * Creates new options.
   *
   * @param reporter The reporter to use.
   */
  public Options(Reporter reporter) {
    this.reporter = reporter;
    this.annotatedOption = new AnnotatedOption(reporter);
    this.directoryOption = new DirectoryOption(reporter);
    this.docencodingOption = new DocencodingOption(reporter);
    this.extendsOption = new ExtendsOption(reporter);
    this.filenameOption = new FilenameOption(reporter);
    this.implementsOption = new ImplementsOption(reporter);
    this.multipleOption = new MultipleOption(reporter);
    this.subfoldersOption = new SubfoldersOption(reporter);
    this.tagletOption = new TagletOption(reporter);
    this.tagOption = new TagOption(reporter);
  }

  /**
   * Indicates whether these options should use multiple files.
   *
   * @return <code>true</code> if multiple files should be used; <code>false</code> otherwise.
   */
  public boolean useMultipleFiles() {
    return this.multipleOption.enabled();
  }

  /**
   * Indicates whether to organise files as subfolders for packages.
   *
   * @return <code>true</code> if files should be organised in subfolders; <code>false</code> otherwise.
   */
  public boolean useSubFolders() {
    return this.subfoldersOption.enabled();
  }

  /**
   * Returns the charset to use to encode the output.
   *
   * @return the charset to use to encode the output.
   */
  public Charset getEncoding() {
    return this.docencodingOption.getCharset();
  }

  /**
   * Returns the directory where to store the files.
   *
   * @return where to store the files.
   */
  public File getDirectory() {
    return this.directoryOption.getDirectory();
  }

  /**
   * Returns the name of the file for single output.
   *
   * @return the name of the file for single output.
   */
  public String getFilename() {
    return this.filenameOption.getFilename();
  }

  /**
   * Returns the taglet instance for the specified tag name.
   *
   * @param name The name of the tag.
   * @return The corresponding <code>Taglet</code> or <code>null</code>.
   */
  public @Nullable Taglet getTagletForName(String name) {
    return getTaglets().get(name);
  }

  private Map<String, Taglet> getTaglets() {
    if (this.taglets.isEmpty()) {
      // Load the standard taglets
      for (BlockTag t : BlockTag.values()) {
        this.taglets.put(t.getName(), t);
      }
      for (InlineTag t : InlineTag.values()) {
        this.taglets.put("@" + t.getName(), t);
      }
      // Load custom tags
      for (CustomTag t : this.tagOption.getTags()) {
        String name = t.isInlineTag() ? '@' + t.getName() : t.getName();
        this.taglets.put(name, t);
      }
      // Load custom taglets
      for (Taglet t : this.tagletOption.getTaglets()) {
        String name = t.isInlineTag() ? '@' + t.getName() : t.getName();
        this.taglets.put(name, t);
      }
    }
    return this.taglets;
  }

  /**
   * Indicates whether these options specify a filter.
   *
   * @return <code>true</code> if the class must implement or extends or have a specific annotation.
   *         <code>false</code> otherwise.
   */
  public boolean hasFilter() {
    return this.extendsOption.hasFilter()
        || this.implementsOption.hasFilter()
        || this.annotatedOption.hasFilter();
  }

  /**
   * Filters the included set of classes by checking whether the given class matches all of
   * the specified '-extends', '-implements' and '-annotated' options.
   *
   * @param element the class documentation.
   * @return <code>true</code> if the class should be included; <code>false</code> otherwise.
   */
  public boolean filter(TypeElement element) {
    boolean included = true;

    // Extends
    if (this.extendsOption.hasFilter()) {
      included = this.extendsOption.matches(element);
    }
    // Implements
    if (this.implementsOption.hasFilter()) {
      included = included && this.implementsOption.matches(element);
    }
    // Annotation
    if (this.annotatedOption.hasFilter()) {
      included = included && this.annotatedOption.matches(element);
    }

    // No filtering
    return included;
  }

  public Set<? extends Doclet.Option> asSet() {
    Set<Doclet.Option> options = new HashSet<>();
    options.add(this.annotatedOption);
    options.add(this.directoryOption);
    options.add(this.docencodingOption);
    options.add(this.extendsOption);
    options.add(this.filenameOption);
    options.add(this.implementsOption);
    options.add(this.multipleOption);
    options.add(this.subfoldersOption);
    options.add(this.tagletOption);
    options.add(this.tagOption);
    return options;
  }

}
