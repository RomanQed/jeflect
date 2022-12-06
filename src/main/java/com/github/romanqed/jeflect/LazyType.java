package com.github.romanqed.jeflect;

import com.github.romanqed.util.LazyFunction;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * A class representing an abstract type that can be lazily loaded by name.
 */
public final class LazyType implements Type {
    private static final Map<String, Class<?>> PRIMITIVES = getPrimitives();
    private final String className;
    private final Function<String, Class<?>> loader;

    /**
     * Creates a type with the specified class name and a classloader that will load this class.
     *
     * @param className the name of the class, can have any delimiter format, must be non-null
     * @param loader    class loader, must be non-null
     */
    public LazyType(String className, ClassLoader loader) {
        Objects.requireNonNull(className);
        Objects.requireNonNull(loader);
        this.className = className.replace('/', '.');
        if (PRIMITIVES.containsKey(this.className)) {
            this.loader = PRIMITIVES::get;
        } else {
            this.loader = new LazyFunction<>(name -> {
                try {
                    return loader.loadClass(name);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Cannot load class due to", e);
                }
            });
        }
    }

    /**
     * Creates a type with the specified class name and system classloader.
     *
     * @param className the name of the class, can have any delimiter format, must be non-null
     */
    public LazyType(String className) {
        this(className, ClassLoader.getSystemClassLoader());
    }

    private static Map<String, Class<?>> getPrimitives() {
        Map<String, Class<?>> ret = new HashMap<>();
        ret.put("void", void.class);
        ret.put("byte", byte.class);
        ret.put("short", short.class);
        ret.put("int", int.class);
        ret.put("long", long.class);
        ret.put("char", char.class);
        ret.put("float", float.class);
        ret.put("double", double.class);
        ret.put("boolean", boolean.class);
        return ret;
    }

    @Override
    public String getTypeName() {
        return className;
    }

    /**
     * Loads a class and returns an instance of {@link Class} containing information about it.
     *
     * @return {@link Class} that represents this class
     */
    public Class<?> getType() {
        return loader.apply(className);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LazyType)) return false;
        LazyType lazyType = (LazyType) o;
        return className.equals(lazyType.className);
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }

    @Override
    public String toString() {
        return className;
    }
}
