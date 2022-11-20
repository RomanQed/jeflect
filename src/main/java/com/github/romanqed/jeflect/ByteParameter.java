package com.github.romanqed.jeflect;

import org.objectweb.asm.Type;

public abstract class ByteParameter extends AbstractAnnotated {
    private final String descriptor;
    private final LazyType type;
    private final int modifiers;

    public ByteParameter(String descriptor, int modifiers) {
        this.descriptor = descriptor;
        this.type = new LazyType(Type.getType(descriptor).getClassName());
        this.modifiers = modifiers;
    }

    public Class<?> getType() {
        return type.getType();
    }

    public String getTypeName() {
        return type.getTypeName();
    }

    public int getModifiers() {
        return modifiers;
    }

    public String getDescriptor() {
        return descriptor;
    }
}
