package com.github.romanqed.jeflect.lambdas;

import com.github.romanqed.jeflect.AsmUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;

final class ConstructorProxyFactory {
    private static final int ACCESS = Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL;

    byte[] create(String name, Constructor<?> constructor) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        writer.visit(Opcodes.V1_8,
                ACCESS,
                name,
                null,
                AsmUtil.INTERNAL_OBJECT_NAME,
                new String[]{Util.LAMBDA});
        AsmUtil.createEmptyConstructor(writer);
        MethodVisitor visitor = writer.visitMethod(Opcodes.ACC_PUBLIC,
                Util.METHOD,
                Util.BOUND_DESCRIPTOR,
                null,
                Util.EXCEPTIONS);
        visitor.visitCode();
        // Instantiate ctor declaring class
        String descriptor = Type.getConstructorDescriptor(constructor);
        Type type = Type.getType(descriptor);
        Type owner = Type.getType(constructor.getDeclaringClass());
        visitor.visitTypeInsn(Opcodes.NEW, owner.getInternalName());
        visitor.visitInsn(Opcodes.DUP);
        // Prepare arguments
        Util.prepareArguments(visitor, type.getArgumentTypes(), 1);
        // Invoke ctor
        visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, owner.getInternalName(), AsmUtil.INIT, descriptor, false);
        // Return result
        visitor.visitInsn(Opcodes.ARETURN);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
        writer.visitEnd();
        return writer.toByteArray();
    }
}
