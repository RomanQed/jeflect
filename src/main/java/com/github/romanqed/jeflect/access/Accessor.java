package com.github.romanqed.jeflect.access;

import com.github.romanqed.jeflect.ByteClass;

import java.util.function.BiConsumer;

/**
 * A class that represents an abstract interface
 * that allows you to redefine the visibility modifiers of classes and their members.
 */
public interface Accessor {

    /**
     * Starts the process of changing visibility modifiers.
     *
     * @param loader   a loader whose task is to load the required classes using a classloader or direct access
     * @param consumer a filter function that specifies the required access modifiers
     *                 for the class being modified and its members
     */
    void setAccess(Runnable loader, BiConsumer<AccessModifier, ByteClass> consumer);
}
