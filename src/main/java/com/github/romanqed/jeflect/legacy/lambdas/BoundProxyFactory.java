package com.github.romanqed.jeflect.legacy.lambdas;

import com.github.romanqed.jeflect.legacy.AsmUtil;
import com.github.romanqed.jeflect.legacy.Variable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static com.github.romanqed.jeflect.legacy.lambdas.Util.*;

final class BoundProxyFactory extends AbstractProxyFactory {
    @Override
    protected void createConstructor(String owner, ClassWriter writer, Type argument) {
        AsmUtil.createConstructor(writer, owner, new Variable(FIELD_NAME, argument));
    }

    @Override
    protected void createMethod(String owner, ClassWriter writer, MethodData data) {
        MethodVisitor call = writer.visitMethod(Opcodes.ACC_PUBLIC, METHOD, BOUND_DESCRIPTOR, null, EXCEPTIONS);
        call.visitCode();
        // Load body field to invoke virtual method from it
        call.visitVarInsn(Opcodes.ALOAD, 0);
        call.visitFieldInsn(Opcodes.GETFIELD, owner, FIELD_NAME, data.owner.getDescriptor());
        // Create arguments
        prepareArguments(call, data.getArguments(), 1);
        // Invoke method
        invokeMethod(call, data);
        call.visitMaxs(0, 0);
        call.visitEnd();
    }
}
