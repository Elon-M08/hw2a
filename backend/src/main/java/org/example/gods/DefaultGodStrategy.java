// src/main/java/org/example/gods/DefaultGodStrategy.java
package org.example.gods;

/**
 * Default Strategy Implementation.
 * Behaves as a standard player without any special powers.
 */
public class DefaultGodStrategy extends AbstractGodStrategy {
    @Override
    public String getName() {
        return "Default";
    }

    // No additional overrides needed as it uses the default behavior
}
