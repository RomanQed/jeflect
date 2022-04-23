package com.github.romanqed.jeflect.lambdas;

import java.lang.invoke.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 *
 */
public final class MetaFactory {
    private final MethodHandles.Lookup lookup;

    public MetaFactory(MethodHandles.Lookup lookup) {
        this.lookup = Objects.requireNonNull(lookup);
    }

    /**
     * @return
     */
    public MethodHandles.Lookup getLookup() {
        return lookup;
    }

    /**
     * @param method
     * @return
     * @throws IllegalAccessException
     */
    public MethodType extractType(Method method) throws IllegalAccessException {
        Objects.requireNonNull(method);
        MethodHandle handle = lookup.unreflect(method);
        return handle.type();
    }

    /**
     * @param method
     * @return
     * @throws IllegalAccessException
     */
    public MethodType extractDynamicType(Method method) throws IllegalAccessException {
        return extractType(method).dropParameterTypes(0, 1);
    }

    /**
     * @param clazz
     * @param handle
     * @param bind
     * @param <T>
     * @return
     * @throws Throwable
     */
    @SuppressWarnings("unchecked")
    public <T> T packLambdaHandle(LambdaClass<T> clazz, MethodHandle handle, Object bind) throws Throwable {
        Objects.requireNonNull(clazz);
        Method lambdaMethod = clazz.getLambdaMethod();
        MethodType lambdaType = extractDynamicType(lambdaMethod);
        MethodType bindType = MethodType.methodType(clazz.getLambdaClass());
        MethodType sourceType = handle.type();
        if (bind != null) {
            bindType = bindType.appendParameterTypes(bind.getClass());
            sourceType = sourceType.dropParameterTypes(0, 1);
        }
        CallSite callSite = LambdaMetafactory.metafactory(
                lookup,
                lambdaMethod.getName(),
                bindType,
                lambdaType,
                handle,
                sourceType
        );
        MethodHandle ret = bind == null ? callSite.getTarget() : callSite.getTarget().bindTo(bind);
        return (T) ret.invoke();
    }

    /**
     * @param clazz
     * @param method
     * @param bind
     * @param <T>
     * @return
     * @throws Throwable
     */
    public <T> T packLambdaMethod(LambdaClass<T> clazz, Method method, Object bind) throws Throwable {
        Objects.requireNonNull(method);
        MethodHandle handle = lookup.unreflect(method);
        return packLambdaHandle(clazz, handle, bind);
    }

    /**
     * @param clazz
     * @param constructor
     * @param <T>
     * @return
     * @throws Throwable
     */
    public <T> T packLambdaConstructor(LambdaClass<T> clazz, Constructor<?> constructor) throws Throwable {
        Objects.requireNonNull(constructor);
        MethodHandle handle = lookup.unreflectConstructor(constructor);
        return packLambdaHandle(clazz, handle, null);
    }

    /**
     * @param clazz
     * @param method
     * @param <T>
     * @return
     * @throws Throwable
     */
    public <T> T packLambdaMethod(LambdaClass<T> clazz, Method method) throws Throwable {
        return packLambdaMethod(clazz, method, null);
    }
}

