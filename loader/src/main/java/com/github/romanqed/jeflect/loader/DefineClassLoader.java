package com.github.romanqed.jeflect.loader;

/**
 * The default {@link DefineLoader} implementation used by the lambda factory.
 * <p>
 * This class extends {@link ClassLoader} and provides capabilities for defining and loading
 * classes dynamically from bytecode at runtime.
 */
public final class DefineClassLoader extends ClassLoader implements DefineLoader {

    /**
     * Constructs a new {@code DefineClassLoader} with the system class loader as its parent.
     */
    public DefineClassLoader() {
        super();
    }

    /**
     * Constructs a new {@code DefineClassLoader} with the specified parent class loader.
     *
     * @param parent the parent class loader
     */
    public DefineClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> define(String name, byte[] buffer) {
        return defineClass(name, buffer, 0, buffer.length);
    }

    @Override
    public Class<?> load(String name) {
        try {
            return loadClass(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        return this;
    }
}
