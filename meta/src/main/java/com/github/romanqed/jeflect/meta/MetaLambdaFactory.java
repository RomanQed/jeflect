package com.github.romanqed.jeflect.meta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * An interface describing a factory that packages methods with a pre-known signature into lambda interfaces.
 */
public interface MetaLambdaFactory {

    /**
     * Automatically unreflects and packages {@link Method} into the passed {@link LambdaType}.
     *
     * @param clazz  lambda class for packaging
     * @param method method for packaging
     * @param bind   instance of the object to which the packaged method will be bound
     *               (if null, the method will be considered static)
     * @param <T>    type of packing lambda
     * @return the object instantiating the passed lambda
     * @throws RuntimeException if any errors occurred during the packaging process
     */
    <T> T packLambdaMethod(LambdaType<T> clazz, Method method, Object bind);

    /**
     * Automatically unreflects and packages {@link Constructor} into the passed {@link LambdaType}.
     *
     * @param clazz       lambda class for packaging
     * @param constructor constructor for packaging
     * @param <T>         type of packing lambda
     * @return the object instantiating the passed lambda
     * @throws RuntimeException if any errors occurred during the packaging process
     */
    <T> T packLambdaConstructor(LambdaType<T> clazz, Constructor<?> constructor);

    /**
     * Automatically unreflects and packages static {@link Method} into the passed {@link LambdaType}.
     *
     * @param clazz  lambda class for packaging
     * @param method method for packaging
     * @param <T>    type of packing lambda
     * @return the object instantiating the passed lambda
     */
    <T> T packLambdaMethod(LambdaType<T> clazz, Method method);
}
