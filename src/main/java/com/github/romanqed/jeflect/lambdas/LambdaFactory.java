package com.github.romanqed.jeflect.lambdas;

import com.github.romanqed.jeflect.DefineClassLoader;
import com.github.romanqed.jeflect.DefineLoader;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * A factory that generates the bytecode of a proxy class for methods and constructors.
 */
public final class LambdaFactory {
    private static final String PROXY = "Proxy";
    private final DefineLoader loader;

    public LambdaFactory(DefineLoader loader) {
        this.loader = Objects.requireNonNull(loader);
    }

    public LambdaFactory() {
        this(new DefineClassLoader());
    }

    private static String getProxyName(Method method) {
        var toHash = method.getDeclaringClass().getName() + method.getName() + Type.getMethodDescriptor(method);
        return PROXY + toHash.hashCode();
    }

    private static String getProxyName(Constructor<?> ctor) {
        var toHash = ctor.getName() + Type.getConstructorDescriptor(ctor);
        return PROXY + toHash.hashCode();
    }

    /**
     * @return {@link DefineLoader} instance, which is used to define proxies
     */
    public DefineLoader getLoader() {
        return loader;
    }

    private Lambda pack(String name, Callable<byte[]> provider) throws Exception {
        var clazz = this.loader.load(name);
        if (clazz == null) {
            var bytes = provider.call();
            clazz = this.loader.define(name, bytes);
        }
        return (Lambda) clazz.getDeclaredConstructor().newInstance();
    }

    /**
     * Creates a proxy implementation of the {@link Lambda} interface for the specified method.
     *
     * @param method the target method
     * @return object of the generated proxy class implementing the {@link Lambda} interface
     * @throws Exception if errors occur during proxy class generation or instantiation
     */
    public Lambda packMethod(Method method) throws Exception {
        var name = getProxyName(method);
        return pack(name, () -> ProxyUtil.createProxy(name, method));
    }

    /**
     * Creates a proxy implementation of the {@link Lambda} interface for the specified constructor.
     *
     * @param constructor the target constructor
     * @return object of the generated proxy class implementing the {@link Lambda} interface
     * @throws Exception if errors occur during proxy class generation or instantiation
     */
    public Lambda packConstructor(Constructor<?> constructor) throws Exception {
        var name = getProxyName(constructor);
        return pack(name, () -> ProxyUtil.createProxy(name, constructor));
    }
}
