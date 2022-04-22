package com.github.romanqed.jeflect;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

import static com.github.romanqed.jeflect.Constants.*;

class VirtualConstructorCreator implements Consumer<MethodVisitor> {
    private final String className;
    private final String descriptor;

    VirtualConstructorCreator(String className, String descriptor) {
        this.className = className;
        this.descriptor = descriptor;
    }

    @Override
    public void accept(MethodVisitor visitor) {
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, OBJECT, INIT, EMPTY_DESCRIPTOR, false);
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitVarInsn(Opcodes.ALOAD, 1);
        visitor.visitFieldInsn(Opcodes.PUTFIELD, className, FIELD_NAME, descriptor);
        visitor.visitInsn(Opcodes.RETURN);
    }
}
