package org.pageseeder.xmldoclet.options;

import jdk.javadoc.doclet.Reporter;
import org.eclipse.jdt.annotation.Nullable;

import java.util.List;

/**
 * Option to specify the filename of the output.
 *
 * <p>Only used if single output.
 *
 * @see MultipleOption
 *
 * @author Christophe Lauret
 *
 * @version 1.0
 * @since 1.0
 */
public final class FilenameOption extends XMLDocletOptionBase {

  /**
   * The default filename for the output.
   */
  private static final String DEFAULT_FILENAME = "xmldoclet.xml";

  private String filename = DEFAULT_FILENAME;

  public FilenameOption(Reporter reporter) {
    super(reporter);
  }

  @Override
  public int getArgumentCount() {
    return 1;
  }

  @Override
  public String getDescription() {
    return "name of the file (used for single output only)";
  }

  @Override
  public Kind getKind() {
    return Kind.STANDARD;
  }

  @Override
  public List<String> getNames() {
    return List.of("-filename");
  }

  @Override
  public String getParameters() {
    return "<filename>";
  }

  @Override
  public boolean process(String option, List<String> arguments) {
    String name = arguments.get(0);
    if (isValidFilename(name)) {
      this.filename = name;
      note("Output filename: "+name);
    } else {
      error("Invalid filename: "+name+" - must be a valid filename");
      return false;
    }
    return true;
  }

  public String getFilename() {
    return this.filename;
  }

  // Java
  private static boolean isValidFilename(@Nullable String filename) {
    // Disallow empty and null
    if (filename == null || filename.trim().isEmpty()) return false;

    // Define a pattern for invalid characters (for Windows and cross-platform safety)
    String invalidChars = "[\\\\/:*?\"<>|]";
    if (filename.matches(".*" + invalidChars + ".*")) return false;

    // Optionally, limit the length (common maximum for most OSes is 255)
    if (filename.length() > 255) return false;

    // Disallow reserved names (Windows)
    String[] reserved = {
        "CON", "PRN", "AUX", "NUL",
        "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
        "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
    };
    for (String res : reserved) {
      if (filename.equalsIgnoreCase(res) || filename.toUpperCase().startsWith(res + ".")) {
        return false;
      }
    }

    // Looks good
    return true;
  }
}
