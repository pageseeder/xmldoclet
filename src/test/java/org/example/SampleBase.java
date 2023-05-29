package org.example;

public abstract class SampleBase implements SampleInterface {

  protected final String something;

  SampleBase(String something) {
    this.something = something;
  }

  @Override
  public final String writeSomething() {
    return this.something;
  }

}
