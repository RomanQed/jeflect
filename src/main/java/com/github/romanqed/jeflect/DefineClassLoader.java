package com.github.romanqed.jeflect;

public class DefineClassLoader extends ClassLoader implements Definer {
    @Override
    public Class<?> define(String name, byte[] buffer) {
        return defineClass(name, buffer, 0, buffer.length);
    }
}
