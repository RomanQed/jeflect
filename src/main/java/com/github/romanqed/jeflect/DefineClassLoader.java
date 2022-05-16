package com.github.romanqed.jeflect;

import java.security.AccessController;
import java.security.PrivilegedAction;

final class DefineClassLoader extends ClassLoader implements DefineLoader {
    @Override
    public Class<?> define(String name, byte[] buffer) {
        return AccessController.doPrivileged(
                (PrivilegedAction<Class<?>>) () -> defineClass(name, buffer, 0, buffer.length)
        );
    }
}
