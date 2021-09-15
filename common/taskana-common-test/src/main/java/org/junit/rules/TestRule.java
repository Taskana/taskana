package org.junit.rules;

// Testcontainers currently requires junit4 as a runtime dependency.
// Because we use junit5 we have to use this workaround to "simulate" the classes testcontainers
// requires in the classpath. They are not used unless a junit4 runtime is used.
// See: https://github.com/testcontainers/testcontainers-java/issues/970#issuecomment-625044008
@SuppressWarnings("unused")
public interface TestRule {}
