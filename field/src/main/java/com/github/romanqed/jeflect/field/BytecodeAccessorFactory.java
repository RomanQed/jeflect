package com.github.romanqed.jeflect.field;

import com.github.romanqed.jeflect.loader.DefineClassLoader;
import com.github.romanqed.jeflect.loader.DefineLoader;
import com.github.romanqed.jeflect.loader.DefineObjectFactory;
import com.github.romanqed.jeflect.loader.ObjectFactory;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * <p>A class representing a factory that creates
 * {@link FieldAccessor} instances for subsequent access to the field.</p>
 * <p>Access occurs at almost native speed, minus the time to call the proxy class method.</p>
 */
public final class BytecodeAccessorFactory implements FieldAccessorFactory {
    private static final String ACCESSOR = "Accessor";
    private final ObjectFactory<FieldAccessor> factory;

    public BytecodeAccessorFactory(ObjectFactory<FieldAccessor> factory) {
        this.factory = Objects.requireNonNull(factory);
    }

    public BytecodeAccessorFactory(DefineLoader loader) {
        this(new DefineObjectFactory<>(loader));
    }

    public BytecodeAccessorFactory() {
        this(new DefineClassLoader());
    }

    public FieldAccessor packField(Field field) {
        var toHash = field.getDeclaringClass().getName() + field.getName();
        var name = ACCESSOR + toHash.hashCode();
        return factory.create(name, () -> FieldUtil.createAccessor(name, field));
    }
}
