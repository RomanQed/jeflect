package com.github.romanqed.jeflect;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

import static com.github.romanqed.jeflect.Constants.*;

class LambdaCreator extends CommonCreator {
    private static final String DESCRIPTOR = formatDescriptor("L" + OBJECT + ";", "[L" + OBJECT + ";");
    private final String descriptor;
    private final Consumer<MethodVisitor> constructor;

    LambdaCreator(String descriptor, Consumer<MethodVisitor> constructor, Consumer<MethodVisitor> method) {
        super(DESCRIPTOR, method);
        this.descriptor = descriptor;
        this.constructor = constructor;
    }

    @Override
    public void accept(ClassWriter writer) {
        // Generate constructor
        MethodVisitor init = writer.visitMethod(Opcodes.ACC_PUBLIC, INIT, descriptor, null, null);
        init.visitCode();
        constructor.accept(init);
        init.visitMaxs(0, 0);
        init.visitEnd();
        super.accept(writer);
    }
}
