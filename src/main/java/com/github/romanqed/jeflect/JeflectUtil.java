package com.github.romanqed.jeflect;

import com.github.romanqed.jeflect.lambdas.LambdaFactory;
import com.github.romanqed.jeflect.meta.LambdaType;
import com.github.romanqed.jeflect.meta.MetaFactory;
import com.github.romanqed.jfunc.Exceptions;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A set of utilities for interacting with reflection.
 * Contains static instances of the {@link MetaFactory} and {@link LambdaFactory}.
 */
public final class JeflectUtil {
    @SuppressWarnings("rawtypes")
    public static final LambdaType<Consumer> CONSUMER = LambdaType.fromClass(Consumer.class);
    @SuppressWarnings("rawtypes")
    public static final LambdaType<Function> FUNCTION = LambdaType.fromClass(Function.class);
    @SuppressWarnings("rawtypes")
    public static final LambdaType<Callable> CALLABLE = LambdaType.fromClass(Callable.class);
    public static final LambdaType<Runnable> RUNNABLE = LambdaType.fromClass(Runnable.class);

    private static final MetaFactory META_FACTORY = new MetaFactory(MethodHandles.lookup());

    /**
     * Extracts the value from the annotation by name using basic reflection tools.
     *
     * @param annotation annotation object to be extracted from
     * @param value      name of the extracted value
     * @param <T>        type of extracted value
     * @return extracted value, or null in case of an error
     * @throws RuntimeException if a value with the specified name was not found
     */
    @SuppressWarnings("unchecked")
    public static <T> T extractAnnotationValue(Annotation annotation, String value) {
        var annotationType = annotation.annotationType();
        var found = Exceptions.suppress(() -> annotationType.getDeclaredMethod(value));
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
     */
    public static <T> T extractAnnotationValue(Annotation annotation) {
        return extractAnnotationValue(annotation, "value");
    }

    public static Runnable packRunnable(Method method, Object bind) {
        return META_FACTORY.packLambdaMethod(RUNNABLE, method, bind);
    }

    public static Runnable packRunnable(Method method) {
        return META_FACTORY.packLambdaMethod(RUNNABLE, method, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> Consumer<T> packConsumer(Method method, Object bind) {
        return META_FACTORY.packLambdaMethod(CONSUMER, method, bind);
    }

    @SuppressWarnings("unchecked")
    public static <T> Consumer<T> packConsumer(Method method) {
        return META_FACTORY.packLambdaMethod(CONSUMER, method, null);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Function<T, R> packFunction(Method method, Object bind) {
        return META_FACTORY.packLambdaMethod(FUNCTION, method, bind);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Function<T, R> packFunction(Method method) {
        return META_FACTORY.packLambdaMethod(FUNCTION, method, null);
    }

    @SuppressWarnings("unchecked")
    public static <R> Callable<R> packCallable(Method method, Object bind) {
        return META_FACTORY.packLambdaMethod(CALLABLE, method, bind);
    }

    @SuppressWarnings("unchecked")
    public static <R> Callable<R> packCallable(Method method) {
        return META_FACTORY.packLambdaMethod(CALLABLE, method, null);
    }
}
