package com.github.romanqed.jeflect;

import org.objectweb.asm.Opcodes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class Constants {
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
    static final String VOID = "V";
    static final Map<String, String> PRIMITIVES;
    static final Map<String, String> PRIMITIVE_METHODS;

    static {
        // Init primitives
        Map<String, String> primitives = new HashMap<>();
        primitives.put("Z", "java/lang/Boolean");
        primitives.put("C", "java/lang/Character");
        primitives.put("B", "java/lang/Byte");
        primitives.put("S", "java/lang/Short");
        primitives.put("I", "java/lang/Integer");
        primitives.put("F", "java/lang/Float");
        primitives.put("J", "java/lang/Long");
        primitives.put("D", "java/lang/Double");
        PRIMITIVES = Collections.unmodifiableMap(primitives);
        // Init primitive methods
        Map<String, String> primitiveMethods = new HashMap<>();
        primitiveMethods.put("Z", "booleanValue");
        primitiveMethods.put("C", "charValue");
        primitiveMethods.put("B", "byteValue");
        primitiveMethods.put("S", "shortValue");
        primitiveMethods.put("I", "intValue");
        primitiveMethods.put("F", "floatValue");
        primitiveMethods.put("J", "longValue");
        primitiveMethods.put("D", "doubleValue");
        PRIMITIVE_METHODS = Collections.unmodifiableMap(primitiveMethods);
    }
}
