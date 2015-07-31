# XML Doclet

This library defines a Doclet for use with the Javadoc tool in order to
export Java documentation as XML.

Note: There are other doctlet out there doing the same job, but most of them seem no longer maintained or do not support 
the notations introduced with Java 5.

How to use:
```
javadoc -doclet org.pageseeder.xmldoclet.XMLDoclet \
    -docletpath lib/wo-xmldoclet-0.8.11.jar:lib/jtidy-r938.jar \
    -sourcepath <pathlist> [packagenames] -d <directory>
```

## About this library

To compile, include 'tools.jar' from your JDK to the classpath.
