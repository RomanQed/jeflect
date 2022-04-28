package com.github.romanqed.jeflect;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

import static com.github.romanqed.jeflect.Constants.METHOD;
import static com.github.romanqed.jeflect.Constants.THROWABLE;

abstract class CommonCreator implements Consumer<ClassWriter> {
    private final String descriptor;
    private final Consumer<MethodVisitor> method;

    CommonCreator(String descriptor, Consumer<MethodVisitor> method) {
        this.descriptor = descriptor;
        this.method = method;
    }

    @Override
    public void accept(ClassWriter writer) {
        // Generate method
        MethodVisitor call = writer.visitMethod(Opcodes.ACC_PUBLIC,
                METHOD,
                descriptor,
                null,
                new String[]{THROWABLE});
        call.visitCode();
        method.accept(call);
        call.visitMaxs(0, 0);
        call.visitEnd();
    }
}
