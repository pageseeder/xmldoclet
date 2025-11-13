package org.example;

/**
 * A fruity collection.
 *
 * <p>Some fruit {@verb might} go off.</p>
 *
 * @banner Fruits are good for you!
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
   * @deprecated We don't like pineapples anymore, use {@link #APPLE} instead
   */
  @Deprecated
  PINEAPPLE
}
