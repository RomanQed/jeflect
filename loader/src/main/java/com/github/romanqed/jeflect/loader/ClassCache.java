package com.github.romanqed.jeflect.loader;

/**
 * Represents a cache for class bytecode.
 * <p>
 * This interface allows storing and retrieving bytecode for classes by their fully qualified names.
 * It can be used to persist bytecode for later reuse, e.g., to avoid regenerating or reloading classes.
 */
public interface ClassCache {

    /**
     * Retrieves the bytecode for a class with the given fully qualified name.
     *
     * @param name the fully qualified name of the class (e.g., {@code com.example.MyClass})
     * @return the bytecode of the class, or {@code null} if not present in the cache
     */
    byte[] get(String name);

    /**
     * Stores the bytecode of a class under the given fully qualified name.
     *
     * @param name   the fully qualified name of the class
     * @param buffer the bytecode of the class to store
     */
    void set(String name, byte[] buffer);
}
