package com.github.romanqed.jeflect;

final class DefineClassLoader extends ClassLoader implements Definer {
    @Override
    public Class<?> define(String name, byte[] buffer) {
        return defineClass(name, buffer, 0, buffer.length);
    }
}
