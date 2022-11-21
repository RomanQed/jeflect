package com.github.romanqed.jeflect.access;

import com.github.romanqed.jeflect.ByteClass;
import com.github.romanqed.jeflect.ByteField;
import com.github.romanqed.jeflect.ByteMethod;

/**
 * A class representing an abstract mechanism that allows the filter to notify the transformer of the need to change the
 * visibility modifiers of a specified class.
 */
public interface AccessModifier {

    /**
     * Sets the class visibility modifier.
     *
     * @param clazz  {@link ByteClass} instance containing data about the class being modified
     * @param access visibility modifier to be set, constants can be taken from {@link java.lang.reflect.Modifier}
     */
    void setAccess(ByteClass clazz, int access);

    /**
     * Sets the method (or constructor) visibility modifier.
     *
     * @param method {@link ByteMethod} instance containing data about the method being modified
     * @param access visibility modifier to be set, constants can be taken from {@link java.lang.reflect.Modifier}
     */
    void setAccess(ByteMethod method, int access);

    /**
     * Sets the field visibility modifier.
     *
     * @param field  {@link ByteField} instance containing data about the field being modified
     * @param access visibility modifier to be set, constants can be taken from {@link java.lang.reflect.Modifier}
     */
    void setAccess(ByteField field, int access);
}
