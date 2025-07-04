package com.github.romanqed.jeflect.cloner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * The no operation implementation of {@link ReflectCloner}.
 * Returns the same instances that were given.
 */
public final class NoopReflectCloner implements ReflectCloner {

    @Override
    public Constructor<?> clone(Constructor<?> constructor) {
        return constructor;
    }

    @Override
    public Method clone(Method method) {
        return method;
    }

    @Override
    public Field clone(Field field) {
        return field;
    }
}
