package com.github.romanqed.jeflect;

import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ByteMethod extends AbstractMember {
    private final String descriptor;
    private final LazyType returnType;
    private final List<LazyType> exceptionTypes;

    protected ByteMethod(ByteClass parent, String descriptor, String[] exceptions, String name, int modifiers) {
        super(parent, name, modifiers);
        this.descriptor = descriptor;
        this.returnType = new LazyType(Type.getType(descriptor).getReturnType().getClassName());
        if (exceptions == null) {
            this.exceptionTypes = Collections.emptyList();
        } else {
            this.exceptionTypes = Collections.unmodifiableList(Arrays
                    .stream(exceptions)
                    .map(LazyType::new)
                    .collect(Collectors.toList()));
        }
    }

    public String getDescriptor() {
        return descriptor;
    }

    public abstract List<ByteParameter> getParameters();

    public int getParameterCount() {
        return getParameters().size();
    }

    public List<LazyType> getExceptionTypes() {
        return exceptionTypes;
    }

    public LazyType getReturnType() {
        return returnType;
    }
}
