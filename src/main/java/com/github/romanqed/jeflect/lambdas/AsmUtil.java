package com.github.romanqed.jeflect.lambdas;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class containing some methods for the ASM library.
 */
public final class AsmUtil {
    public static final String INIT = "<init>";
    public static final String DESCRIPTOR = "(%s)%s";
    public static final String EMPTY_DESCRIPTOR = "()V";
    public static final String OBJECT = "java/lang/Object";
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

    /**
     * Casts the last value on the stack to the specified type.
     *
     * @param visitor the visitor containing the method code
     * @param type    the type to which the value will be cast
     */
    public static void castReference(MethodVisitor visitor, Type type) {
        String name = type.getInternalName();
        if (name.startsWith("[")) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, name);
            visitor.visitTypeInsn(Opcodes.CHECKCAST, name);
            return;
        }
        String wrap = PRIMITIVES.get(name);
        if (wrap != null) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, wrap);
            String method = PRIMITIVE_METHODS.get(name);
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, wrap, method, "()" + name, false);
            return;
        }
        visitor.visitTypeInsn(Opcodes.CHECKCAST, name);
    }

    /**
     * Packs the last primitive on the stack.
     *
     * @param visitor   the visitor containing the method code
     * @param primitive the type of primitive to be packed
     */
    public static void packPrimitive(MethodVisitor visitor, Type primitive) {
        String name = primitive.getInternalName();
        String wrap = PRIMITIVES.get(name);
        if (wrap == null) {
            return;
        }
        String descriptor = formatDescriptor("L" + wrap + ";", name);
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, wrap, "valueOf", descriptor, false);
    }
}
