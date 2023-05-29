package org.example;

public interface SampleInterface {

  /**
   * @return something
   */
  String writeSomething();

  /**
   * Process some stuff.
   *
   * @param stuff The stuff to process.
   *
   * @throws NullPointerException You must specify some stuff!
   */
  void process(Object stuff);

  /**
   * Do nothing, if possible.
   */
  default void doNothing(){}

}
