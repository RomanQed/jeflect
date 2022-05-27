package com.github.romanqed.jeflect.lambdas;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static com.github.romanqed.jeflect.lambdas.AsmUtil.*;

final class Constants {
    static final String FIELD_NAME = "body";
    static final String LAMBDA = Type.getType(Lambda.class).getInternalName();
    static final int FIELD_ACCESS = Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL;
    static final int INT_0 = Opcodes.ICONST_0;
    static final String METHOD = "call";
    static final String[] EXCEPTIONS = new String[]{THROWABLE};
    static final String BOUND_DESCRIPTOR = formatDescriptor("L" + OBJECT + ";", "[L" + OBJECT + ";");
    static final String FREE_DESCRIPTOR = getFreeDescriptor();

    private static String getFreeDescriptor() {
        String object = "L" + OBJECT + ";";
        return formatDescriptor(object, object + "[" + object);
    }
}
