package com.github.romanqed.jeflect;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

class ArgumentCreator implements Consumer<MethodVisitor> {
    private static final int INT_0 = Opcodes.ICONST_0;
    private final Type[] arguments;

    ArgumentCreator(Type[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public void accept(MethodVisitor visitor) {
        for (int i = 0; i < arguments.length; ++i) {
            visitor.visitVarInsn(Opcodes.ALOAD, 1);
            if (i < 6) {
                visitor.visitInsn(INT_0 + i);
            } else {
                visitor.visitVarInsn(Opcodes.BIPUSH, i);
            }
            visitor.visitInsn(Opcodes.AALOAD);
            visitor.visitTypeInsn(Opcodes.CHECKCAST, arguments[i].getInternalName());
        }
    }
}
