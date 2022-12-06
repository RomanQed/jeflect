package com.github.romanqed.jeflect.lambdas;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static com.github.romanqed.jeflect.AsmUtil.*;

final class Util {
    static final String FIELD_NAME = "body";
    static final String LAMBDA = Type.getType(Lambda.class).getInternalName();
    static final int INT_0 = Opcodes.ICONST_0;
    static final String METHOD = "call";
    static final String[] EXCEPTIONS = new String[]{THROWABLE.getInternalName()};
    static final String BOUND_DESCRIPTOR = getDescriptor(OBJECT, OBJECT_ARRAY);
    static final String FREE_DESCRIPTOR = getDescriptor(OBJECT, OBJECT, OBJECT_ARRAY);
    static final String VOID = Type.VOID_TYPE.getDescriptor();

    static void invokeMethod(MethodVisitor visitor, MethodData data) {
        // Select opcode to invoke method
        int opcode;
        if (data.isStatic) {
            opcode = Opcodes.INVOKESTATIC;
        } else {
            opcode = data.isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL;
        }
        // Invoke method
        visitor.visitMethodInsn(opcode,
                data.owner.getInternalName(),
                data.methodName,
                data.getDescriptor(),
                data.isInterface);
        // Generate ret value
        Type returnType = data.returnType;
        if (returnType.getDescriptor().equals(VOID)) {
            visitor.visitInsn(Opcodes.ACONST_NULL);
        } else {
            packPrimitive(visitor, returnType);
        }
        visitor.visitInsn(Opcodes.ARETURN);
    }

    static void prepareArguments(MethodVisitor visitor, Type[] arguments, int offset) {
        for (int i = 0; i < arguments.length; ++i) {
            visitor.visitVarInsn(Opcodes.ALOAD, offset);
            if (i < 6) {
                visitor.visitInsn(INT_0 + i);
            } else {
                int opcode = i <= Byte.MAX_VALUE ? Opcodes.BIPUSH : Opcodes.SIPUSH;
                visitor.visitIntInsn(opcode, i);
            }
            visitor.visitInsn(Opcodes.AALOAD);
            castReference(visitor, arguments[i]);
        }
    }

    static void createStaticMethod(ClassWriter writer, MethodData data) {
        MethodVisitor call = writer.visitMethod(Opcodes.ACC_PUBLIC, METHOD, BOUND_DESCRIPTOR, null, EXCEPTIONS);
        // Create arguments
        prepareArguments(call, data.getArguments(), 1);
        // Invoke method
        invokeMethod(call, data);
        call.visitMaxs(0, 0);
        call.visitEnd();
    }
}
