[ ![Download](https://api.bintray.com/packages/pageseeder/maven/xmldoclet/images/download.svg) ](https://bintray.com/pageseeder/maven/xmldoclet/_latestVersion)

# XML Doclet

This library defines a Doclet for use with the Javadoc tool in order to
export Java documentation as XML.

Note: There are other doctlet out there doing the same job, but most of them 
seem no longer maintained or do not support the notations introduced with Java 5.

How to use:
```
javadoc -doclet org.pageseeder.xmldoclet.XMLDoclet \
    -docletpath pso-xmldoclet-0.9.1.jar:jtidy-r938.jar \
    -sourcepath <pathlist> [packagenames] -d <directory>
```

## About this library

To resolve the tools jar dependency update your Maven settings to inlcude
the `jdk.home` variable. for example:

```
 <profile>
   <id>default-tools.jar</id>
   <properties>
     <jdk.home>C:/Program Files/Java/jdk1.8.0_60</jdk.home>
   </properties>
 </profile>
```
