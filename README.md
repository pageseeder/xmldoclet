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