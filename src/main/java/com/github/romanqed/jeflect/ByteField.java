package com.github.romanqed.jeflect;

import org.objectweb.asm.Type;

public abstract class ByteField extends AbstractMember {
    private final String descriptor;
    private final LazyType type;
    private final Object value;

    protected ByteField(ByteClass parent, String descriptor, Object value, String name, int modifiers) {
        super(parent, name, modifiers);
        this.descriptor = descriptor;
        this.type = new LazyType(Type.getType(descriptor).getClassName());
        this.value = value;
    }

    public String getTypeName() {
        return type.getTypeName();
    }

    public Class<?> getType() {
        return type.getType();
    }

    public String getDescriptor() {
        return descriptor;
    }

    public Object getValue() {
        return value;
    }
}
