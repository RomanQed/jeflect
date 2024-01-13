package com.github.romanqed.jeflect;

import com.github.romanqed.jeflect.meta.LambdaType;
import com.github.romanqed.jeflect.meta.MetaFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public final class MetaUtil {
    @SuppressWarnings("rawtypes")
    public static final LambdaType<Consumer> CONSUMER = LambdaType.fromClass(Consumer.class);
    @SuppressWarnings("rawtypes")
    public static final LambdaType<Function> FUNCTION = LambdaType.fromClass(Function.class);
    @SuppressWarnings("rawtypes")
    public static final LambdaType<Callable> CALLABLE = LambdaType.fromClass(Callable.class);
    public static final LambdaType<Runnable> RUNNABLE = LambdaType.fromClass(Runnable.class);
    private static final MetaFactory META_FACTORY = new MetaFactory(MethodHandles.lookup());

    private MetaUtil() {
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
