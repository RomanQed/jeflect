package com.github.romanqed.jeflect.lambdas;

import com.github.romanqed.jeflect.AsmUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

final class ProxyUtil {
    private static final Type OBJECT_TYPE = Type.getType(Object.class);
    private static final String OBJECT_NAME = Type.getInternalName(Object.class);
    private static final String METHOD_NAME = "invoke";
    private static final String METHOD_DESCRIPTOR = Type.getMethodDescriptor(
            OBJECT_TYPE,
            OBJECT_TYPE,
            Type.getType(Object[].class)
    );
    private static final String LAMBDA_NAME = Type.getInternalName(Lambda.class);
    private static final String THROWABLE_NAME = Type.getInternalName(Throwable.class);

    static byte[] createProxy(String name,
                              Class<?>[] parameters,
                              Consumer<MethodVisitor> loader,
                              Consumer<MethodVisitor> invoker) {
        // Create proxy class
        var writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        writer.visit(Opcodes.V11,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                name,
                null,
                OBJECT_NAME,
                new String[]{LAMBDA_NAME});
        // Create empty constructor
        AsmUtil.createEmptyConstructor(writer);
        // Implement proxy method
        var visitor = writer.visitMethod(Opcodes.ACC_PUBLIC,
                METHOD_NAME,
                METHOD_DESCRIPTOR,
                null,
                new String[]{THROWABLE_NAME});
        visitor.visitCode();
        // Load executable owner
        loader.accept(visitor);
        // Extract parameters
        var index = 0;
        for (var parameter : parameters) {
            // Load array containing params
            visitor.visitVarInsn(Opcodes.ALOAD, 2);
            // Push index to stack
            // If it < 5, use const opcodes
            if (index <= 5) {
                visitor.visitInsn(index + Opcodes.ICONST_0);
            } else {
                // Else use push instructions
                // Since according to the specification,
                // the method cannot have more than 255 parameters,
                // we do not need to use LDC
                visitor.visitIntInsn(index <= Byte.MAX_VALUE ? Opcodes.BIPUSH : Opcodes.SIPUSH, index);
            }
            // Load argument from array
            visitor.visitInsn(Opcodes.AALOAD);
            // Cast argument
            AsmUtil.castReference(visitor, Type.getType(parameter));
            ++index;
        }
        // Invoke target method
        invoker.accept(visitor);
        // Return result
        visitor.visitInsn(Opcodes.ARETURN);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
        return writer.toByteArray();
    }

    static byte[] createProxy(String name, Method method) {
        var isStatic = Modifier.isStatic(method.getModifiers());
        var owner = Type.getType(method.getDeclaringClass());
        Consumer<MethodVisitor> loader = visitor -> {
            if (!isStatic) {
                // Load object
                visitor.visitVarInsn(Opcodes.ALOAD, 1);
                // Cast to method owner
                visitor.visitTypeInsn(Opcodes.CHECKCAST, owner.getInternalName());
            }
        };
        Consumer<MethodVisitor> invoker = visitor -> {
            boolean isInterface = method.getDeclaringClass().isInterface();
            // Select opcode to invoke method
            var opcode = isStatic ? Opcodes.INVOKESTATIC :
                    (isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL);
            // Invoke target method
            visitor.visitMethodInsn(opcode,
                    owner.getInternalName(),
                    method.getName(),
                    Type.getType(method).getDescriptor(),
                    isInterface);
            // Wrap return value if it necessary
            var type = method.getReturnType();
            if (type == void.class) {
                visitor.visitInsn(Opcodes.ACONST_NULL);
                return;
            }
            AsmUtil.packPrimitive(visitor, Type.getType(type));
        };
        return createProxy(name, method.getParameterTypes(), loader, invoker);
    }

    static byte[] createProxy(String name, Constructor<?> constructor) {
        var owner = Type.getType(constructor.getDeclaringClass());
        Consumer<MethodVisitor> loader = visitor -> {
            // Create object
            visitor.visitTypeInsn(Opcodes.NEW, owner.getInternalName());
            visitor.visitInsn(Opcodes.DUP);
        };
        Consumer<MethodVisitor> invoker = visitor -> {
            // Invoke target constructor
            visitor.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    owner.getInternalName(),
                    AsmUtil.INIT,
                    Type.getConstructorDescriptor(constructor),
                    false);
        };
        return createProxy(name, constructor.getParameterTypes(), loader, invoker);
    }
}
