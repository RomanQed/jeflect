package com.github.romanqed.jeflect;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.Map;
import java.util.function.Consumer;

/**
 * A utility class containing some methods for the ASM library.
 */
public final class AsmUtil {
    // String constants
    public static final String INIT = "<init>";
    public static final String EMPTY_DESCRIPTOR = "()V";
    // Type constants
    public static final Type OBJECT = Type.getType(Object.class);
    public static final Map<Type, Type> PRIMITIVES = getPrimitives();
    public static final Map<Type, String> PRIMITIVE_METHODS = getPrimitiveMethods();

    private static Map<Type, Type> getPrimitives() {
        return Map.of(
                Type.BOOLEAN_TYPE, Type.getType(Boolean.class),
                Type.CHAR_TYPE, Type.getType(Character.class),
                Type.BYTE_TYPE, Type.getType(Byte.class),
                Type.SHORT_TYPE, Type.getType(Short.class),
                Type.INT_TYPE, Type.getType(Integer.class),
                Type.FLOAT_TYPE, Type.getType(Float.class),
                Type.LONG_TYPE, Type.getType(Long.class),
                Type.DOUBLE_TYPE, Type.getType(Double.class)
        );
    }

    private static Map<Type, String> getPrimitiveMethods() {
        return Map.of(
                Type.BOOLEAN_TYPE, "booleanValue",
                Type.CHAR_TYPE, "charValue",
                Type.BYTE_TYPE, "byteValue",
                Type.SHORT_TYPE, "shortValue",
                Type.INT_TYPE, "intValue",
                Type.FLOAT_TYPE, "floatValue",
                Type.LONG_TYPE, "longValue",
                Type.DOUBLE_TYPE, "doubleValue"
        );
    }

    /**
     * Casts the last value on the stack to the specified type.
     *
     * @param visitor the visitor containing the method code
     * @param type    the type to which the value will be cast
     */
    public static void castReference(MethodVisitor visitor, Type type) {
        if (type.equals(OBJECT)) {
            return;
        }
        var name = type.getInternalName();
        if (name.startsWith("[")) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, name);
            visitor.visitTypeInsn(Opcodes.CHECKCAST, name);
            return;
        }
        var wrap = PRIMITIVES.get(type);
        if (wrap != null) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, wrap.getInternalName());
            var method = PRIMITIVE_METHODS.get(type);
            visitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    wrap.getInternalName(),
                    method,
                    "()" + name,
                    false
            );
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
        var wrap = PRIMITIVES.get(primitive);
        if (wrap == null) {
            return;
        }
        var descriptor = Type.getMethodDescriptor(wrap, primitive);
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                wrap.getInternalName(),
                "valueOf",
                descriptor,
                false);
    }

    /**
     * Creates a new object of the specified type on the stack
     *
     * @param visitor  visitor containing the incomplete code of the method
     * @param typeName name of the required type
     */
    public static void newObject(MethodVisitor visitor, String typeName) {
        visitor.visitTypeInsn(Opcodes.NEW, typeName);
        visitor.visitInsn(Opcodes.DUP);
        visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, typeName, INIT, EMPTY_DESCRIPTOR, false);
    }

    /**
     * Creates a synchronized block on the stack
     *
     * @param visitor visitor containing the incomplete code of the method
     * @param body    The code generator to be executed inside the synchronized block
     */
    public static void synchronizedCall(LocalVariablesSorter visitor, Consumer<LocalVariablesSorter> body) {
        // Body labels
        var startLabel = new Label();
        var endLabel = new Label();
        // Handle labels
        var handleLabel = new Label();
        var throwLabel = new Label();
        // try-catches
        visitor.visitTryCatchBlock(startLabel, endLabel, handleLabel, null);
        visitor.visitTryCatchBlock(handleLabel, throwLabel, handleLabel, null);
        // Save lock to variable
        var varIndex = visitor.newLocal(OBJECT);
        visitor.visitInsn(Opcodes.DUP);
        visitor.visitVarInsn(Opcodes.ASTORE, varIndex);
        // Enter
        visitor.visitInsn(Opcodes.MONITORENTER);
        visitor.visitLabel(startLabel);
        // Generate body
        body.accept(visitor);
        visitor.visitVarInsn(Opcodes.ALOAD, varIndex);
        visitor.visitInsn(Opcodes.MONITOREXIT);
        visitor.visitLabel(endLabel);
        var gotoLabel = new Label();
        visitor.visitJumpInsn(Opcodes.GOTO, gotoLabel);
        visitor.visitLabel(handleLabel);
        visitor.visitVarInsn(Opcodes.ASTORE, varIndex + 1);
        visitor.visitVarInsn(Opcodes.ALOAD, varIndex);
        visitor.visitInsn(Opcodes.MONITOREXIT);
        visitor.visitLabel(throwLabel);
        visitor.visitVarInsn(Opcodes.ALOAD, varIndex + 1);
        visitor.visitInsn(Opcodes.ATHROW);
        visitor.visitLabel(gotoLabel);
    }

    /**
     * Creates an empty constructor
     *
     * @param writer     {@link ClassWriter} containing the class in which the constructor will be created
     * @param superClass class parent
     */
    public static void createEmptyConstructor(ClassWriter writer, String superClass) {
        var init = writer.visitMethod(Opcodes.ACC_PUBLIC,
                INIT,
                EMPTY_DESCRIPTOR,
                null,
                null);
        init.visitCode();
        init.visitVarInsn(Opcodes.ALOAD, 0);
        init.visitMethodInsn(Opcodes.INVOKESPECIAL, superClass, INIT, EMPTY_DESCRIPTOR, false);
        init.visitInsn(Opcodes.RETURN);
        init.visitMaxs(1, 1);
        init.visitEnd();
    }

    /**
     * Creates an empty constructor
     *
     * @param writer {@link ClassWriter} containing the class in which the constructor will be created
     */
    public static void createEmptyConstructor(ClassWriter writer) {
        createEmptyConstructor(writer, OBJECT.getInternalName());
    }

    /**
     * Puts an int scalar on the stack, using, if possible, the most optimal method.
     *
     * @param visitor visitor containing the incomplete code of the method
     * @param value   int value
     */
    public static void pushInt(MethodVisitor visitor, int value) {
        if (value >= -1 && value <= 5) {
            visitor.visitInsn(Opcodes.ICONST_M1 + value + 1);
            return;
        }
        if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            visitor.visitIntInsn(Opcodes.BIPUSH, value);
            return;
        }
        if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            visitor.visitIntInsn(Opcodes.SIPUSH, value);
            return;
        }
        visitor.visitLdcInsn(value);
    }

    /**
     * Puts a long scalar on the stack, using, if possible, the most optimal method.
     *
     * @param visitor visitor containing the incomplete code of the method
     * @param value   long value
     */
    public static void pushLong(MethodVisitor visitor, long value) {
        if (value >= 0 && value <= 1) {
            visitor.visitInsn(Opcodes.LCONST_0 + (int) value);
            return;
        }
        visitor.visitLdcInsn(value);
    }

    /**
     * Puts a float scalar on the stack, using, if possible, the most optimal method.
     *
     * @param visitor visitor containing the incomplete code of the method
     * @param value   float value
     */
    public static void pushFloat(MethodVisitor visitor, float value) {
        if (Float.compare(value, 0) == 0) {
            visitor.visitInsn(Opcodes.FCONST_0);
            return;
        }
        if (Float.compare(value, 1) == 0) {
            visitor.visitInsn(Opcodes.FCONST_1);
            return;
        }
        if (Float.compare(value, 2) == 0) {
            visitor.visitInsn(Opcodes.FCONST_2);
            return;
        }
        visitor.visitLdcInsn(value);
    }

    /**
     * Puts a double scalar on the stack, using, if possible, the most optimal method.
     *
     * @param visitor visitor containing the incomplete code of the method
     * @param value   double value
     */
    public static void pushDouble(MethodVisitor visitor, double value) {
        if (Double.compare(value, 0) == 0) {
            visitor.visitInsn(Opcodes.DCONST_0);
            return;
        }
        if (Double.compare(value, 1) == 0) {
            visitor.visitInsn(Opcodes.DCONST_1);
            return;
        }
        visitor.visitLdcInsn(value);
    }

    /**
     * Puts a constant on the stack, using the most optimal method if possible.
     *
     * @param visitor visitor containing the incomplete code of the method
     * @param value   constant value
     */
    public static void pushConstant(MethodVisitor visitor, Object value) {
        if (value == null) {
            visitor.visitInsn(Opcodes.ACONST_NULL);
            return;
        }
        var type = value.getClass();
        if (type == Character.class) {
            pushInt(visitor, (Character) value);
            return;
        }
        if (type == Integer.class) {
            pushInt(visitor, (Integer) value);
            return;
        }
        if (type == Long.class) {
            pushLong(visitor, (Long) value);
            return;
        }
        if (type == Float.class) {
            pushFloat(visitor, (Float) value);
            return;
        }
        if (type == Double.class) {
            pushDouble(visitor, (Double) value);
            return;
        }
        visitor.visitLdcInsn(value);
    }
}
