package com.github.romanqed.jeflect;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

abstract class CommonMethodCreator implements Consumer<MethodVisitor> {
    private final boolean hasReturn;

    CommonMethodCreator(boolean hasReturn) {
        this.hasReturn = hasReturn;
    }

    @Override
    public void accept(MethodVisitor visitor) {
        if (!hasReturn) {
            visitor.visitInsn(Opcodes.ACONST_NULL);
        }
        visitor.visitInsn(Opcodes.ARETURN);
    }
}
