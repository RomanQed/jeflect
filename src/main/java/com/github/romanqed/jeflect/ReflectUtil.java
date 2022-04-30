package com.github.romanqed.jeflect;

import com.github.romanqed.jeflect.meta.LambdaClass;
import com.github.romanqed.jeflect.meta.MetaFactory;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A set of utilities for interacting with reflection.
 * Contains static instances of the {@link MetaFactory} and {@link LambdaFactory}
 */
public final class ReflectUtil {
    @SuppressWarnings("rawtypes")
    public static final LambdaClass<Consumer> CONSUMER = LambdaClass.fromClass(Consumer.class);
    @SuppressWarnings("rawtypes")
    public static final LambdaClass<Function> FUNCTION = LambdaClass.fromClass(Function.class);
    @SuppressWarnings("rawtypes")
    public static final LambdaClass<Callable> CALLABLE = LambdaClass.fromClass(Callable.class);
    private static final LambdaFactory LAMBDA_FACTORY = new LambdaFactory();
    private static final MethodFactory LAMBDA_METHOD_FACTORY = new MethodFactory();
    private static final MetaFactory META_FACTORY = new MetaFactory(MethodHandles.lookup());

    /**
     * Extracts the value from the annotation by name using basic reflection tools.
     *
     * @param annotation annotation object to be extracted from
     * @param value      name of the extracted value
     * @param type       class of extracted value
     * @param <T>        type of extracted value
     * @return extracted value, or null in case of an error
     * @throws NoSuchMethodException if a value with the specified name was not found
     */
    public static <T> T extractAnnotationValue(Annotation annotation, String value, Class<T> type) throws
            NoSuchMethodException {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        Method found = annotationType.getDeclaredMethod(value);
        try {
            return type.cast(found.invoke(annotation));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extracts the value from the annotation by name "value" using basic reflection tools.
     *
     * @param annotation annotation object to be extracted from
     * @param type       class of extracted value
     * @param <T>        type of extracted value
     * @return extracted value, or null in case of an error
     * @throws NoSuchMethodException if a value with the specified name was not found
     */
    public static <T> T extractAnnotationValue(Annotation annotation, Class<T> type) throws NoSuchMethodException {
        return extractAnnotationValue(annotation, "value", type);
    }

    /**
     * Packages the passed virtual method into a {@link Lambda}.
     *
     * @param method method for packaging
     * @param bind   instance of the object to which the packaged method will be bound
     * @return the object instantiating the {@link Lambda}
     * @throws InvocationTargetException if an error occurred inside the proxy constructor
     * @throws InstantiationException    if the proxy could not be created
     * @throws IllegalAccessException    if the proxy could not be accessed
     */
    public static Lambda packMethod(Method method, Object bind) throws
            InvocationTargetException, InstantiationException, IllegalAccessException {
        return LAMBDA_FACTORY.packMethod(method, bind);
    }

    /**
     * Packages the passed static method into a {@link Lambda}.
     *
     * @param method method for packaging
     * @return the object instantiating the {@link Lambda}
     * @throws InvocationTargetException if an error occurred inside the proxy constructor
     * @throws InstantiationException    if the proxy could not be created
     * @throws IllegalAccessException    if the proxy could not be accessed
     */
    public static Lambda packMethod(Method method) throws
            InvocationTargetException, InstantiationException, IllegalAccessException {
        return LAMBDA_FACTORY.packMethod(method);
    }

    /**
     * Packages the passed method into a {@link LambdaMethod}.
     *
     * @param method method for packaging
     * @return the object instantiating the {@link LambdaMethod}
     * @throws InvocationTargetException if an error occurred inside the proxy constructor
     * @throws InstantiationException    if the proxy could not be created
     * @throws IllegalAccessException    if the proxy could not be accessed
     */
    public static LambdaMethod packLambdaMethod(Method method) throws
            InvocationTargetException, InstantiationException, IllegalAccessException {
        return LAMBDA_METHOD_FACTORY.packMethod(method);
    }

    @SuppressWarnings("unchecked")
    public static <R> Callable<R> packConstructor(Class<R> clazz) throws Throwable {
        Constructor<R> toPack = clazz.getDeclaredConstructor();
        MethodHandle handle = META_FACTORY.getLookup().unreflectConstructor(toPack);
        return META_FACTORY.packLambdaHandle(CALLABLE, handle, null);
    }

    /**
     * Packages {@link MethodHandle} into the passed {@link LambdaClass}.
     *
     * @param clazz  lambda class for packaging
     * @param handle handle for packaging
     * @param bind   instance of the object to which the packaged method will be bound
     *               (if null, the method will be considered static)
     * @param <T>    type of packing lambda
     * @return the object instantiating the passed lambda
     * @throws Throwable if any errors occurred during the packaging process
     */
    public static <T> T packLambdaHandle(LambdaClass<T> clazz, MethodHandle handle, Object bind) throws Throwable {
        return META_FACTORY.packLambdaHandle(clazz, handle, bind);
    }

    /**
     * Automatically unreflects and packages {@link Method} into the passed {@link LambdaClass}.
     *
     * @param clazz  lambda class for packaging
     * @param method method for packaging
     * @param bind   instance of the object to which the packaged method will be bound
     *               (if null, the method will be considered static)
     * @param <T>    type of packing lambda
     * @return the object instantiating the passed lambda
     * @throws Throwable if any errors occurred during the packaging process
     */
    public static <T> T packLambdaMethod(LambdaClass<T> clazz, Method method, Object bind) throws Throwable {
        return META_FACTORY.packLambdaMethod(clazz, method, bind);
    }

    /**
     * Automatically unreflects and packages {@link Constructor} into the passed {@link LambdaClass}.
     *
     * @param clazz       lambda class for packaging
     * @param constructor constructor for packaging
     * @param <T>         type of packing lambda
     * @return the object instantiating the passed lambda
     * @throws Throwable if any errors occurred during the packaging process
     */
    public static <T> T packLambdaConstructor(LambdaClass<T> clazz, Constructor<?> constructor) throws Throwable {
        return META_FACTORY.packLambdaConstructor(clazz, constructor);
    }

    /**
     * Automatically unreflects and packages static {@link Method} into the passed {@link LambdaClass}.
     *
     * @param clazz  lambda class for packaging
     * @param method method for packaging
     * @param <T>    type of packing lambda
     * @return the object instantiating the passed lambda
     * @throws Throwable if any errors occurred during the packaging process
     */
    public static <T> T packLambdaMethod(LambdaClass<T> clazz, Method method) throws Throwable {
        return META_FACTORY.packLambdaMethod(clazz, method);
    }

    @SuppressWarnings("unchecked")
    public static <T> Consumer<T> packConsumer(Method method, Object bind) throws Throwable {
        return META_FACTORY.packLambdaMethod(CONSUMER, method, bind);
    }

    @SuppressWarnings("unchecked")
    public static <T> Consumer<T> packConsumer(Method method) throws Throwable {
        return META_FACTORY.packLambdaMethod(CONSUMER, method, null);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Function<T, R> packFunction(Method method, Object bind) throws Throwable {
        return META_FACTORY.packLambdaMethod(FUNCTION, method, bind);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Function<T, R> packFunction(Method method) throws Throwable {
        return META_FACTORY.packLambdaMethod(FUNCTION, method, null);
    }

    @SuppressWarnings("unchecked")
    public static <R> Callable<R> packCallable(Method method, Object bind) throws Throwable {
        return META_FACTORY.packLambdaMethod(CALLABLE, method, bind);
    }

    @SuppressWarnings("unchecked")
    public static <R> Callable<R> packCallable(Method method) throws Throwable {
        return META_FACTORY.packLambdaMethod(CALLABLE, method, null);
    }
}
