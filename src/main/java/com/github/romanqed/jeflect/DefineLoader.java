package com.github.romanqed.jeflect;

/**
 * An interface describing the mechanism that loads a class into memory.
 */
public interface DefineLoader {
    /**
     * Defines a class in memory and returns its instance
     *
     * @param name   the name with which the class will be defined
     * @param buffer buffer containing the byte code of the class
     * @return the {@link Class} containing the loaded class
     */
    Class<?> define(String name, byte[] buffer);
}
