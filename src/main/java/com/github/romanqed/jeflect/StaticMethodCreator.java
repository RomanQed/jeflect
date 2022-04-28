package com.github.romanqed.jeflect;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

class StaticMethodCreator extends CommonMethodCreator {
    private final Type clazz;
    private final MethodData data;
    private final boolean isInterface;

    StaticMethodCreator(Type clazz, boolean isInterface, MethodData data, int argument) {
        super(data.returnType, data.getArguments(), argument);
        this.clazz = clazz;
        this.data = data;
        this.isInterface = isInterface;
    }

    @Override
    public void accept(MethodVisitor visitor) {
        // Create arguments
        createArguments(visitor);
        // Invoke method
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                clazz.getInternalName(),
                data.methodName,
                data.getDescriptor(),
                isInterface);
        // Generate ret code
        super.accept(visitor);
    }
}
