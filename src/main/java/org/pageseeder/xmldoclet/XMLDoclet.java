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

/**
 * The Doclet implementation to use with javadoc.
 *
 * <p>A Doclet to be used with JavaDoc which will output XML with all of the information from the JavaDoc.
 *
 * @author Christophe Lauret
 * @version 1.0
 */
public final class XMLDoclet implements Doclet {

  /**
   * The date format matching ISO 8601, easier to parse with XSLT.
   */
  private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss";

  private Reporter reporter;

  /**
   * The method used by this method invocation.
   */
  private Options options;

  /**
   * The doclet environment provided by the run method.
   */
  private DocletEnvironment env;

  @Override
  public void init(Locale locale, Reporter reporter) {
    reporter.print(Diagnostic.Kind.NOTE, "Doclet using locale: " + locale);
    this.reporter = reporter;
    options = new Options(reporter);
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
        nodes.add(toClassNode(element));
      }
    }

    // Iterate over packages
    if (!options.hasFilter()) {
      for (PackageElement element : ElementFilter.packagesIn(this.env.getIncludedElements())) {
        nodes.add(toPackageNode(element));
      }
    }

    // Save the output XML
    save(nodes);

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
   */
  private void save(List<XMLNode> nodes) {
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
            dir.mkdirs();
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
    // TODO flag nested classes
//    node.attribute("nesting-kind", typeElement.getNestingKind().toString().toLowerCase());

    // Class properties
    Set<Modifier> modifiers = typeElement.getModifiers();
    node.attribute("final", modifiers.contains(Modifier.FINAL));
    node.attribute("abstract", modifiers.contains(Modifier.ABSTRACT));
    node.attribute("serializable", isSerializable(typeElement));
    // TODO Deprecate (use `kind` instead)
    node.attribute("interface", typeElement.getKind() == ElementKind.INTERFACE);
    node.attribute("enum", typeElement.getKind() == ElementKind.ENUM);

    // Interfaces
    List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
    if (interfaces.size() > 0) {
      XMLNode implement = new XMLNode("implements");
      for (TypeMirror type : interfaces) {
        XMLNode interfce = new XMLNode("interface");
        interfce.attribute("type", toSimpleType(type)); // i.name()
        interfce.attribute("fulltype", type.toString()); // i.qualifiedName()
        implement.child(interfce);
      }
      node.child(implement);
    }

    // Superclass
    if (typeElement.getSuperclass() != null) {
      TypeMirror superclass = typeElement.getSuperclass();
      if (typeElement.getKind() == ElementKind.CLASS) {
        if (!"java.lang.Object".equals(superclass.toString())) {
          node.attribute("superclass", superclass.toString()); // i.name()
//          node.attribute("superclassfulltype", superclass.toString()); // i.qualifiedName()
        }
      } else if (typeElement.getKind() == ElementKind.ENUM) {
        String defaultEnumSuperclass = "java.lang.Enum<" + typeElement.getQualifiedName() + ">";
        if (!defaultEnumSuperclass.equals(superclass.toString())) {
          node.attribute("superclass", superclass.toString()); // i.name()
//        node.attribute("superclassfulltype", superclass.toString()); // i.qualifiedName()
        }
      }
    }

    // Comment
    node.child(toComment(typeElement));

    // Other child nodes
    node.child(toAnnotationsNode(typeElement.getAnnotationMirrors()));
    node.child(toStandardTags(typeElement));
//    node.child(toTags(typeElement));
    node.child(toSeeNodes(typeElement));
    node.child(toFieldsNode(typeElement));
    node.child(toConstructorsNode(typeElement));
    node.child(toMethods(typeElement));

    // Handle inner classes
//    for (ClassDoc inner : typeElement.innerClasses()) {
//      node.child(toClassNode(inner));
//    }

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

    // TODO
//    if (field.constantValue() != null && field.constantValue().toString().length() > 0) {
//      node.attribute("const", field.constantValue().toString());
//    }
//    if (field.constantValueExpression() != null && field.constantValueExpression().length() > 0) {
//      node.attribute("constexpr", field.constantValueExpression());
//    }

    Set<Modifier> modifiers = field.getModifiers();
    node.attribute("static", modifiers.contains(Modifier.STATIC));
    node.attribute("final", modifiers.contains(Modifier.FINAL));
    node.attribute("transient", modifiers.contains(Modifier.TRANSIENT));
    node.attribute("volatile", modifiers.contains(Modifier.VOLATILE));
    node.attribute("visibility", getVisibility(field));

    // Comment
// TODO    node.child(toComment(field));

    // Other child nodes
    node.child(toStandardTags(field));
// TODO   node.child(toTags(field));
    node.child(toSeeNodes(field));

    return node;
  }

  private XMLNode toConstructorsNode(TypeElement element) {
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
  private XMLNode toMethods(TypeElement element) {
    List<ExecutableElement> methods = ElementFilter.methodsIn(element.getEnclosedElements());
    if (methods.isEmpty()) return null;

    // Create the <methods> node
    XMLNode node = new XMLNode("methods");

    // Add the <method> nodes
    for (ExecutableElement method : methods) {
      XMLNode methodNode = new XMLNode("method");

      processExecutableElement(method, methodNode);

      Set<Modifier> modifiers = method.getModifiers();
      TypeMirror returnType = method.getReturnType();

      methodNode.attribute("type", toSimpleType(returnType)); // TODO typeName()
      methodNode.attribute("fulltype", returnType.toString());
      methodNode.attribute("abstract", modifiers.contains(Modifier.ABSTRACT));

//      Tag[] returnTags = method.tags("@return");
//      if (returnTags.length > 0) {
//        node.text(toComment(returnTags[0]));
//      }

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
  private XMLNode toFieldsNode(TypeElement element) {
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
    Set<Modifier> modifiers = member.getModifiers();

    // Add the basic attribute values
    node.attribute("name", member.getSimpleName().toString());
    node.attribute("static", modifiers.contains(Modifier.STATIC));
// TODO    node.attribute("interface",    member.isInterface());
    node.attribute("final", modifiers.contains(Modifier.FINAL));
    node.attribute("visibility", getVisibility(member));
    node.attribute("synchronized", modifiers.contains(Modifier.SYNCHRONIZED));
// TODO    node.attribute("synthetic",    member.isSynthetic());

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

  /**
   * Transforms comments on the Doc object into XML.
   */
  private XMLNode toTags(Element element) {
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
          String markup = Markup.asString(block.getContent(), this.options, false);
          tNode.text(markup);
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
  private XMLNode toParametersNode(ExecutableElement member) {
    List<? extends VariableElement> parameters = member.getParameters();
    if (parameters.isEmpty()) return null;

    // Iterate over the parameters
    XMLNode node = new XMLNode("parameters");
    for (VariableElement parameter : parameters) {
      ParamTree comment = findParamTree(member, parameter.getSimpleName().toString());
      XMLNode p = toParameterNode(parameter, comment);
      node.child(p);
    }

    return node;
  }

  /**
   * Returns the XML for the specified exceptions using the throws tags for additional description.
   *
   * @return the XML for the specified parameters using the param tags for additional description.
   */
  private XMLNode toExceptionsNode(ExecutableElement member) {
    List<? extends TypeMirror> thrownTypes = member.getThrownTypes();
    if (thrownTypes.isEmpty()) return null;

    // Iterate over the exceptions
    XMLNode node = new XMLNode("exceptions");
    for (TypeMirror exception : thrownTypes) {
      ThrowsTree throwsTree = findThrowsTree(member, exception.toString());
      XMLNode n = toExceptionNode(exception, throwsTree);
      node.child(n);
    }

    return node;
  }

  /**
   * Transforms comments on the Doc object into XML.
   */
  private List<XMLNode> toSeeNodes(Element element) {
    DocCommentTree tree = this.env.getDocTrees().getDocCommentTree(element);
    if (tree == null) return Collections.emptyList();
    List<? extends DocTree> blockTags = tree.getBlockTags();
    if (blockTags.isEmpty()) return Collections.emptyList();
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

  private static XMLNode toAnnotationsNode(List<? extends AnnotationMirror> annotations) {
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
  private XMLNode toSeeNode(SeeTree doc) {
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
  private XMLNode toParameterNode(VariableElement parameter, ParamTree comment) {
    if (parameter == null) return null;
    XMLNode node = new XMLNode("parameter");
    node.attribute("name", parameter.getSimpleName().toString());
    node.attribute("type", toSimpleType(parameter.asType()));
    node.attribute("fulltype", parameter.asType().toString());
    if (comment != null) {
      String markup = Markup.asString(comment.getDescription(), this.options, false);
      node.text(markup);
    }
    return node;
  }

  /**
   * Returns the XML for an exception and its corresponding throws tag.
   *
   * @return The corresponding XML.
   */
  private XMLNode toExceptionNode(TypeMirror exception, ThrowsTree throwsTree) {
    if (exception == null) return null;
    XMLNode node = new XMLNode("exception");
    node.attribute("type", toSimpleType(exception));
    node.attribute("fulltype", exception.toString());
    if (throwsTree != null) {
      node.attribute("comment", throwsTree.getDescription().toString());
      String markup = Markup.asString(throwsTree.getDescription(), this.options, false);
      node.text(markup);
    }
    return node;
  }

  /**
   * Transforms comments on the Doc object into XML.
   *
   * @param element The element
   */
  private XMLNode toComment(Element element) {
    DocCommentTree commentTree = this.env.getDocTrees().getDocCommentTree(element);
    if (commentTree == null || commentTree.toString().isEmpty()) return null;
    XMLNode node = new XMLNode("comment", element, -1); // TODO doc.position().line()
    String markup = Markup.asString(commentTree.getFullBody(), this.options, true);
    return node.text(markup);
  }

  /**
   * @return an "annotation" XML node for the annotation.
   */
  private static XMLNode toAnnotationNode(AnnotationMirror annotation) {
    if (annotation == null) return null;
    XMLNode node = new XMLNode("annotation");
    node.attribute("name", annotation.getAnnotationType().asElement().getSimpleName().toString());
    // TODO
//    for (ElementValuePair pair : annotation.elementValues()) {
//      node.child(toPairNode(pair));
//    }
    return node;
  }

//  /**
//   *
//   * @return an "element" XML node for the element value pair.
//   */
//  private static XMLNode toPairNode(ElementValuePair pair) {
//    if (pair == null) return null;
//    XMLNode node = new XMLNode("element");
//    AnnotationTypeElementDoc element = pair.element();
//    node.attribute("name", element.name());
//    node.child(toComment(element));
//    node.child(toAnnotationValueNode(pair.value()));
//    return node;
//  }
//
//  /**
//   *
//   * @return an "value" or "array" XML node for the annotation value.
//   */
//  private static XMLNode toAnnotationValueNode(AnnotationValue value) {
//    if (value == null) return null;
//    XMLNode node = null;
//    Object o = value.value();
//    Class<?> c = o.getClass();
//    if (c.isArray()) {
//      node = new XMLNode("array");
//      Object[]a = (Object[])o;
//      for (Object i : a) {
//        if (i instanceof AnnotationValue) {
//          node.child(toAnnotationValueNode((AnnotationValue)i));
//        } else {
//          System.err.println("Unexpected annotation value type"+i);
//        }
//      }
//    } else {
//      node = new XMLNode("value");
//      node.attribute("type", getAnnotationValueType(o));
//      node.attribute("value", o.toString());
//    }
//
//    return node;
//  }

  // Utilities ====================================================================================

  /**
   * Sets the visibility for the class, method or field.
   *
   * @param element The member for which the visibility needs to be set (class, method, or field).
   */
  private static String getVisibility(Element element) {
    Set<Modifier> modifiers = element.getModifiers();
    if (modifiers.contains(Modifier.PRIVATE)) return "private";
    if (modifiers.contains(Modifier.PROTECTED)) return "protected";
    if (modifiers.contains(Modifier.PUBLIC)) return "public";
    // TODO this might not be the correct default...
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
  private ThrowsTree findThrowsTree(ExecutableElement member, String name) {
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
  private ParamTree findParamTree(ExecutableElement member, String name) {
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

//  /**
//   * Returns the value type of the annotation depending on the specified object's class.
//   *
//   * @param o the object representing the type of annotation value.
//   * @return the primitive if any of full class name.
//   */
//  private static String getAnnotationValueType(Object o) {
//    if (o instanceof String)  return "String";
//    if (o instanceof Integer) return "int";
//    if (o instanceof Boolean) return "boolean";
//    if (o instanceof Long)    return "long";
//    if (o instanceof Short)   return "short";
//    if (o instanceof Float)   return "float";
//    if (o instanceof Double)  return "double";
//    if (o instanceof FieldDoc) return ((FieldDoc)o).containingClass().qualifiedName();
//    return o.getClass().getName();
//  }

  private String toSimpleType(TypeMirror type) {
    if (type instanceof DeclaredType) {
      return ((DeclaredType) type).asElement().getSimpleName().toString();
    }
    return type.toString();
  }

}
