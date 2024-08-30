package com.github.romanqed.jeflect.loader;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class DefineLoaderTest {
    private static final DefineLoader LOADER = new DefineClassLoader();
    private static final ObjectFactory<?> FACTORY = new DefineObjectFactory<>(LOADER);

    private static byte[] generateClass(String className, String methodName, String value) {
        var writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        writer.visit(
                Opcodes.V11,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                className,
                null,
                Type.getInternalName(Object.class),
                null
        );
        var cv = writer.visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null
        );
        cv.visitCode();
        cv.visitVarInsn(Opcodes.ALOAD, 0);
        cv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                Type.getInternalName(Object.class),
                "<init>",
                "()V",
                false
        );
        cv.visitInsn(Opcodes.RETURN);
        cv.visitMaxs(0, 0);
        cv.visitEnd();
        var mv = writer.visitMethod(
                Opcodes.ACC_PUBLIC,
                methodName,
                "()Ljava/lang/String;",
                null,
                null
        );
        mv.visitCode();
        mv.visitLdcInsn(value);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        return writer.toByteArray();
    }

    @Test
    public void testDefineClass() throws Exception {
        var className = "TestClass1";
        var methodName = "testMethod";
        var value = "testValue" + System.nanoTime();
        var bytes = generateClass(className, methodName, value);
        var clazz = LOADER.define(className, bytes);
        assertEquals(className, clazz.getName());
        var method = clazz.getMethod(methodName);
        assertEquals(methodName, method.getName());
        assertEquals(value, method.invoke(clazz.getConstructor().newInstance()));
    }

    @Test
    public void testInstantiateObject() throws Exception {
        var className = "TestClass2";
        var methodName = "testMethod";
        var value = "testValue" + System.nanoTime();
        var object = FACTORY.create(className, () -> generateClass(className, methodName, value));
        assertNotNull(object);
        var clazz = object.getClass();
        assertEquals(className, clazz.getName());
        var method = clazz.getMethod(methodName);
        assertEquals(methodName, method.getName());
        assertEquals(value, method.invoke(object));
    }
}
