package com.github.romanqed.jeflect.lambdas;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static com.github.romanqed.jeflect.AsmUtil.*;
import static com.github.romanqed.jeflect.lambdas.Constants.*;

class BoundProxyFactory extends AbstractProxyFactory {
    @Override
    protected void createConstructor(String name, ClassWriter writer, MethodData data) {
        String descriptor = getDescriptor(Type.VOID_TYPE, data.owner);
        MethodVisitor init = writer.visitMethod(Opcodes.ACC_PUBLIC, INIT, descriptor, null, null);
        init.visitCode();
        writer.visitField(FIELD_ACCESS, FIELD_NAME, data.owner.getDescriptor(), null, null)
                .visitEnd();
        init.visitVarInsn(Opcodes.ALOAD, 0);
        init.visitMethodInsn(Opcodes.INVOKESPECIAL, OBJECT_NAME, INIT, EMPTY_DESCRIPTOR, false);
        init.visitVarInsn(Opcodes.ALOAD, 0);
        init.visitVarInsn(Opcodes.ALOAD, 1);
        init.visitFieldInsn(Opcodes.PUTFIELD, name, FIELD_NAME, data.owner.getDescriptor());
        init.visitInsn(Opcodes.RETURN);
        init.visitMaxs(0, 0);
        init.visitEnd();
    }

    @Override
    protected void createMethod(String name, ClassWriter writer, MethodData data) {
        MethodVisitor call = writer.visitMethod(Opcodes.ACC_PUBLIC, METHOD, BOUND_DESCRIPTOR, null, EXCEPTIONS);
        call.visitCode();
        // Load body field to invoke virtual method from it
        call.visitVarInsn(Opcodes.ALOAD, 0);
        call.visitFieldInsn(Opcodes.GETFIELD, name, FIELD_NAME, data.owner.getDescriptor());
        // Create arguments
        prepareArguments(call, data.getArguments(), 1);
        // Invoke method
        invokeMethod(call, data);
        call.visitMaxs(0, 0);
        call.visitEnd();
    }
}
