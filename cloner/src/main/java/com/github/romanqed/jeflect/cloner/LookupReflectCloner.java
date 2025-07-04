package com.github.romanqed.jeflect.cloner;

import java.lang.reflect.*;

/**
 * The implementation of the {@link ReflectCloner} that lookups over members cache to get copy of given instance.
 * Throws an {@link IllegalStateException} if it cannot find the desired member in the cache.
 * Normally, this should never happen in a JVM following the usual specifications.
 * If this does happen, the problem is definitely on the side of your runtime.
 */
public final class LookupReflectCloner implements ReflectCloner {

    private static boolean isPublic(Member object) {
        return Modifier.isPublic(object.getModifiers());
    }

    @Override
    public Constructor<?> clone(Constructor<?> constructor) {
        var owner = constructor.getDeclaringClass();
        var constructors = isPublic(constructor) ? owner.getConstructors() : owner.getDeclaredConstructors();
        for (var entry : constructors) {
            if (constructor.equals(entry)) {
                return entry;
            }
        }
        throw new IllegalStateException("Cannot found given constructor, the JVM may be corrupted");
    }

    @Override
    public Method clone(Method method) {
        var owner = method.getDeclaringClass();
        var methods = isPublic(method) ? owner.getMethods() : owner.getDeclaredMethods();
        for (var entry : methods) {
            if (method.equals(entry)) {
                return entry;
            }
        }
        throw new IllegalStateException("Cannot found given method, the JVM may be corrupted");
    }

    @Override
    public Field clone(Field field) {
        var owner = field.getDeclaringClass();
        var fields = isPublic(field) ? owner.getFields() : owner.getDeclaredFields();
        for (var entry : fields) {
            if (field.equals(entry)) {
                return entry;
            }
        }
        throw new IllegalStateException("Cannot found given field, the JVM may be corrupted");
    }
}
