package com.github.romanqed.jeflect.lambdas;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static com.github.romanqed.jeflect.AsmUtil.*;

final class Constants {
    static final String FIELD_NAME = "body";
    static final String LAMBDA = Type.getType(Lambda.class).getInternalName();
    static final int INT_0 = Opcodes.ICONST_0;
    static final String METHOD = "call";
    static final String[] EXCEPTIONS = new String[]{THROWABLE.getInternalName()};
    static final String BOUND_DESCRIPTOR = getDescriptor(OBJECT, OBJECT_ARRAY);
    static final String FREE_DESCRIPTOR = getDescriptor(OBJECT, OBJECT, OBJECT_ARRAY);
    static final String VOID = Type.VOID_TYPE.getDescriptor();
}
