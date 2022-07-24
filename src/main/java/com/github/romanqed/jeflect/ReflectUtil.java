package com.github.romanqed.jeflect;

import com.github.romanqed.jeflect.lambdas.Lambda;
import com.github.romanqed.jeflect.lambdas.LambdaFactory;
import com.github.romanqed.jeflect.meta.LambdaType;
import com.github.romanqed.jeflect.meta.MetaFactory;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A set of utilities for interacting with reflection.
 * Contains static instances of the {@link MetaFactory} and {@link LambdaFactory}
 */
public final class ReflectUtil {
    @SuppressWarnings("rawtypes")
    public static final LambdaType<Consumer> CONSUMER = LambdaType.fromClass(Consumer.class);
    @SuppressWarnings("rawtypes")
    public static final LambdaType<Function> FUNCTION = LambdaType.fromClass(Function.class);
    @SuppressWarnings("rawtypes")
    public static final LambdaType<Callable> CALLABLE = LambdaType.fromClass(Callable.class);
    private static final LambdaFactory LAMBDA_FACTORY = new LambdaFactory();
    private static final MetaFactory META_FACTORY = new MetaFactory(MethodHandles.lookup());

    private static Method findDefineMethod() {
        Class<ClassLoader> clazz = ClassLoader.class;
        try {
            Method method = clazz.getDeclaredMethod("defineClass",
                    String.class,
                    byte[].class,
                    int.class,
                    int.class);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Can't find defineClass method from ClassLoader");
        }
    }

    /**
     * Wraps the classloader into an interface that allows you to define new classes.
     *
     * @param loader the {@link ClassLoader} to wrap
     * @return the resulting object implementing interface {@link DefineLoader}
     */
    public static DefineLoader wrapClassLoader(ClassLoader loader) {
        Objects.requireNonNull(loader);
        return new WrapClassLoader(loader, findDefineMethod());
    }

    /**
     * Extracts the value from the annotation by name using basic reflection tools.
     *
     * @param annotation annotation object to be extracted from
     * @param value      name of the extracted value
     * @param <T>        type of extracted value
     * @return extracted value, or null in case of an error
     * @throws NoSuchMethodException if a value with the specified name was not found
     */
    @SuppressWarnings("unchecked")
    public static <T> T extractAnnotationValue(Annotation annotation, String value) throws
            NoSuchMethodException {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        Method found = annotationType.getDeclaredMethod(value);
        try {
            return (T) found.invoke(annotation);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extracts the value from the annotation by name "value" using basic reflection tools.
     *
     * @param annotation annotation object to be extracted from
     * @param <T>        type of extracted value
     * @return extracted value, or null in case of an error
     * @throws NoSuchMethodException if a value with the specified name was not found
     */
    public static <T> T extractAnnotationValue(Annotation annotation) throws NoSuchMethodException {
        return extractAnnotationValue(annotation, "value");
    }

    /**
     * Packages the passed virtual method into a {@link Lambda}.
     *
     * @param method method for packaging
     * @param bind   instance of the object to which the packaged method will be bound
     * @return the object instantiating the {@link Lambda}
     */
    public static Lambda packMethod(Method method, Object bind) {
        return LAMBDA_FACTORY.packMethod(method, bind);
    }

    /**
     * Packages the passed static method into a {@link Lambda}.
     *
     * @param method method for packaging
     * @return the object instantiating the {@link Lambda}
     */
    public static Lambda packMethod(Method method) {
        return LAMBDA_FACTORY.packMethod(method);
    }

    @SuppressWarnings("unchecked")
    public static <R> Callable<R> packConstructor(Class<R> clazz) throws Throwable {
        Constructor<R> toPack = clazz.getDeclaredConstructor();
        MethodHandle handle = META_FACTORY.getLookup().unreflectConstructor(toPack);
        return META_FACTORY.packLambdaHandle(CALLABLE, handle, null);
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
    public static <T> T packLambdaHandle(LambdaType<T> clazz, MethodHandle handle, Object bind) throws Throwable {
        return META_FACTORY.packLambdaHandle(clazz, handle, bind);
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
    public static <T> T packLambdaMethod(LambdaType<T> clazz, Method method, Object bind) throws Throwable {
        return META_FACTORY.packLambdaMethod(clazz, method, bind);
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
    public static <T> T packLambdaConstructor(LambdaType<T> clazz, Constructor<?> constructor) throws Throwable {
        return META_FACTORY.packLambdaConstructor(clazz, constructor);
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
    public static <T> T packLambdaMethod(LambdaType<T> clazz, Method method) throws Throwable {
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
