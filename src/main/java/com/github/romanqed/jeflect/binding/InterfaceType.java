package com.github.romanqed.jeflect.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A class that describes a binding interface and stores information about it.
 *
 * @param <T> type of interface class
 */
public final class InterfaceType<T> {
    private final Class<T> type;
    private final boolean isInterface;
    private final Collection<Method> abstractMethods;
    private final Collection<Method> defaultMethods;

    private InterfaceType(Class<T> type, Collection<Method> abstractMethods, Collection<Method> defaultMethods) {
        this.type = type;
        this.isInterface = type.isInterface();
        this.abstractMethods = abstractMethods;
        this.defaultMethods = defaultMethods;
    }

    /**
     * Creates {@link InterfaceType} for a class.
     *
     * @param clazz the class to be processed
     * @param <T>   class type
     * @return {@link InterfaceType} containing information about the class
     */
    public static <T> InterfaceType<T> fromClass(Class<T> clazz) {
        if (!clazz.isInterface()) {
            throw new IllegalStateException("The class must be interface");
        }
        List<Method> abstractMethods = new LinkedList<>();
        List<Method> defaultMethods = new LinkedList<>();
        extractMethods(clazz, abstractMethods, defaultMethods);
        if (abstractMethods.isEmpty() && defaultMethods.isEmpty()) {
            throw new IllegalStateException("No method suitable for binding has been found");
        }
        return new InterfaceType<>(clazz,
                Collections.unmodifiableList(abstractMethods),
                Collections.unmodifiableList(defaultMethods));
    }

    private static void extractMethods(Class<?> clazz, List<Method> abstractMethods, List<Method> defaultMethods) {
        for (Method method : clazz.getMethods()) {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers)) {
                continue;
            }
            if (Modifier.isAbstract(modifiers)) {
                abstractMethods.add(method);
            } else if (method.isAnnotationPresent(Overridable.class)) {
                defaultMethods.add(method);
            }
        }
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public Collection<Method> getAbstractMethods() {
        return abstractMethods;
    }

    public Collection<Method> getDefaultMethods() {
        return defaultMethods;
    }
}
