package com.github.romanqed.jeflect;

import com.github.romanqed.jeflect.lambdas.LambdaClass;
import com.github.romanqed.jeflect.lambdas.MetaFactory;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class ReflectUtil {
    @SuppressWarnings("rawtypes")
    public static final LambdaClass<Consumer> CONSUMER = LambdaClass.fromClass(Consumer.class);
    @SuppressWarnings("rawtypes")
    public static final LambdaClass<Function> FUNCTION = LambdaClass.fromClass(Function.class);
    @SuppressWarnings("rawtypes")
    public static final LambdaClass<Callable> CALLABLE = LambdaClass.fromClass(Callable.class);
    private static final LambdaFactory LAMBDA_FACTORY = new LambdaFactory();
    private static final MetaFactory META_FACTORY = new MetaFactory(MethodHandles.lookup());

    public static <T> T extractAnnotationValue(Annotation annotation, String value, Class<T> type) throws
            InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Objects.requireNonNull(annotation);
        Objects.requireNonNull(value);
        Objects.requireNonNull(type);
        Class<? extends Annotation> annotationType = annotation.annotationType();
        Method found = annotationType.getDeclaredMethod(value);
        if (found.getReturnType() != type) {
            throw new NoSuchMethodException();
        }
        return type.cast(found.invoke(annotation));
    }

    public static <T> T extractAnnotationValue(Annotation annotation, Class<T> type) throws
            InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return extractAnnotationValue(annotation, "value", type);
    }

    public static <T> Lambda packMethod(Method method, T bind) throws
            InvocationTargetException, InstantiationException, IllegalAccessException {
        return LAMBDA_FACTORY.packMethod(method, bind);
    }

    public static Lambda packMethod(Method method) throws
            InvocationTargetException, InstantiationException, IllegalAccessException {
        return LAMBDA_FACTORY.packMethod(method);
    }

    @SuppressWarnings("unchecked")
    public static <R> Callable<R> packConstructor(Class<R> clazz) throws Throwable {
        Objects.requireNonNull(clazz);
        Constructor<R> toPack = clazz.getDeclaredConstructor();
        MethodHandle handle = META_FACTORY.getLookup().unreflectConstructor(toPack);
        return META_FACTORY.packLambdaHandle(CALLABLE, handle, null);
    }

    public <T> T packLambdaHandle(LambdaClass<T> clazz, MethodHandle handle, Object bind) throws Throwable {
        return META_FACTORY.packLambdaHandle(clazz, handle, bind);
    }

    public <T> T packLambdaMethod(LambdaClass<T> clazz, Method method, Object bind) throws Throwable {
        return META_FACTORY.packLambdaMethod(clazz, method, bind);
    }

    public <T> T packLambdaConstructor(LambdaClass<T> clazz, Constructor<?> constructor) throws Throwable {
        return META_FACTORY.packLambdaConstructor(clazz, constructor);
    }

    public <T> T packLambdaMethod(LambdaClass<T> clazz, Method method) throws Throwable {
        return META_FACTORY.packLambdaMethod(clazz, method);
    }

    @SuppressWarnings("unchecked")
    public <T> Consumer<T> packConsumer(Method method, Object bind) throws Throwable {
        return META_FACTORY.packLambdaMethod(CONSUMER, method, bind);
    }

    @SuppressWarnings("unchecked")
    public <T> Consumer<T> packConsumer(Method method) throws Throwable {
        return META_FACTORY.packLambdaMethod(CONSUMER, method, null);
    }

    @SuppressWarnings("unchecked")
    public <T, R> Function<T, R> packFunction(Method method, Object bind) throws Throwable {
        return META_FACTORY.packLambdaMethod(FUNCTION, method, bind);
    }

    @SuppressWarnings("unchecked")
    public <T, R> Function<T, R> packFunction(Method method) throws Throwable {
        return META_FACTORY.packLambdaMethod(FUNCTION, method, null);
    }

    @SuppressWarnings("unchecked")
    public <R> Callable<R> packCallable(Method method, Object bind) throws Throwable {
        return META_FACTORY.packLambdaMethod(CALLABLE, method, bind);
    }

    @SuppressWarnings("unchecked")
    public <R> Callable<R> packCallable(Method method) throws Throwable {
        return META_FACTORY.packLambdaMethod(CALLABLE, method, null);
    }
}
