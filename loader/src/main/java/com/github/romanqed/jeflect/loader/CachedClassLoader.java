package com.github.romanqed.jeflect.loader;

import java.util.Objects;

/**
 * A {@link DefineLoader} implementation that integrates a {@link ClassCache}
 * for storing and retrieving class bytecode.
 * <p>
 * This loader wraps another {@link DefineLoader} and adds caching functionality:
 * <ul>
 *     <li>When defining a class, the bytecode is automatically stored in the cache.</li>
 *     <li>When loading a class, if it cannot be loaded normally, the cache is checked and
 *         the class is defined using the stored bytecode.</li>
 * </ul>
 */
public final class CachedClassLoader implements DefineLoader {
    private final DefineLoader loader;
    private final ClassCache cache;

    /**
     * Constructs a new {@code CachedClassLoader}.
     *
     * @param loader the underlying {@link DefineLoader} responsible for defining and loading classes
     * @param cache  the bytecode cache to store and retrieve class definitions
     * @throws NullPointerException if either argument is {@code null}
     */
    public CachedClassLoader(DefineLoader loader, ClassCache cache) {
        this.loader = Objects.requireNonNull(loader);
        this.cache = Objects.requireNonNull(cache);
    }

    /**
     * Defines a class using the underlying loader and stores its bytecode in the cache.
     *
     * @param name   the fully qualified name of the class
     * @param buffer the bytecode of the class
     * @return the defined {@link Class} object
     */
    @Override
    public Class<?> define(String name, byte[] buffer) {
        var ret = loader.define(name, buffer);
        cache.set(name, buffer);
        return ret;
    }

    /**
     * Attempts to load a class by name.
     * <p>
     * If the class cannot be loaded normally, it checks the cache and defines the class
     * using the stored bytecode if available.
     *
     * @param name the fully qualified name of the class
     * @return the loaded {@link Class} object, or {@code null} if not found
     */
    @Override
    public Class<?> load(String name) {
        var ret = loader.load(name);
        if (ret != null) {
            return ret;
        }
        var buffer = cache.get(name);
        if (buffer == null) {
            return null;
        }
        return loader.define(name, buffer);
    }

    /**
     * Returns the {@link ClassLoader} associated with the underlying loader.
     *
     * @return the backing {@link ClassLoader}
     */
    @Override
    public ClassLoader getClassLoader() {
        return loader.getClassLoader();
    }
}
