package com.github.romanqed.jeflect.legacy.fields;

import com.github.romanqed.jeflect.DefineClassLoader;
import com.github.romanqed.jeflect.DefineLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * <p>A class representing a factory that creates
 * {@link FieldAccessor} instances for subsequent access to the field.</p>
 * <p>Access occurs at almost native speed, minus the time to call the proxy class method.</p>
 */
public final class FieldAccessorFactory {
    private static final FieldProxyFactory FACTORY = new FieldProxyFactory();
    private static final String PROXY = "FieldProxy";
    private static final String STATIC = "S";
    private static final String VIRTUAL = "V";
    private final DefineLoader loader;

    public FieldAccessorFactory(DefineLoader loader) {
        this.loader = Objects.requireNonNull(loader);
    }

    public FieldAccessorFactory() {
        this.loader = new DefineClassLoader();
    }

    /**
     * @return {@link DefineLoader} instance, which is used to define proxies
     */
    public DefineLoader getLoader() {
        return loader;
    }

    /**
     * Packages access to the passed field with {@link FieldAccessor}
     *
     * @param field field to be packaged
     * @return constructed {@link FieldAccessor} instance
     */
    public FieldAccessor packField(Field field) {
        boolean isStatic = Modifier.isStatic(field.getModifiers());
        String name = (isStatic ? STATIC : VIRTUAL) + PROXY + field.hashCode();
        Class<?> proxy = loader.load(name);
        if (proxy == null) {
            byte[] toLoad = FACTORY.create(name, field);
            proxy = loader.define(name, toLoad);
        }
        try {
            return (FieldAccessor) proxy.getDeclaredConstructor().newInstance();
        } catch (Throwable e) {
            throw new IllegalStateException("Can't pack field due to", e);
        }
    }
}
