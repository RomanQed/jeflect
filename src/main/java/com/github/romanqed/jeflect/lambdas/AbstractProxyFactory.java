package com.github.romanqed.jeflect.lambdas;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

import static com.github.romanqed.jeflect.AsmUtil.*;
import static com.github.romanqed.jeflect.lambdas.Constants.*;

abstract class AbstractProxyFactory implements ProxyFactory {
    protected static void prepareArguments(MethodVisitor visitor, Type[] arguments, int offset) {
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

    protected static void invokeMethod(MethodVisitor visitor, MethodData data) {
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

    protected static void createEmptyConstructor(ClassWriter writer) {
        MethodVisitor init = writer.visitMethod(Opcodes.ACC_PUBLIC, INIT, EMPTY_DESCRIPTOR, null, new String[0]);
        init.visitCode();
        init.visitVarInsn(Opcodes.ALOAD, 0);
        init.visitMethodInsn(Opcodes.INVOKESPECIAL, OBJECT_NAME, INIT, EMPTY_DESCRIPTOR, false);
        init.visitInsn(Opcodes.RETURN);
        init.visitMaxs(0, 0);
        init.visitEnd();
    }

    protected static void createStaticMethod(ClassWriter writer, MethodData data) {
        MethodVisitor call = writer.visitMethod(Opcodes.ACC_PUBLIC, METHOD, BOUND_DESCRIPTOR, null, EXCEPTIONS);
        // Create arguments
        prepareArguments(call, data.getArguments(), 1);
        // Invoke method
        invokeMethod(call, data);
        call.visitMaxs(0, 0);
        call.visitEnd();
    }

    protected abstract void createConstructor(String name, ClassWriter writer, MethodData data);

    protected abstract void createMethod(String name, ClassWriter writer, MethodData data);

    @Override
    public byte[] create(String name, Method source) {
        ClassWriter ret = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ret.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, name, null, OBJECT_NAME, new String[]{LAMBDA});
        MethodData data = new MethodData(source);
        if (data.isStatic) {
            createEmptyConstructor(ret);
            createStaticMethod(ret, data);
        } else {
            createConstructor(name, ret, data);
            createMethod(name, ret, data);
        }
        ret.visitEnd();
        return ret.toByteArray();
    }
}
