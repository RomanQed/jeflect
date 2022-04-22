package com.github.romanqed.jeflect;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

import static com.github.romanqed.jeflect.Constants.*;

class StaticConstructorCreator implements Consumer<MethodVisitor> {
    @Override
    public void accept(MethodVisitor visitor) {
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, OBJECT, INIT, EMPTY_DESCRIPTOR, false);
        visitor.visitInsn(Opcodes.RETURN);
    }
}
