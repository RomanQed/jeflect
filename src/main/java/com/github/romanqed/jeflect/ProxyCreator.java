package com.github.romanqed.jeflect;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

import static com.github.romanqed.jeflect.Constants.*;

class ProxyCreator implements Consumer<ClassWriter> {
    private final String descriptor;
    private final Consumer<MethodVisitor> constructor;
    private final Consumer<MethodVisitor> method;

    ProxyCreator(String descriptor, Consumer<MethodVisitor> constructor, Consumer<MethodVisitor> method) {
        this.descriptor = descriptor;
        this.constructor = constructor;
        this.method = method;
    }

    @Override
    public void accept(ClassWriter writer) {
        // Generate constructor
        MethodVisitor init = writer.visitMethod(Opcodes.ACC_PUBLIC, INIT, descriptor, null, null);
        init.visitCode();
        constructor.accept(init);
        init.visitMaxs(0, 0);
        init.visitEnd();
        // Generate method
        MethodVisitor call = writer.visitMethod(Opcodes.ACC_PUBLIC, METHOD, METHOD_DESCRIPTOR, null, null);
        call.visitCode();
        method.accept(call);
        call.visitMaxs(0, 0);
        call.visitEnd();
    }
}
