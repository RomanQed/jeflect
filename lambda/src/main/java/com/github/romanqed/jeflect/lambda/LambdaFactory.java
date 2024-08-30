package com.github.romanqed.jeflect.lambda;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * An interface describing factory that generates the bytecode of a proxy class for methods and constructors.
 */
public interface LambdaFactory {

    /**
     * Creates a proxy implementation of the {@link Lambda} interface for the specified method.
     *
     * @param method the target method
     * @return object of the generated proxy class implementing the {@link Lambda} interface
     */
    Lambda packMethod(Method method);

    /**
     * Creates a proxy implementation of the {@link Lambda} interface for the specified constructor.
     *
     * @param constructor the target constructor
     * @return object of the generated proxy class implementing the {@link Lambda} interface
     */
    Lambda packConstructor(Constructor<?> constructor);
}
