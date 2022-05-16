package com.github.romanqed.jeflect.meta;

import java.lang.invoke.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * A class describing a factory that packages methods with a pre-known signature into lambda interfaces.
 */
public final class MetaFactory {
    private final MethodHandles.Lookup lookup;

    public MetaFactory(MethodHandles.Lookup lookup) {
        this.lookup = Objects.requireNonNull(lookup);
    }

    public MetaFactory() {
        this.lookup = MethodHandles.lookup();
    }

    /**
     * @return {@link java.lang.invoke.MethodHandles.Lookup} instance, which is used to package methods
     */
    public MethodHandles.Lookup getLookup() {
        return lookup;
    }

    /**
     * Extracts the type from the passed method.
     *
     * @param method method for extraction
     * @return extracted type
     * @throws IllegalAccessException if it couldn't access the method
     */
    public MethodType extractType(Method method) throws IllegalAccessException {
        Objects.requireNonNull(method);
        MethodHandle handle = lookup.unreflect(method);
        return handle.type();
    }

    /**
     * Extracts the type from the passed dynamic method.
     *
     * @param method method for extraction
     * @return extracted type
     * @throws IllegalAccessException if it couldn't access the method
     */
    public MethodType extractDynamicType(Method method) throws IllegalAccessException {
        return extractType(method).dropParameterTypes(0, 1);
    }

    /**
     * Packages {@link MethodHandle} into the passed {@link LambdaType}.
     *
     * @param clazz  lambda class for packaging
     * @param handle handle for packaging
     * @param bind   instance of the object to which the packaged method will be bound
     *               (if null, the method will be considered static)
     * @param <T>    type of packing lambda
     * @return the object instantiating the passed lambda
     * @throws Throwable if any errors occurred during the packaging process
     */
    @SuppressWarnings("unchecked")
    public <T> T packLambdaHandle(LambdaType<T> clazz, MethodHandle handle, Object bind) throws Throwable {
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
     * Automatically unreflects and packages {@link Method} into the passed {@link LambdaType}.
     *
     * @param clazz  lambda class for packaging
     * @param method method for packaging
     * @param bind   instance of the object to which the packaged method will be bound
     *               (if null, the method will be considered static)
     * @param <T>    type of packing lambda
     * @return the object instantiating the passed lambda
     * @throws Throwable if any errors occurred during the packaging process
     */
    public <T> T packLambdaMethod(LambdaType<T> clazz, Method method, Object bind) throws Throwable {
        Objects.requireNonNull(method);
        MethodHandle handle = lookup.unreflect(method);
        return packLambdaHandle(clazz, handle, bind);
    }

    /**
     * Automatically unreflects and packages {@link Constructor} into the passed {@link LambdaType}.
     *
     * @param clazz       lambda class for packaging
     * @param constructor constructor for packaging
     * @param <T>         type of packing lambda
     * @return the object instantiating the passed lambda
     * @throws Throwable if any errors occurred during the packaging process
     */
    public <T> T packLambdaConstructor(LambdaType<T> clazz, Constructor<?> constructor) throws Throwable {
        Objects.requireNonNull(constructor);
        MethodHandle handle = lookup.unreflectConstructor(constructor);
        return packLambdaHandle(clazz, handle, null);
    }

    /**
     * Automatically unreflects and packages static {@link Method} into the passed {@link LambdaType}.
     *
     * @param clazz  lambda class for packaging
     * @param method method for packaging
     * @param <T>    type of packing lambda
     * @return the object instantiating the passed lambda
     * @throws Throwable if any errors occurred during the packaging process
     */
    public <T> T packLambdaMethod(LambdaType<T> clazz, Method method) throws Throwable {
        return packLambdaMethod(clazz, method, null);
    }
}
