package com.github.romanqed.jeflect.lambdas;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * <p>A class describing a factory that packages methods with any signature into a general-looking lambda interface.</p>
 * <p>Calls to target methods can be made a little slower than meta-lambdas calls,</p>
 * <p>since copying arguments on the stack and possible packing/unpacking are inevitable.</p>
 */
public final class LambdaFactory {
    private static final ProxyFactory BOUND_FACTORY = new BoundProxyFactory();
    private static final ProxyFactory FREE_FACTORY = new FreeProxyFactory();
    private static final String PROXY = "Proxy";
    private static final String STATIC = "S";
    private static final String VIRTUAL = "V";
    private static final String BOUNDED = "B";

    private final DefineLoader loader;

    public LambdaFactory(DefineLoader loader) {
        this.loader = Objects.requireNonNull(loader);
    }

    public LambdaFactory() {
        this.loader = new DefineClassLoader();
    }

    /**
     * @return {@link DefineLoader} instance, which is used to define proxies
     */
    public DefineLoader getLoader() {
        return loader;
    }

    /**
     * Packages the passed virtual method into a {@link Lambda}.
     *
     * @param method method for packaging
     * @param bind   instance of the object to which the packaged method will be bound
     * @return the object instantiating the {@link Lambda}
     */
    public Lambda packMethod(Method method, Object bind) {
        boolean bound = bind != null;
        Class<?> owner = method.getDeclaringClass();
        if (bound && !owner.isAssignableFrom(bind.getClass())) {
            throw new IllegalArgumentException("Incorrect bind object type: " + bind.getClass());
        }
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        if (isStatic && bound) {
            throw new IllegalArgumentException("Can't bind static method to object");
        }
        String name = (isStatic ? STATIC : VIRTUAL + (bound ? BOUNDED : "")) + PROXY + method.hashCode();
        Class<?> proxy = loader.load(name);
        if (proxy == null) {
            ProxyFactory factory = bound ? BOUND_FACTORY : FREE_FACTORY;
            byte[] toLoad = factory.create(name, method);
            proxy = loader.define(name, toLoad);
        }
        try {
            if (bound) {
                return (Lambda) proxy.getDeclaredConstructor(owner).newInstance(bind);
            }
            return (Lambda) proxy.getDeclaredConstructor().newInstance();
        } catch (Throwable e) {
            throw new IllegalStateException("Can't pack method due to", e);
        }
    }

    /**
     * Packages the passed method into a {@link Lambda}.
     *
     * @param method method for packaging
     * @return the object instantiating the {@link Lambda}
     */
    public Lambda packMethod(Method method) {
        return packMethod(method, null);
    }
}
