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

import com.sun.source.doctree.*;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.Taglet;
import org.eclipse.jdt.annotation.Nullable;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Doclet implementation to use with javadoc.
 *
 * <p>A Doclet to be used with JavaDoc which will output XML with all of the information from the JavaDoc.
 *
 * @author Christophe Lauret
 * @version 1.0
 */
@SuppressWarnings("java:S1192") // It's easier to read like this
public final class XMLDoclet implements Doclet {

  /**
   * The date format matching ISO 8601, easier to parse with XSLT.
   */
  private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss";

  private static final Set<Modifier> BOOLEAN_MODIFIERS = EnumSet.complementOf(EnumSet.of(Modifier.PRIVATE, Modifier.PUBLIC, Modifier.PROTECTED));

  /**
   *
   * singleton tags is necessary for processing or transforming documentation elements.
   */
  private static final Set<DocTree.Kind> SINGLETON_TAGS = Collections.unmodifiableSet(EnumSet.of(
      DocTree.Kind.VERSION,
      DocTree.Kind.SINCE,
      DocTree.Kind.SERIAL,
      DocTree.Kind.SERIAL_DATA,
      DocTree.Kind.SERIAL_FIELD,
      DocTree.Kind.DEPRECATED
  ));

  /**
   * The reporter provided by the run method.
   */
  private Reporter reporter;

  /**
   * The method used by this method invocation.
   */
  private Options options;

  /**
   * The doclet environment provided by the run method.
   */
  private DocletEnvironment env;

  /**
   * @return The Doclet environment.
   */
  public DocletEnvironment getEnvironment() {
    return this.env;
  }

  /**
   * @return The options used by this instance.
   */
  public Options getOptions() {
    return this.options;
  }

  /**
   * @return The reporter used by this instance.
   */
  public Reporter getReporter() {
    return this.reporter;
  }

  @Override
  public void init(Locale locale, Reporter reporter) {
    reporter.print(Diagnostic.Kind.NOTE, "Doclet using locale: " + locale);
    this.reporter = reporter;
    this.options = new Options(reporter);
  }

  /**
   * Processes the JavaDoc documentation.
   *
   * @param env the DocletEnvironment
   *
   * @return <code>true</code> if processing was successful.
   */
  @Override
  public boolean run(DocletEnvironment env) {
    this.env = env;
    List<XMLNode> nodes = new ArrayList<>();

    // Iterate over elements
    for (TypeElement element : ElementFilter.typesIn(this.env.getIncludedElements())) {
      // Apply the filters from options
      if (this.options.filter(element)) {
        try {
          nodes.add(toClassNode(element));
        } catch (Exception ex) {
          reporter.print(Diagnostic.Kind.ERROR, element, ex.getMessage());
          ex.printStackTrace();
        }
      }
    }

    // Iterate over packages
    if (!options.hasFilter()) {
      for (PackageElement element : ElementFilter.packagesIn(this.env.getIncludedElements())) {
        try {
          nodes.add(toPackageNode(element));
        } catch (Exception ex) {
          reporter.print(Diagnostic.Kind.ERROR, element, ex.getMessage());
        }
      }
    }

    // Save the output XML
    try {
      save(nodes);
    } catch (DocletException ex) {
      reporter.print(Diagnostic.Kind.ERROR, ex.getElement(), ex.getMessage());
      return false;
    }

    return true;
  }

  @Override
  public String getName() {
    return "XMLDoclet";
  }

  @Override
  public Set<? extends Option> getSupportedOptions() {
    return this.options.asSet();
  }

  /**
   * Returns the version of the Java Programming Language supported by this Doclet.
   */
  @Override
  public SourceVersion getSupportedSourceVersion() {
    // support the latest release
    return SourceVersion.latest();
  }

  /**
   * Save the given array of nodes.
   *
   * <p>Will either save the files individually, or as a single file depending on whether the
   * "-multiple" flag is used or not.
   *
   * @param nodes The array of nodes to be saved.
   *
   * @throws DocletException If an error occurs while saving the files.
   */
  private void save(List<XMLNode> nodes) throws DocletException {
    // Add admin node
    XMLNode meta = new XMLNode("meta");
    DateFormat df = new SimpleDateFormat(ISO_8601);
    meta.attribute("created", df.format(new Date()));

    // Multiple files
    if (this.options.useMultipleFiles()) {
      for (XMLNode node : nodes) {
        File dir = this.options.getDirectory();
        String name = node.getAttribute("name");
        if (this.options.useSubFolders()) {
          name = name.replace('.', '/');
          int x = name.lastIndexOf('/');
          if (x >= 0) {
            dir = new File(dir, name.substring(0, x));
            if (!dir.exists()) {
              boolean created = dir.mkdirs();
              if (!created) this.reporter.print(Diagnostic.Kind.WARNING, "Unable to create directory "+dir.getAbsolutePath());
            }
            name = name.substring(x + 1);
          }
        }
        XMLNode root = new XMLNode("root");
        root.attribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
        root.child(meta);
        root.child(node);
        String fileName = name + ".xml";
        root.save(dir, fileName, this.options.getEncoding(), "");
      }
      // Index
      XMLNode root = new XMLNode("root");
      root.attribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
      root.child(meta);
      for (XMLNode node : nodes) {
        String name = node.getAttribute("name");
        if (this.options.useSubFolders()) {
          name = name.replace('.', '/');
        }
        XMLNode ref = new XMLNode(node.getName());
        ref.attribute("xlink:type", "simple");
        ref.attribute("xlink:href", name + ".xml");
        root.child(ref);
      }
      String fileName = "index.xml";
      root.save(this.options.getDirectory(), fileName, this.options.getEncoding(), "");

      // Single file
    } else {
      // Wrap the XML
      XMLNode root = new XMLNode("root");
      root.attribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
      root.child(meta);
      for (XMLNode node : nodes) {
        root.child(node);
      }
      root.save(this.options.getDirectory(), this.options.getFilename(), this.options.getEncoding(), "");
    }

  }

  /**
   * Returns the XML node corresponding to the specified ClassDoc.
   *
   * @param packageElement The package packageElement to process.
   */
  private XMLNode toPackageNode(PackageElement packageElement) {
    XMLNode node = new XMLNode("package", packageElement);

    // Core attributes
    node.attribute("name", packageElement.getQualifiedName().toString());
    node.attribute("unnamed", packageElement.isUnnamed());

    // Comment
    node.child(toComment(packageElement));

    // Child nodes
    node.child(toAnnotationsNode(packageElement.getAnnotationMirrors()));
    node.child(toStandardTags(packageElement));
    node.child(toTags(packageElement));
    node.child(toSeeNodes(packageElement));

    return node;
  }

  /**
   * Returns the XML node corresponding to the specified ClassDoc.
   *
   * @param typeElement The class to transform.
   */
  @SuppressWarnings("java:3776")
  private XMLNode toClassNode(TypeElement typeElement) {
    XMLNode node = new XMLNode("class", typeElement);

    Elements elements = this.env.getElementUtils();

    // Core attributes
    node.attribute("type", typeElement.getSimpleName().toString());
    node.attribute("fulltype", typeElement.getQualifiedName().toString());
    node.attribute("name", typeElement.getQualifiedName().toString());
    node.attribute("package", elements.getPackageOf(typeElement).toString());
    node.attribute("visibility", getVisibility(typeElement));
    node.attribute("kind", typeElement.getKind().toString().toLowerCase());
    if (typeElement.getNestingKind().isNested()) {
      node.attribute("nesting-kind", typeElement.getNestingKind().toString().toLowerCase());
    }

    // Class properties
    for (Modifier modifier : toBooleanModifiers(typeElement)) {
      node.attribute(modifier.name().toLowerCase(), "true");
    }
    if (isSerializable(typeElement)) {
      node.attribute("serializable", "true");
    }

    // Interfaces
    List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
    if (!interfaces.isEmpty()) {
      XMLNode implement = new XMLNode("implements");
      for (TypeMirror type : interfaces) {
        XMLNode interfce = new XMLNode("interface");
        interfce.attribute("type", toSimpleType(type));
        interfce.attribute("fulltype", type.toString());
        implement.child(interfce);
      }
      node.child(implement);
    }

    // Superclass
    if (typeElement.getSuperclass() != null) {
      TypeMirror superclass = typeElement.getSuperclass();
      if (typeElement.getKind() == ElementKind.CLASS) {
        if (!"java.lang.Object".equals(superclass.toString())) {
          node.attribute("superclass", superclass.toString());
        }
      } else if (typeElement.getKind() == ElementKind.ENUM) {
        String defaultEnumSuperclass = "java.lang.Enum<" + typeElement.getQualifiedName() + ">";
        if (!defaultEnumSuperclass.equals(superclass.toString())) {
          node.attribute("superclass", superclass.toString());
        }
      }
    }

    // Comment
    node.child(toComment(typeElement));

    // Other child nodes
    node.child(toAnnotationsNode(typeElement.getAnnotationMirrors()));
    node.child(toStandardTags(typeElement));
    node.child(toTags(typeElement));
    node.child(toSeeNodes(typeElement));
    node.child(toFieldsNode(typeElement));
    node.child(toConstructorsNode(typeElement));
    node.child(toMethods(typeElement));

    return node;
  }

  /**
   * Returns the specified field as an XML node.
   *
   * @param field A field.
   *
   * @return The corresponding node.
   */
  private XMLNode toFieldNode(VariableElement field) {
    // Create the <field> node and populate it.
    XMLNode node = new XMLNode("field");
    node.attribute("name", field.getSimpleName().toString());
    node.attribute("type", toSimpleType(field.asType()));
    node.attribute("fulltype", field.asType().toString());

    if (field.getConstantValue() != null && !field.getConstantValue().toString().isEmpty()) {
      node.attribute("const", field.getConstantValue().toString());
    }

    for (Modifier modifier : toBooleanModifiers(field)) {
      node.attribute(modifier.name().toLowerCase(), "true");
    }
    node.attribute("visibility", getVisibility(field));

    // Comment
    node.child(toComment(field));

    // Other child nodes
    node.child(toStandardTags(field));
    node.child(toTags(field));
    node.child(toSeeNodes(field));

    return node;
  }

  private @Nullable XMLNode toConstructorsNode(TypeElement element) {
    List<ExecutableElement> constructors = ElementFilter.constructorsIn(element.getEnclosedElements());
    if (constructors.isEmpty()) return null;

    // Create the <constructors> node
    XMLNode node = new XMLNode("constructors");

    // Add the <constructor> nodes
    for (ExecutableElement constructor : constructors) {
      XMLNode c = new XMLNode("constructor");
      processExecutableElement(constructor, c);
      node.child(c);
    }

    return node;
  }

  /**
   * Transforms an array of methods and an array of constructor methods into XML and adds those to the host node.
   */
  private @Nullable XMLNode toMethods(TypeElement element) {
    List<ExecutableElement> methods = ElementFilter.methodsIn(element.getEnclosedElements());
    if (methods.isEmpty()) return null;

    // Create the <methods> node
    XMLNode node = new XMLNode("methods");

    // Add the <method> nodes
    for (ExecutableElement method : methods) {
      XMLNode methodNode = new XMLNode("method");

      processExecutableElement(method, methodNode);

      TypeMirror returnType = method.getReturnType();
      methodNode.attribute("type", toSimpleType(returnType));
      methodNode.attribute("fulltype", returnType.toString());

      for (Modifier modifier : toBooleanModifiers(method)) {
        methodNode.attribute(modifier.name().toLowerCase(), "true");
      }

      // Return tag
      ReturnTree returnTree = findReturnTree(method);
      if (returnTree != null) {
        XMLNode comment = new XMLNode("return", element, -1); // TODO doc.position().line()
        String markup = Markup.toString(returnTree.getDescription(), element, this.options, this.reporter, false);
        comment.markup(markup);
        methodNode.child(comment);
      }

      node.child(methodNode);
    }

    return node;
  }

  /**
   * Returns the fields node.
   *
   * @param element The class
   *
   * @return the fields or <code>null</code> if none.
   */
  private @Nullable XMLNode toFieldsNode(TypeElement element) {
    List<VariableElement> fields = ElementFilter.fieldsIn(element.getEnclosedElements());
    if (fields.isEmpty()) return null;
    // Iterate over the fields
    XMLNode node = new XMLNode("fields");
    for (VariableElement field : fields) {
      node.child(toFieldNode(field));
    }
    return node;
  }

  /**
   * Set the commons attribute and child nodes for method and constructor nodes.
   *
   * @param member The executable member documentation.
   * @param node   The node to update
   */
  private void processExecutableElement(ExecutableElement member, XMLNode node) {
    // Add the basic attribute values
    node.attribute("name", member.getSimpleName().toString());
    node.attribute("visibility", getVisibility(member));
    for (Modifier modifier : toBooleanModifiers(member)) {
      node.attribute(modifier.name().toLowerCase(), "true");
    }

    // Comment
    node.child(toComment(member));

    // Other objects attached to the method/constructor.
    node.child(toTags(member));
    node.child(toSeeNodes(member));
    node.child(toParametersNode(member));
    node.child(toExceptionsNode(member));
  }

  /**
   * Transforms common tags on the Doc object into XML.
   *
   * @param element The element to document.
   *
   * @return The corresponding list of nodes.
   */
  private List<XMLNode> toStandardTags(Element element) {
    // Create the comment node
    List<XMLNode> nodes = new ArrayList<>();
    DocCommentTree commentTree = this.env.getDocTrees().getDocCommentTree(element);

    // Handle the tags
    if (commentTree != null) {
      checkSingletonTags(element, commentTree);
      for (DocTree tag : commentTree.getBlockTags()) {
        BlockTagTree block = (BlockTagTree) tag;
        Taglet taglet = this.options.getTagletForName(block.getTagName());
        if (taglet instanceof BlockTag) {
          nodes.add(((BlockTag) taglet).toXMLNode(tag));
        }
      }
    }

    // Add the node to the host
    return nodes;
  }

  void checkSingletonTags(Element element, DocCommentTree commentTree) {
    EnumSet<DocTree.Kind> found = EnumSet.noneOf(DocTree.Kind.class);
    for (DocTree tag : commentTree.getBlockTags()) {
      if (SINGLETON_TAGS.contains(tag.getKind()) && found.contains(tag.getKind())) {
        this.reporter.print(Diagnostic.Kind.WARNING, element, "Duplicate tag "+tag.toString()+" found");
      }
      found.add(tag.getKind());
    }
  }

  /**
   * Transforms comments on the Doc object into XML.
   */
  private @Nullable XMLNode toTags(Element element) {
    DocCommentTree comment = this.env.getDocTrees().getDocCommentTree(element);
    if (comment == null) return null;
    List<? extends DocTree> blockTags = comment.getBlockTags();

    // Create the comment node
    XMLNode node = new XMLNode("tags");

    boolean hasTags = false;

    // Handle the tags
    for (DocTree tag : blockTags) {
      if (tag.getKind() == DocTree.Kind.UNKNOWN_BLOCK_TAG) {
        UnknownBlockTagTree block = (UnknownBlockTagTree)tag;
        Taglet taglet = options.getTagletForName(block.getTagName());
        if (taglet != null) {
          XMLNode tNode = new XMLNode("tag");
          tNode.attribute("name", block.getTagName());
          String contents = taglet.toString(List.of(block), element);
          tNode.markup(contents);
          node.child(tNode);
          hasTags = true;
        }
      }
    }

    // Add the node to the host
    return hasTags ? node : null;
  }

  // Aggregate XML methods ========================================================================

  /**
   * Returns the XML for the specified parameters using the param tags for additional description.
   *
   * @return the XML for the specified parameters using the param tags for additional description.
   */
  private @Nullable XMLNode toParametersNode(ExecutableElement member) {
    List<? extends VariableElement> parameters = member.getParameters();
    if (parameters.isEmpty()) return null;

    // Iterate over the parameters
    XMLNode node = new XMLNode("parameters");
    for (VariableElement parameter : parameters) {
      ParamTree comment = findParamTree(member, parameter.getSimpleName().toString());
      XMLNode p = toParameterNode(member, parameter, comment);
      node.child(p);
    }

    return node;
  }

  /**
   * Returns the XML for the specified exceptions using the throws tags for additional description.
   *
   * @return the XML for the specified parameters using the param tags for additional description.
   */
  private @Nullable XMLNode toExceptionsNode(ExecutableElement member) {
    List<? extends TypeMirror> thrownTypes = member.getThrownTypes();
    if (thrownTypes.isEmpty()) return null;

    // Iterate over the exceptions
    XMLNode node = new XMLNode("exceptions");
    for (TypeMirror exception : thrownTypes) {
      ThrowsTree throwsTree = findThrowsTree(member, exception.toString());
      XMLNode n = toExceptionNode(member, exception, throwsTree);
      node.child(n);
    }

    return node;
  }

  /**
   * Transforms comments on the Doc object into XML.
   */
  private List<XMLNode> toSeeNodes(Element element) {
    DocCommentTree tree = this.env.getDocTrees().getDocCommentTree(element);
    if (tree == null) return List.of();
    List<? extends DocTree> blockTags = tree.getBlockTags();
    if (blockTags.isEmpty()) return List.of();
    List<XMLNode> nodes = new ArrayList<>();
    for (DocTree tag : blockTags) {
      if (tag.getKind() == DocTree.Kind.SEE) {
        XMLNode n = toSeeNode((SeeTree) tag);
        if (n != null) {
          nodes.add(n);
        }
      }
    }

    // Add the node to the host
    return nodes;
  }

  private @Nullable XMLNode toAnnotationsNode(List<? extends AnnotationMirror> annotations) {
    if (annotations.isEmpty()) return null;
    XMLNode node = new XMLNode("annotations");
    for (AnnotationMirror annotation : annotations) {
      node.child(toAnnotationNode(annotation));
    }
    return node;
  }

  // Atomic XML methods ===========================================================================

  /**
   * Returns the XML for a see tag.
   *
   * @param doc The See tag to process.
   */
  private @Nullable XMLNode toSeeNode(@Nullable SeeTree doc) {
    if (doc == null) return null;
    XMLNode see = new XMLNode("see");
    see.attribute("xlink:type", "simple");

    boolean multiple = this.options.useMultipleFiles();
    // TODO

    // A link
//    if (tag.text().startsWith("<a")) {
//      String text = tag.text();
//      Matcher href = Pattern.compile("href=\"(.+)\"").matcher(text);
//      if (href.find()) {
//        see.attribute("xlink:href", href.group(1));
//      }
//      Matcher title = Pattern.compile("\\>(.+)\\<\\/").matcher(text);
//      if (title.find()) {
//        see.attribute("xlink:title", title.group(1));
//      }

    // A referenced Package
//    } else if (tag.referencedPackage() != null) {
//      String pkg = tag.referencedPackage().name();
//      see.attribute("xlink:href", multiple? pkg+".xml" : "xpath1(//package[name='"+pkg+"'])");

    // A referenced Class
//    } else if (tag.referencedClass() != null) {
//      String cls = tag.referencedClass().qualifiedName();
//      see.attribute("xlink:href", multiple? cls+".xml" : "xpath1(//class[name='"+cls+"'])");

    // Something else
//    } else {
//      see.attribute("xlink:href", tag.text());
//    }

    return see;
  }

  /**
   * Returns the XML for a parameter and its corresponding param tag.
   *
   * @return The corresponding XML.
   */
  private @Nullable XMLNode toParameterNode(ExecutableElement member, @Nullable VariableElement parameter, @Nullable ParamTree comment) {
    if (parameter == null) return null;
    XMLNode node = new XMLNode("parameter");
    node.attribute("name", parameter.getSimpleName().toString());
    node.attribute("type", toSimpleType(parameter.asType()));
    node.attribute("fulltype", parameter.asType().toString());
    if (comment != null) {
      String markup = Markup.toString(comment.getDescription(), member, this.options, this.reporter, false);
      node.markup(markup);
    }
    return node;
  }

  /**
   * Returns the XML for an exception and its corresponding throws tag.
   *
   * @return The corresponding XML.
   */
  private @Nullable XMLNode toExceptionNode(ExecutableElement member, @Nullable TypeMirror exception, @Nullable ThrowsTree throwsTree) {
    if (exception == null) return null;
    XMLNode node = new XMLNode("exception");
    node.attribute("type", toSimpleType(exception));
    node.attribute("fulltype", exception.toString());
    if (throwsTree != null) {
      node.attribute("comment", throwsTree.getDescription().toString());
      String markup = Markup.toString(throwsTree.getDescription(), member, this.options, this.reporter, false);
      node.markup(markup);
    }
    return node;
  }

  /**
   * Transforms comments on the Doc object into XML.
   *
   * @param element The element
   */
  private @Nullable XMLNode toComment(Element element) {
    DocCommentTree commentTree = this.env.getDocTrees().getDocCommentTree(element);
    if (commentTree == null || commentTree.toString().isEmpty()) return null;
    XMLNode node = new XMLNode("comment", element, -1); // TODO doc.position().line()
    String markup = Markup.toString(commentTree.getFullBody(), element, this.options, this.reporter, true);
    return node.markup(markup);
  }

  /**
   * @return an "annotation" XML node for the annotation.
   */
  private @Nullable XMLNode toAnnotationNode(@Nullable AnnotationMirror annotation) {
    if (annotation == null) return null;
    XMLNode node = new XMLNode("annotation");
    node.attribute("name", annotation.getAnnotationType().asElement().getSimpleName().toString());
    Map<? extends ExecutableElement, ? extends AnnotationValue> values = annotation.getElementValues();
    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> pair : values.entrySet()) {
      node.child(toPairNode(pair.getKey(), pair.getValue()));
    }
    return node;
  }

  /**
   *
   * @return an "element" XML node for the element value pair.
   */
  private XMLNode toPairNode(ExecutableElement element, AnnotationValue value) {
    XMLNode node = new XMLNode("element");
    node.attribute("name", element.getSimpleName().toString());
// TODO   node.child(toComment(element));
    node.child(toAnnotationValueNode(value));
    return node;
  }

  /**
   *
   * @return an "value" or "array" XML node for the annotation value.
   */
  private @Nullable XMLNode toAnnotationValueNode(@Nullable AnnotationValue value) {
    if (value == null) return null;
    XMLNode node = null;
    Object o = value.getValue();
    Class<?> c = o.getClass();
    if (c.isArray()) { // JDK8
      node = new XMLNode("array");
      Object[] a = (Object[]) o;
      for (Object i : a) {
        if (i instanceof AnnotationValue) {
          node.child(toAnnotationValueNode((AnnotationValue) i));
        } else {
          this.reporter.print(Diagnostic.Kind.WARNING, "Unexpected annotation value type" + i);
        }
      }
    } else if (o instanceof List) { // JDK11
      node = new XMLNode("array");
      List<?> list = (List<?>)o;
      for (Object i : list) {
        if (i instanceof AnnotationValue) {
          node.child(toAnnotationValueNode((AnnotationValue)i));
        } else {
          this.reporter.print(Diagnostic.Kind.WARNING, "Unexpected annotation value type" + i);
        }
      }
    } else {
      node = new XMLNode("value");
      node.attribute("type", getAnnotationValueType(o));
      node.attribute("value", o.toString());
    }

    return node;
  }

  // Utilities ====================================================================================

  /**
   * Sets the visibility for the class, method, or field.
   *
   * @param element The member for which the visibility needs to be set (class, method, or field).
   */
  private static String getVisibility(Element element) {
    Set<Modifier> modifiers = element.getModifiers();
    if (modifiers.contains(Modifier.PRIVATE)) return "private";
    if (modifiers.contains(Modifier.PROTECTED)) return "protected";
    if (modifiers.contains(Modifier.PUBLIC)) return "public";
    return "package-private";
  }

  private static boolean isSerializable(TypeElement element) {
    List<? extends TypeMirror> interfaces = element.getInterfaces();
    for (TypeMirror i : interfaces) {
      if ("java.io.Serializable".equals(i.toString())) return true;
    }
    return false;
  }

  /**
   * Find the corresponding throws tag
   */
  private @Nullable ThrowsTree findThrowsTree(ExecutableElement member, String name) {
    DocCommentTree comment = this.env.getDocTrees().getDocCommentTree(member);
    if (comment == null) return null;
    for (DocTree tree : comment.getBlockTags()) {
      if (tree.getKind() == DocTree.Kind.THROWS) {
        ThrowsTree throwsTree = (ThrowsTree) tree;
        if (throwsTree.getExceptionName().toString().equals(name)) return throwsTree;
      }
    }
    return null;
  }

  /**
   * Find the corresponding parameter tag.
   */
  private @Nullable ParamTree findParamTree(ExecutableElement member, String name) {
    DocCommentTree comment = this.env.getDocTrees().getDocCommentTree(member);
    if (comment == null) return null;
    for (DocTree tree : comment.getBlockTags()) {
      if (tree.getKind() == DocTree.Kind.PARAM) {
        ParamTree paramTree = (ParamTree) tree;
        if (paramTree.getName().toString().equals(name)) return paramTree;
      }
    }
    return null;
  }

  /**
   * Find the corresponding return tag.
   */
  private @Nullable ReturnTree findReturnTree(ExecutableElement member) {
    DocCommentTree comment = this.env.getDocTrees().getDocCommentTree(member);
    if (comment == null) return null;
    for (DocTree tree : comment.getBlockTags()) {
      if (tree.getKind() == DocTree.Kind.RETURN) {
        return (ReturnTree) tree;
      }
    }
    return null;
  }

  /**
   * Returns the value type of the annotation depending on the specified object's class.
   *
   * @param o the object representing the type of annotation value.
   * @return the primitive if any of full class name.
   */
  private static String getAnnotationValueType(Object o) {
    if (o instanceof String)  return "String";
    if (o instanceof Integer) return "int";
    if (o instanceof Boolean) return "boolean";
    if (o instanceof Long)    return "long";
    if (o instanceof Short)   return "short";
    if (o instanceof Float)   return "float";
    if (o instanceof Double)  return "double";
    if (o instanceof Element) return ((Element)o).getEnclosingElement().toString();
    return o.getClass().getName();
  }

  private String toSimpleType(TypeMirror type) {
    if (type instanceof DeclaredType) {
      return ((DeclaredType) type).asElement().getSimpleName().toString();
    }
    return type.toString();
  }

  private Set<Modifier> toBooleanModifiers(Element element) {
    return element.getModifiers().stream()
        .filter(BOOLEAN_MODIFIERS::contains)
        .collect(Collectors.toUnmodifiableSet());
  }
}
