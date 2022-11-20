package com.github.romanqed.jeflect;

import java.lang.annotation.Annotation;
import java.util.List;

public interface ByteAnnotated {
    ByteAnnotation getAnnotation(Class<? extends Annotation> annotationClass);

    List<ByteAnnotation> getAnnotations();

    boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);
}
