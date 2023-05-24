package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Doclet;
import org.pageseeder.xmldoclet.Options;

import javax.tools.Diagnostic;
import java.io.File;
import java.util.Collections;
import java.util.List;

public class DirectoryOption extends XMLDocletOption {

  public DirectoryOption(Options options) {
    super(options);
  }

  @Override
  public int getArgumentCount() {
    return 1;
  }

  @Override
  public String getDescription() {
    return "the directory where output is placed.";
  }

  @Override
  public Kind getKind() {
    return Kind.STANDARD;
  }

  @Override
  public List<String> getNames() {
    return Collections.singletonList("-d");
  }

  @Override
  public String getParameters() {
    return "<directory>";
  }

  @Override
  public boolean process(String option, List<String> arguments) {
      // TODO

//    // Output directory
//    if (has(options, "-d")) {
//        String directory = get(options, "-d");
//        if (directory == null) {
//            reporter.print(Diagnostic.Kind.ERROR, "Missing value for <directory>, usage:");
//            reporter.print(Diagnostic.Kind.ERROR,"-d <directory> Destination directory for output files");
//            return null;
//        } else {
//            o.directory = new File(directory);
//            // TODO check
//            reporter.print(Diagnostic.Kind.NOTE, "Output directory: "+directory);
//        }
//    } else {
//        reporter.print(Diagnostic.Kind.ERROR, "Output directory not specified; use -d <directory>");
//        return null;
//    }
      return false;
    }
}
