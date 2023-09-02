package com.github.romanqed.jeflect.legacy.fields;

import com.github.romanqed.jeflect.AsmUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class FieldProxyFactory {
    private static final Class<?> ACCESSOR = FieldAccessor.class;
    private static final Type ACCESSOR_TYPE = Type.getType(ACCESSOR);
    private static final int ACCESS = Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL;
    private static final Method GET;
    private static final Method SET;
    private static final Method STATIC_GET;
    private static final Method STATIC_SET;

    static {
        try {
            GET = ACCESSOR.getDeclaredMethod("get", Object.class);
            SET = ACCESSOR.getDeclaredMethod("set", Object.class, Object.class);
            STATIC_GET = ACCESSOR.getDeclaredMethod("get");
            STATIC_SET = ACCESSOR.getDeclaredMethod("set", Object.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Cannot initialize field proxy factory due to", e);
        }
    }

    byte[] create(String name, Field source) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        writer.visit(Opcodes.V1_8,
                ACCESS,
                name,
                null,
                AsmUtil.INTERNAL_OBJECT_NAME,
                new String[]{ACCESSOR_TYPE.getInternalName()});
        AsmUtil.createEmptyConstructor(writer);
        Type owner = Type.getType(source.getDeclaringClass());
        String fieldName = source.getName();
        Type fieldType = Type.getType(source.getType());
        boolean isStatic = Modifier.isStatic(source.getModifiers());
        createGet(writer, owner, fieldName, fieldType, isStatic);
        createSet(writer, owner, fieldName, fieldType, isStatic);
        writer.visitEnd();
        return writer.toByteArray();
    }

    MethodVisitor createMethod(ClassWriter writer, Method target, Type owner, boolean isStatic) {
        MethodVisitor visitor = writer.visitMethod(Opcodes.ACC_PUBLIC,
                target.getName(),
                Type.getMethodDescriptor(target),
                null,
                null);
        if (!isStatic) {
            // Load object and cast it
            visitor.visitVarInsn(Opcodes.ALOAD, 1);
            visitor.visitTypeInsn(Opcodes.CHECKCAST, owner.getInternalName());
        }
        return visitor;
    }

    void createGet(ClassWriter writer, Type owner, String name, Type type, boolean isStatic) {
        Method target = isStatic ? STATIC_GET : GET;
        MethodVisitor visitor = createMethod(writer, target, owner, isStatic);
        int opcode = isStatic ? Opcodes.GETSTATIC : Opcodes.GETFIELD;
        visitor.visitFieldInsn(opcode, owner.getInternalName(), name, type.getDescriptor());
        AsmUtil.packPrimitive(visitor, type);
        visitor.visitInsn(Opcodes.ARETURN);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    void createSet(ClassWriter writer, Type owner, String name, Type type, boolean isStatic) {
        Method target = isStatic ? STATIC_SET : SET;
        MethodVisitor visitor = createMethod(writer, target, owner, isStatic);
        // Load and cast value
        visitor.visitVarInsn(Opcodes.ALOAD, isStatic ? 1 : 2);
        AsmUtil.castReference(visitor, type);
        int opcode = isStatic ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD;
        visitor.visitFieldInsn(opcode, owner.getInternalName(), name, type.getDescriptor());
        visitor.visitInsn(Opcodes.RETURN);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }
}
