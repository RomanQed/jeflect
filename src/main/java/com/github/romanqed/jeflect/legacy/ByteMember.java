package com.github.romanqed.jeflect.legacy;

/**
 * A class representing a class member (field, method, constructor, etc).
 */
public interface ByteMember extends ByteAnnotated {
    /**
     * @return the name of the member
     */
    String getName();

    /**
     * Returns member modifiers. Can be read by standard means of reflection.
     * For example, {@link java.lang.reflect.Modifier}.
     *
     * @return member modifiers
     */
    int getModifiers();

    /**
     * Returns the {@link ByteClass} instance containing this member.
     * If this method is called from {@link ByteClass}, it will return null.
     *
     * @return the {@link ByteClass} instance containing this member
     */
    ByteClass getDeclaringClass();
}
