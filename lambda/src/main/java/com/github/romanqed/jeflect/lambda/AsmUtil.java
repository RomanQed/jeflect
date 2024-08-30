package com.github.romanqed.jeflect.lambda;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

final class AsmUtil {
    // String constants
    static final String INIT = "<init>";
    static final String EMPTY_DESCRIPTOR = "()V";
    // Type constants
    static final Type OBJECT = Type.getType(Object.class);
    static final Map<Class<?>, Class<?>> PRIMITIVES = Map.of(
            boolean.class, Boolean.class,
            char.class, Character.class,
            byte.class, Byte.class,
            short.class, Short.class,
            int.class, Integer.class,
            float.class, Float.class,
            long.class, Long.class,
            double.class, Double.class
    );
    static final Map<Class<?>, String> PRIMITIVE_METHODS = Map.of(
            boolean.class, "booleanValue",
            char.class, "charValue",
            byte.class, "byteValue",
            short.class, "shortValue",
            int.class, "intValue",
            float.class, "floatValue",
            long.class, "longValue",
            double.class, "doubleValue"
    );

    private AsmUtil() {
    }

    static void castReference(MethodVisitor visitor, Class<?> clazz) {
        if (clazz == Object.class) {
            return;
        }
        if (!clazz.isPrimitive()) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(clazz));
            return;
        }
        var wrap = Type.getInternalName(PRIMITIVES.get(clazz));
        visitor.visitTypeInsn(Opcodes.CHECKCAST, wrap);
        var method = PRIMITIVE_METHODS.get(clazz);
        visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                wrap,
                method,
                "()" + Type.getDescriptor(clazz),
                false
        );
    }

    static void packPrimitive(MethodVisitor visitor, Class<?> primitive) {
        if (!primitive.isPrimitive()) {
            return;
        }
        var wrap = Type.getType(PRIMITIVES.get(primitive));
        var descriptor = Type.getMethodDescriptor(wrap, Type.getType(primitive));
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                wrap.getInternalName(),
                "valueOf",
                descriptor,
                false);
    }

    static void createEmptyConstructor(ClassWriter writer) {
        var init = writer.visitMethod(Opcodes.ACC_PUBLIC,
                INIT,
                EMPTY_DESCRIPTOR,
                null,
                null);
        init.visitCode();
        init.visitVarInsn(Opcodes.ALOAD, 0);
        init.visitMethodInsn(Opcodes.INVOKESPECIAL, OBJECT.getInternalName(), INIT, EMPTY_DESCRIPTOR, false);
        init.visitInsn(Opcodes.RETURN);
        init.visitMaxs(1, 1);
        init.visitEnd();
    }

    static void invoke(MethodVisitor visitor, Method method) {
        var owner = method.getDeclaringClass();
        var isInterface = owner.isInterface();
        var opcode = Modifier.isStatic(method.getModifiers()) ?
                Opcodes.INVOKESTATIC
                : (isInterface ?
                Opcodes.INVOKEINTERFACE
                : Opcodes.INVOKEVIRTUAL);
        visitor.visitMethodInsn(
                opcode,
                Type.getInternalName(owner),
                method.getName(),
                Type.getMethodDescriptor(method),
                isInterface
        );
    }
}
