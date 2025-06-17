package com.github.romanqed.jeflect.meta;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Container for lambda classes.
 *
 * @param <T> the type corresponding to the lambda type
 */
public final class LambdaType<T> {
    private final Class<T> type;
    private final Method method;

    private LambdaType(Class<T> type, Method method) {
        this.type = type;
        this.method = method;
    }

    /**
     * Checks and packages the passed class.
     *
     * @param type class to be packaged
     * @param <T>  the type corresponding to the lambda type
     * @return instance of {@link LambdaType}
     */
    public static <T> LambdaType<T> of(Class<T> type) {
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Invalid lambda class");
        }
        var found = Arrays.
                stream(type.getMethods()).
                filter(e -> Modifier.isAbstract(e.getModifiers())).
                collect(Collectors.toList());
        if (found.size() != 1) {
            throw new IllegalArgumentException("Invalid lambda class");
        }
        return new LambdaType<>(type, found.get(0));
    }

    /**
     * @return java class object, contains lambda type
     */
    public Class<T> getLambdaType() {
        return type;
    }

    /**
     * @return java method object, contains lambda method
     */
    public Method getLambdaMethod() {
        return method;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        var that = (LambdaType<?>) object;

        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return "Lambda " + type.getSimpleName() + " with method " + method.getName();
    }
}
