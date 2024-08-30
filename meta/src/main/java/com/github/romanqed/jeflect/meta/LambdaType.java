package com.github.romanqed.jeflect.meta;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Container for lambda classes.
 *
 * @param <T> the type corresponding to the lambda type
 */
public final class LambdaType<T> {
    private final Class<T> lambdaType;
    private final Method lambdaMethod;

    private LambdaType(Class<T> type, Method method) {
        this.lambdaType = type;
        this.lambdaMethod = method;
    }

    /**
     * Checks and packages the passed class.
     *
     * @param type class to be packaged
     * @param <T>  the type corresponding to the lambda type
     * @return instance of {@link LambdaType}
     */
    public static <T> LambdaType<T> of(Class<T> type) {
        Objects.requireNonNull(type);
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
        return lambdaType;
    }

    /**
     * @return java method object, contains lambda method
     */
    public Method getLambdaMethod() {
        return lambdaMethod;
    }

    @Override
    public int hashCode() {
        return lambdaType.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LambdaType)) {
            return false;
        }
        return lambdaType.equals(((LambdaType<?>) obj).lambdaType);
    }

    @Override
    public String toString() {
        return "Lambda " + lambdaType.getSimpleName() + " with method " + lambdaMethod.getName();
    }
}
