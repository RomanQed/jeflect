package com.github.romanqed.jeflect;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

import static com.github.romanqed.jeflect.Constants.*;

class LambdaMethodCreator extends CommonCreator {
    private static final Consumer<MethodVisitor> CONSTRUCTOR = new EmptyConstructorCreator();
    private static final String DESCRIPTOR = getDescriptor();

    LambdaMethodCreator(Consumer<MethodVisitor> method) {
        super(DESCRIPTOR, method);
    }

    private static String getDescriptor() {
        String object = "L" + OBJECT + ";";
        return formatDescriptor(object, object + "[" + object);
    }

    @Override
    public void accept(ClassWriter writer) {
        // Generate constructor
        MethodVisitor init = writer.visitMethod(Opcodes.ACC_PUBLIC, INIT, EMPTY_DESCRIPTOR, null, null);
        init.visitCode();
        CONSTRUCTOR.accept(init);
        init.visitMaxs(0, 0);
        init.visitEnd();
        super.accept(writer);
    }
}
