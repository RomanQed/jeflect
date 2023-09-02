package com.github.romanqed.jeflect.legacy.binding;

import com.github.romanqed.jeflect.legacy.DefineClassLoader;
import com.github.romanqed.jeflect.legacy.DefineLoader;

import java.lang.reflect.Modifier;
import java.util.Objects;

import static com.github.romanqed.jeflect.legacy.binding.Util.getName;

/**
 * A class describing a factory that creates objects inherited from the specified interface
 * and containing an implementation from the class of the specified object.
 */
public final class BindingFactory {
    private final DefineLoader loader;

    public BindingFactory(DefineLoader loader) {
        this.loader = Objects.requireNonNull(loader);
    }

    public BindingFactory() {
        this.loader = new DefineClassLoader();
    }

    public DefineLoader getLoader() {
        return loader;
    }

    /**
     * Generates a proxy inherited from the desired interface and instantiates it.
     *
     * @param interfaceType interface class
     * @param bind          instance of the object to bind
     * @param <T>           interface type
     * @return proxy instance
     */
    @SuppressWarnings("unchecked")
    public <T> T bind(InterfaceType<T> interfaceType, Object bind) {
        Objects.requireNonNull(interfaceType);
        Objects.requireNonNull(bind);
        Class<?> type = interfaceType.getType();
        Class<?> target = bind.getClass();
        // Check for cast types
        if (type.isAssignableFrom(target)) {
            return (T) bind;
        }
        if (Modifier.isAbstract(target.getModifiers())) {
            throw new IllegalArgumentException("Target must be non-abstract");
        }
        String name = getName(interfaceType.getType(), target);
        Class<?> proxy = loader.load(name);
        if (proxy == null) {
            byte[] toLoad = new ProxyGenerator(interfaceType, target).create();
            proxy = loader.define(name, toLoad);
        }
        try {
            return (T) proxy.getDeclaredConstructor(target).newInstance(bind);
        } catch (Throwable e) {
            throw new IllegalStateException("Can't bind class due to", e);
        }
    }
}
