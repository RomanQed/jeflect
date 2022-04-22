package com.github.romanqed.jeflect;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

import static com.github.romanqed.jeflect.Constants.FIELD_NAME;

public class VirtualMethodCreator extends CommonMethodCreator {
    private final String name;
    private final Type clazz;
    private final MethodData data;
    private final boolean isInterface;
    private final Consumer<MethodVisitor> argumentCreator;

    VirtualMethodCreator(String name, Type clazz, boolean isInterface, MethodData data) {
        super(data.hasReturn);
        this.name = name;
        this.clazz = clazz;
        this.data = data;
        this.isInterface = isInterface;
        this.argumentCreator = new ArgumentCreator(data.getArguments());
    }

    @Override
    public void accept(MethodVisitor visitor) {
        // Load body field to invoke virtual method from it
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitFieldInsn(Opcodes.GETFIELD, name, FIELD_NAME, clazz.getDescriptor());
        // Create arguments
        argumentCreator.accept(visitor);
        // Invoke method
        int opcode = isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL;
        visitor.visitMethodInsn(opcode, clazz.getInternalName(), data.methodName, data.getDescriptor(), isInterface);
        // Generate ret code
        super.accept(visitor);
    }
}
