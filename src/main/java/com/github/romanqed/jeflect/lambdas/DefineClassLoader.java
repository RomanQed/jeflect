package com.github.romanqed.jeflect.lambdas;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * The default {@link DefineLoader} used by the lambda factory.
 */
public final class DefineClassLoader extends ClassLoader implements DefineLoader {
    @Override
    public Class<?> define(String name, byte[] buffer) {
        return AccessController.doPrivileged(
                (PrivilegedAction<Class<?>>) () -> defineClass(name, buffer, 0, buffer.length)
        );
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
