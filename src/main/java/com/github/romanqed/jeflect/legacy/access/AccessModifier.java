package com.github.romanqed.jeflect.legacy.access;

import com.github.romanqed.jeflect.legacy.ByteClass;
import com.github.romanqed.jeflect.legacy.ByteField;
import com.github.romanqed.jeflect.legacy.ByteMethod;

/**
 * A class representing an abstract mechanism that allows the filter to notify the transformer of the need to change the
 * visibility modifiers of a specified class.
 */
public interface AccessModifier {

    /**
     * Sets the class modifiers.
     *
     * @param clazz  {@link ByteClass} instance containing data about the class being modified
     * @param access modifiers to be set, constants can be taken from {@link java.lang.reflect.Modifier}
     */
    void setAccess(ByteClass clazz, int access);

    /**
     * Sets the method (or constructor) modifiers.
     *
     * @param method {@link ByteMethod} instance containing data about the method being modified
     * @param access modifiers to be set, constants can be taken from {@link java.lang.reflect.Modifier}
     */
    void setAccess(ByteMethod method, int access);

    /**
     * Sets the field modifiers.
     *
     * @param field  {@link ByteField} instance containing data about the field being modified
     * @param access modifiers to be set, constants can be taken from {@link java.lang.reflect.Modifier}
     */
    void setAccess(ByteField field, int access);
}
