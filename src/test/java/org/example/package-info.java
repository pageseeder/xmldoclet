/**
 * A collection of sample Java elements to test the XML doclet.
 *
 * <p>Below is some sample preformatted code block:
 * <pre>{@code
 *  javadoc -doclet org.pageseeder.xmldoclet.XMLDoclet \
 *        -docletpath target/classes -docletpath target/test-classes \
 *        -sourcepath src/test/java \
 *        org.example}</pre>
 *
 * <p>Some text with <i>inline</i> <b>markup</b>.
 *
 * <p>And a list!
 * <ul>
 *   <li>Item 1 with {@link org.example.SampleEnum#ORANGES}
 *   <li>Item 2
 * </ul>
 *
 * @see <a href="https://example.org">Example 1</a>
 * @see <a href="https://example.net">Example 2</a>
 *
 * @author John Smith
 * @author Jane Smith
 *
 * @version 1.0
 * @since 0.5
 *
 * @warning This is a sample {@glossary warning!}.
 */
package org.example;
