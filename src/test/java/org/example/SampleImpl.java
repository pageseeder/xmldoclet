package org.example;

import jdk.jfr.Experimental;

/**
 * A sample implementation.
 *
 * <p>Some <b>documentation</b> with invalid xml code: <code>{@code  <&>}</code>
 * and <i>inline</i> markup.
 *
 * <ul>
 *   <li>Item 1
 *   <li>Item 2
 * </ul>
 *
 */
@Experimental
public class SampleImpl extends SampleBase {

  SampleImpl(String something) {
    super(something);
  }

  @Override
  public void process(Object stuff) {
    // Do nothing
  }

}
