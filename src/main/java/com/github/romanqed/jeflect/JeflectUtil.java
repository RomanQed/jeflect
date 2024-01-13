package com.github.romanqed.jeflect;

import com.github.romanqed.jfunc.Exceptions;

import java.lang.annotation.Annotation;

/**
 * A set of utilities for interacting with reflection.
 */
public final class JeflectUtil {
    private JeflectUtil() {
    }

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
}
