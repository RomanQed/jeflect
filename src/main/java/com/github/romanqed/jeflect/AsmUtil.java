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
}
