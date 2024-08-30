package com.github.romanqed.jeflect.lambda;

import com.github.romanqed.jeflect.loader.DefineClassLoader;
import com.github.romanqed.jeflect.loader.DefineLoader;
import com.github.romanqed.jeflect.loader.DefineObjectFactory;
import com.github.romanqed.jeflect.loader.ObjectFactory;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * A bytecode factory that generates the bytecode of a proxy class for methods and constructors.
 */
public final class BytecodeLambdaFactory implements LambdaFactory {
    private static final String PROXY = "Proxy";
    private final ObjectFactory<Lambda> factory;

    public BytecodeLambdaFactory(ObjectFactory<Lambda> factory) {
        this.factory = Objects.requireNonNull(factory);
    }

    public BytecodeLambdaFactory(DefineLoader loader) {
        this(new DefineObjectFactory<>(loader));
    }

    public BytecodeLambdaFactory() {
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

    @Override
    public Lambda packMethod(Method method) {
        var name = getProxyName(method);
        return factory.create(name, () -> ProxyUtil.createProxy(name, method));
    }

    @Override
    public Lambda packConstructor(Constructor<?> constructor) {
        var name = getProxyName(constructor);
        return factory.create(name, () -> ProxyUtil.createProxy(name, constructor));
    }
}
