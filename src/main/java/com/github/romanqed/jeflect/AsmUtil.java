package com.github.romanqed.jeflect;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A utility class containing some methods for the ASM library.
 */
public final class AsmUtil {
    // String constants
    public static final String INIT = "<init>";
    public static final String DESCRIPTOR = "(%s)%s";
    public static final String EMPTY_DESCRIPTOR = "()V";
    // Type constants
    public static final Type OBJECT = Type.getType(Object.class);
    public static final String INTERNAL_OBJECT_NAME = OBJECT.getInternalName();
    public static final Type OBJECT_ARRAY = Type.getType(Object[].class);
    public static final Type THROWABLE = Type.getType(Throwable.class);
    public static final Map<Type, Type> PRIMITIVES = getPrimitives();
    public static final Map<Type, String> PRIMITIVE_METHODS = getPrimitiveMethods();
    // Mask constants
    public final static int ACCESS_MASK = Opcodes.ACC_PROTECTED << 1;
    // Private constants
    static final int FIELD_ACCESS = Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL;


    private static Map<Type, Type> getPrimitives() {
        Map<Type, Type> ret = new HashMap<>();
        ret.put(Type.BOOLEAN_TYPE, Type.getType(Boolean.class));
        ret.put(Type.CHAR_TYPE, Type.getType(Character.class));
        ret.put(Type.BYTE_TYPE, Type.getType(Byte.class));
        ret.put(Type.SHORT_TYPE, Type.getType(Short.class));
        ret.put(Type.INT_TYPE, Type.getType(Integer.class));
        ret.put(Type.FLOAT_TYPE, Type.getType(Float.class));
        ret.put(Type.LONG_TYPE, Type.getType(Long.class));
        ret.put(Type.DOUBLE_TYPE, Type.getType(Double.class));
        return Collections.unmodifiableMap(ret);
    }

    private static Map<Type, String> getPrimitiveMethods() {
        Map<Type, String> ret = new HashMap<>();
        ret.put(Type.BOOLEAN_TYPE, "booleanValue");
        ret.put(Type.CHAR_TYPE, "charValue");
        ret.put(Type.BYTE_TYPE, "byteValue");
        ret.put(Type.SHORT_TYPE, "shortValue");
        ret.put(Type.INT_TYPE, "intValue");
        ret.put(Type.FLOAT_TYPE, "floatValue");
        ret.put(Type.LONG_TYPE, "longValue");
        ret.put(Type.DOUBLE_TYPE, "doubleValue");
        return Collections.unmodifiableMap(ret);
    }

    public static String getDescriptor(Type ret, Type... arguments) {
        StringBuilder argumentDescriptors = new StringBuilder();
        for (Type type : arguments) {
            argumentDescriptors.append(type.getDescriptor());
        }
        return String.format(DESCRIPTOR, argumentDescriptors, ret.getDescriptor());
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
        Type wrap = PRIMITIVES.get(type);
        if (wrap != null) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, wrap.getInternalName());
            String method = PRIMITIVE_METHODS.get(type);
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, wrap.getInternalName(), method, "()" + name, false);
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
        Type wrap = PRIMITIVES.get(primitive);
        if (wrap == null) {
            return;
        }
        String descriptor = getDescriptor(wrap, primitive);
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, wrap.getInternalName(), "valueOf", descriptor, false);
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
        Label startLabel = new Label();
        Label endLabel = new Label();
        // Handle labels
        Label handleLabel = new Label();
        Label throwLabel = new Label();
        // try-catches
        visitor.visitTryCatchBlock(startLabel, endLabel, handleLabel, null);
        visitor.visitTryCatchBlock(handleLabel, throwLabel, handleLabel, null);
        // Save lock to variable
        int varIndex = visitor.newLocal(OBJECT);
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
        Label gotoLabel = new Label();
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
        MethodVisitor init = writer.visitMethod(Opcodes.ACC_PUBLIC, INIT, EMPTY_DESCRIPTOR, null, null);
        init.visitCode();
        init.visitVarInsn(Opcodes.ALOAD, 0);
        init.visitMethodInsn(Opcodes.INVOKESPECIAL, superClass, INIT, EMPTY_DESCRIPTOR, false);
        init.visitInsn(Opcodes.RETURN);
        init.visitMaxs(0, 0);
        init.visitEnd();
    }

    /**
     * Creates an empty constructor
     *
     * @param writer {@link ClassWriter} containing the class in which the constructor will be created
     */
    public static void createEmptyConstructor(ClassWriter writer) {
        createEmptyConstructor(writer, INTERNAL_OBJECT_NAME);
    }

    /**
     * Creates a constructor containing N arguments and assigns these arguments to the appropriate fields.
     *
     * @param writer     {@link ClassWriter} containing the class in which the constructor will be created
     * @param superClass class parent
     * @param owner      class internal name
     * @param variables  constructor arguments
     */
    public static void createConstructor(ClassWriter writer, String superClass, String owner, Variable... variables) {
        if (variables.length > 255) {
            throw new IllegalStateException("The method cannot contain more than 255 arguments");
        }
        Type[] types = new Type[variables.length];
        // Generate fields and extracting types
        for (int i = 0; i < types.length; ++i) {
            Variable variable = variables[i];
            types[i] = variable.getType();
            writer
                    .visitField(FIELD_ACCESS, variable.getName(), variable.getType().getDescriptor(), null, null)
                    .visitEnd();
        }
        // Generate constructor header
        String descriptor = getDescriptor(Type.VOID_TYPE, types);
        MethodVisitor init = writer.visitMethod(Opcodes.ACC_PUBLIC, INIT, descriptor, null, null);
        init.visitCode();
        init.visitVarInsn(Opcodes.ALOAD, 0);
        init.visitMethodInsn(Opcodes.INVOKESPECIAL, superClass, INIT, EMPTY_DESCRIPTOR, false);
        // Set fields
        init.visitVarInsn(Opcodes.ALOAD, 0);
        for (int i = 0; i < variables.length; ++i) {
            Variable variable = variables[i];
            init.visitVarInsn(Opcodes.ALOAD, i + 1);
            init.visitFieldInsn(Opcodes.PUTFIELD, owner, variable.getName(), variable.getType().getDescriptor());
        }
        init.visitInsn(Opcodes.RETURN);
        init.visitMaxs(0, 0);
        init.visitEnd();
    }

    /**
     * Creates a constructor containing N arguments and assigns these arguments to the appropriate fields.
     *
     * @param writer    {@link ClassWriter} containing the class in which the constructor will be created
     * @param owner     class internal name
     * @param variables constructor arguments
     */
    public static void createConstructor(ClassWriter writer, String owner, Variable... variables) {
        createConstructor(writer, INTERNAL_OBJECT_NAME, owner, variables);
    }

    public static int resetAccess(int modifiers) {
        return modifiers & ACCESS_MASK;
    }
}
