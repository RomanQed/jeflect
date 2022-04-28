package com.github.romanqed.jeflect;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

class VirtualMethodCreator extends CommonMethodCreator {
    private final Type clazz;
    private final MethodData data;
    private final boolean isInterface;

    VirtualMethodCreator(Type clazz, boolean isInterface, MethodData data) {
        super(data.returnType, data.getArguments(), 2);
        this.clazz = clazz;
        this.data = data;
        this.isInterface = isInterface;
    }

    @Override
    public void accept(MethodVisitor visitor) {
        // Load object from arguments
        visitor.visitVarInsn(Opcodes.ALOAD, 1);
        // Cast to bound type
        visitor.visitTypeInsn(Opcodes.CHECKCAST, clazz.getInternalName());
        // Create arguments
        createArguments(visitor);
        // Invoke method
        int opcode = isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL;
        visitor.visitMethodInsn(opcode,
                clazz.getInternalName(),
                data.methodName,
                data.getDescriptor(),
                isInterface);
        // Generate ret code
        super.accept(visitor);
    }
}
