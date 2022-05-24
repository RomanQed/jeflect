package com.github.romanqed.jeflect.lambdas;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Constants {
    public static final String INIT = "<init>";
    public static final String METHOD = "call";
    public static final String DESCRIPTOR = "(%s)%s";
    public static final String EMPTY_DESCRIPTOR = "()V";
    public static final String FIELD_NAME = "body";
    public static final String OBJECT = "java/lang/Object";
    public static final String LAMBDA = Type.getType(Lambda.class).getInternalName();
    public static final int FIELD_ACCESS = Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL;
    public static final String VOID = "V";
    public static final Map<String, String> PRIMITIVES = getPrimitives();
    public static final Map<String, String> PRIMITIVE_METHODS = getPrimitiveMethods();
    public static final Object[] EMPTY_ARGUMENTS = new Object[0];
    public static final String THROWABLE = "java/lang/Throwable";

    private static Map<String, String> getPrimitives() {
        Map<String, String> ret = new HashMap<>();
        ret.put("Z", "java/lang/Boolean");
        ret.put("C", "java/lang/Character");
        ret.put("B", "java/lang/Byte");
        ret.put("S", "java/lang/Short");
        ret.put("I", "java/lang/Integer");
        ret.put("F", "java/lang/Float");
        ret.put("J", "java/lang/Long");
        ret.put("D", "java/lang/Double");
        return Collections.unmodifiableMap(ret);
    }

    private static Map<String, String> getPrimitiveMethods() {
        Map<String, String> ret = new HashMap<>();
        ret.put("Z", "booleanValue");
        ret.put("C", "charValue");
        ret.put("B", "byteValue");
        ret.put("S", "shortValue");
        ret.put("I", "intValue");
        ret.put("F", "floatValue");
        ret.put("J", "longValue");
        ret.put("D", "doubleValue");
        return Collections.unmodifiableMap(ret);
    }

    public static String formatDescriptor(String ret, String arg) {
        return String.format(DESCRIPTOR, arg, ret);
    }
}
