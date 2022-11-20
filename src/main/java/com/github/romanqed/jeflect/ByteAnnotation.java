package com.github.romanqed.jeflect;

import java.util.Map;

public final class ByteAnnotation {
    private final LazyType type;
    private final Map<String, Object> fields;

    public ByteAnnotation(String className, Map<String, Object> fields) {
        this.type = new LazyType(className);
        this.fields = fields;
    }

    public String getAnnotationClassName() {
        return type.getTypeName();
    }

    public Class<?> getAnnotationClass() {
        return type.getType();
    }

    @SuppressWarnings("unchecked")
    public <T> T getField(String name) {
        return (T) fields.get(name);
    }
}
