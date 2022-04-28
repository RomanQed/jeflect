package com.github.romanqed.jeflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

abstract class PackFactory {
    private static final String PROXY = "Proxy";
    private final Map<Long, Class<?>> virtuals;
    private final Map<Long, Class<?>> statics;
    private final DefineLoader loader;

    protected PackFactory(Map<Long, Class<?>> virtuals, Map<Long, Class<?>> statics, DefineLoader loader) {
        this.virtuals = virtuals;
        this.statics = statics;
        this.loader = loader;
    }

    private long combineHashes(int left, int right) {
        return left > right ? (right | (long) left << 32) : (left | (long) right << 32);
    }

    protected void checkMethod(Method method) {
        int modifiers = method.getModifiers();
        // Check for class accessibility
        if (!Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
            throw new IllegalArgumentException("The class must be public");
        }
        // Check for method accessibility
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalArgumentException("The method must be public");
        }
    }

    protected Class<?> findClass(Method method, boolean isStatic) {
        Class<?> owner = method.getDeclaringClass();
        long hash = combineHashes(owner.hashCode(), method.hashCode());
        Class<?> ret = isStatic ? statics.get(hash) : virtuals.get(hash);
        if (ret != null) {
            return ret;
        }
        hash = combineHashes(owner.hashCode(), method.hashCode());
        String name = PROXY + hash;
        byte[] bytes = createClass(name, owner, method, isStatic);
        ret = loader.define(name, bytes);
        if (isStatic) {
            statics.put(hash, ret);
        } else {
            virtuals.put(hash, ret);
        }
        return ret;
    }

    protected abstract byte[] createClass(String name, Class<?> owner, Method method, boolean isStatic);
}
