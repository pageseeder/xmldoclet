package org.example;

import java.util.Objects;

/**
 * A sample implementation.
 *
 * <p>Some <b>documentation</b> with {@glossary invalid} xml code: <code>{@code  <&>}</code>
 * and <i>inline</i> markup.
 *
 * <ul>
 *   <li>Item 1
 *   <li>Item 2
 * </ul>
 *
 * @see NestedStatic#toString()
 *
 * @version 1.0
 * @version 2.0
 * @since 0.5
 * @since 0.6
 */
public class SampleImpl extends SampleBase {

  SampleImpl(String something) {
    super(something);
  }

  @Override
  public void process(Object stuff) {
    // Do nothing
  }

  public static class NestedStatic {

    /**
     * The default name to use.
     */
    public static final String DEFAULT_NAME = "Anonymous";

    /**
     * The name to use.
     *
     * <p>Another like for <b>testing only</b>.</p>
     */
    final String _name;

    /**
     * @param name The suggested name.
     */
    NestedStatic(String name) {
      this._name = Objects.toString(name, DEFAULT_NAME);
    }

    /**
     * @return The actual name
     */
    @Override
    public String toString() {
      return this._name;
    }
  }
}
