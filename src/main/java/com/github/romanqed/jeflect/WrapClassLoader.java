package com.github.romanqed.jeflect;

import com.github.romanqed.jeflect.lambdas.DefineLoader;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

final class WrapClassLoader implements DefineLoader {
    private final ClassLoader body;
    private final Method define;

    WrapClassLoader(ClassLoader body, Method define) {
        this.body = body;
        this.define = define;
    }

    @Override
    public Class<?> define(String name, byte[] buffer) {
        return (Class<?>) AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            try {
                return define.invoke(body, name, buffer, 0, buffer.length);
            } catch (Throwable e) {
                throw new IllegalStateException("Can't define class due to", e);
            }
        });
    }

    @Override
    public Class<?> load(String name) {
        try {
            return body.loadClass(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        return body;
    }
}
