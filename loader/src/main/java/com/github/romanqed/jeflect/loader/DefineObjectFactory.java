package com.github.romanqed.jeflect.loader;

import com.github.romanqed.jfunc.Function1;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * An implementation of {@link ObjectFactory} that uses a {@link DefineLoader}
 * to define and instantiate objects from bytecode at runtime.
 *
 * @param <T> the type of objects produced by this factory
 */
public final class DefineObjectFactory<T> implements ObjectFactory<T> {
    private final DefineLoader loader;

    /**
     * Constructs a new factory with the given {@link DefineLoader}.
     *
     * @param loader the class loader used to define and load classes
     * @throws NullPointerException if {@code loader} is {@code null}
     */
    public DefineObjectFactory(DefineLoader loader) {
        this.loader = Objects.requireNonNull(loader);
    }

    /**
     * Returns the {@link DefineLoader} used by this factory.
     *
     * @return the associated {@link DefineLoader}
     */
    public DefineLoader getLoader() {
        return loader;
    }

    @Override
    public T create(String name, Callable<byte[]> provider, Function1<Class<?>, ? extends T> creator) {
        var clazz = this.loader.load(name);
        try {
            if (clazz == null) {
                var bytes = provider.call();
                clazz = this.loader.define(name, bytes);
            }
            return creator.invoke(clazz);
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T create(String name, Callable<byte[]> provider) {
        return create(name, provider, clazz ->
                (T) clazz.getDeclaredConstructor((Class<?>[]) null).newInstance((Object[]) null)
        );
    }
}
