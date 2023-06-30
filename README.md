[ ![Download](https://api.bintray.com/packages/pageseeder/maven/xmldoclet/images/download.svg) ](https://bintray.com/pageseeder/maven/xmldoclet/_latestVersion)

# XML Doclet

This library defines a Doclet for use with the Javadoc tool in order to
export Java documentation as XML. This version requires at least JDK11.

## Testing

```shell
javadoc -doclet org.pageseeder.xmldoclet.XMLDoclet \
        -docletpath build/classes/java/main:build/classes/java/test \
        -taglet org.pageseeder.xmldoclet.tags.ErrorIdTaglet \
        -taglet org.pageseeder.xmldoclet.tags.GlossaryTaglet \
        -sourcepath src/test/java \
        -multiple \
        -d build/testdoc \
        org.example
```

Or simply

```shell
javadoc @example/options
```

# Changes from 0.9

## Added

- The `kind` attribute for type elements based on `TypeElement#getKind()`
- The `nesting-kind` attribute for nested classe based on `TypeElement#getNestingKind()`
- The `default` attribute for methods (only specified when `true`)

## Updated

- The `superclass` attribute now contains the fully qualified classname.
- The `superclassfulltype` attribute is no longer included
- Boolean attributes (`final`, `abstract`, `serializable`, `interface`, `enum`) are only specified when `true`

## Removed

- The `synthetic` attribute is no longer supported
- Nested classes are no longer including within the nesting class
- Attributes `interface` and `enum` have been removed, use the `kind` attribute instead.

## Current limitations

- Does not include nested classes
- See nodes are not computed correctly
- Line numbers are no longer included
