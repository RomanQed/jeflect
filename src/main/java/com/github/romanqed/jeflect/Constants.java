package com.github.romanqed.jeflect;

import org.objectweb.asm.Opcodes;

class Constants {
    static final String INIT = "<init>";
    static final String METHOD = "call";
    static final String METHOD_DESCRIPTOR = "([Ljava/lang/Object;)Ljava/lang/Object;";
    static final String EMPTY_DESCRIPTOR = "()V";
    static final String FIELD_NAME = "body";
    static final String OBJECT = "java/lang/Object";
    static final String LAMBDA = "com/github/romanqed/jeflect/Lambda";
    static final String[] INTERFACES = {LAMBDA};
    static final int FIELD_ACCESS = Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL;
    static final String PROXY = "Proxy";
    static final Class<?> VOID = void.class;
}
