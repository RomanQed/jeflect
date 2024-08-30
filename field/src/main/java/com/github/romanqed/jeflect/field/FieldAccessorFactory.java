package com.github.romanqed.jeflect.field;

import java.lang.reflect.Field;

/**
 * <p>An interface describing a factory that creates
 * {@link FieldAccessor} instances for subsequent access to the field.</p>
 */
public interface FieldAccessorFactory {

    /**
     * Creates a proxy implementation of the {@link FieldAccessor} interface for the specified field.
     *
     * @param field the target field
     * @return object of the generated proxy class implementing the {@link FieldAccessor} interface
     */
    FieldAccessor packField(Field field);
}
