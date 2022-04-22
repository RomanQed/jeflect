package com.github.romanqed.jeflect;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

public class StaticMethodCreator extends CommonMethodCreator {
    private final Type clazz;
    private final MethodData data;
    private final boolean isInterface;
    private final Consumer<MethodVisitor> argumentCreator;

    StaticMethodCreator(Type clazz, boolean isInterface, MethodData data) {
        super(data.hasReturn);
        this.clazz = clazz;
        this.data = data;
        this.isInterface = isInterface;
        this.argumentCreator = new ArgumentCreator(data.getArguments());
    }

    @Override
    public void accept(MethodVisitor visitor) {
        // Create arguments
        argumentCreator.accept(visitor);
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
