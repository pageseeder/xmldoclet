package org.example;

/**
 * A fruity collection.
 *
 * @author John Smith
 * @author Jane Smith
 * @version 1.0
 * @since 0.5
 */
public enum SampleEnum {

  APPLE,

  ORANGES,

  /**
   * @deprecated We don't like pineapples anymore.
   */
  @Deprecated
  PINEAPPLE
}
