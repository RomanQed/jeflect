package com.github.romanqed.jeflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class ReflectUtil {
    private static final LambdaFactory FACTORY = new LambdaFactory();

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

    public static <T> Lambda packMethod(Class<T> owner, Method method, T bind) throws
            InvocationTargetException, InstantiationException, IllegalAccessException {
        return FACTORY.packMethod(owner, method, bind);
    }

    public static Lambda packMethod(Class<?> owner, Method method) throws
            InvocationTargetException, InstantiationException, IllegalAccessException {
        return packMethod(owner, method, null);
    }
}
