package com.github.romanqed.jeflect.lambdas;

import com.github.romanqed.jeflect.AsmUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static com.github.romanqed.jeflect.lambdas.Constants.*;

final class FreeProxyFactory extends AbstractProxyFactory {
    @Override
    protected void createConstructor(String owner, ClassWriter writer, Type argument) {
        AsmUtil.createEmptyConstructor(writer);
    }

    @Override
    protected void createMethod(String owner, ClassWriter writer, MethodData data) {
        MethodVisitor call = writer.visitMethod(Opcodes.ACC_PUBLIC, METHOD, FREE_DESCRIPTOR, null, EXCEPTIONS);
        call.visitCode();
        // Load object from arguments
        call.visitVarInsn(Opcodes.ALOAD, 1);
        // Cast to bound type
        call.visitTypeInsn(Opcodes.CHECKCAST, data.owner.getInternalName());
        // Create arguments
        prepareArguments(call, data.getArguments(), 2);
        // Invoke method
        invokeMethod(call, data);
        call.visitMaxs(0, 0);
        call.visitEnd();
    }
}
