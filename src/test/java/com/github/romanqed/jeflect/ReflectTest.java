package com.github.romanqed.jeflect;

import com.github.romanqed.jeflect.parsers.AsmClassFileParser;
import com.github.romanqed.jeflect.parsers.ClassFileParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public final class ReflectTest extends Assertions {
    private static final ClassFileParser PARSER = new AsmClassFileParser();

    private static byte[] getByteCode(Class<?> clazz) {
        var resource = clazz.getName().replace('.', '/') + ".class";
        var stream = clazz.getClassLoader().getResourceAsStream(resource);
        Objects.requireNonNull(stream);
        try {
            var ret = new byte[stream.available()];
            if (stream.read(ret) == -1) {
                throw new IOException("Cannot read classfile");
            }
            stream.close();
            return ret;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot get class bytecode due to", e);
        }
    }

    public void assertAnnotation(ByteAnnotation annotation) {
        Object[] value1 = annotation.getField("value1");
        int[] value2 = annotation.getField("value2");
        Integer value3 = annotation.getField("value3");
        Type value4 = annotation.getField("value4");
        assertAll(
                () -> assertEquals(B.class, annotation.getAnnotationClass()),
                () -> assertEquals("a", value1[0]),
                () -> assertEquals("b", value1[1]),
                () -> assertEquals(5, value2[0]),
                () -> assertEquals(6, value2[1]),
                () -> assertEquals(5, value3),
                () -> assertEquals(Type.getType(Object.class), value4)
        );
    }

    public void assertField(Field field, ByteField byteField) {
        assertAll(
                () -> assertEquals(field.getType(), byteField.getType()),
                () -> assertEquals(field.getName(), byteField.getName()),
                () -> assertEquals(field.getDeclaringClass(), byteField.getDeclaringClass().getType().getType()),
                () -> assertEquals(field.getModifiers(), byteField.getModifiers())
        );
    }

    public void assertMethod(Method method, ByteMethod byteMethod) {
        var exceptionTypes = byteMethod.getExceptionTypes().stream().map(LazyType::getType).toArray();
        var parameterTypes = byteMethod.getParameters().stream().map(ByteParameter::getType).toArray();
        assertAll(
                () -> assertEquals(method.getName(), byteMethod.getName()),
                () -> assertEquals(method.getDeclaringClass(), byteMethod.getDeclaringClass().getType().getType()),
                () -> assertEquals(method.getReturnType(), byteMethod.getReturnType().getType()),
                () -> assertEquals(method.getModifiers(), byteMethod.getModifiers()),
                () -> assertEquals(Type.getMethodDescriptor(method), byteMethod.getDescriptor()),
                () -> assertArrayEquals(method.getExceptionTypes(), exceptionTypes),
                () -> assertArrayEquals(method.getParameterTypes(), parameterTypes)
        );
    }

    public void assertConstructor(Constructor<?> constructor, ByteMethod byteConstructor) {
        var exceptionTypes = byteConstructor.getExceptionTypes().stream().map(LazyType::getType).toArray();
        var parameterTypes = byteConstructor.getParameters().stream().map(ByteParameter::getType).toArray();
        assertAll(
                () -> assertEquals("<init>", byteConstructor.getName()),
                () -> assertEquals(void.class, byteConstructor.getReturnType().getType()),
                () -> assertEquals(constructor.getDeclaringClass(),
                        byteConstructor.getDeclaringClass().getType().getType()),
                () -> assertEquals(constructor.getModifiers(), byteConstructor.getModifiers()),
                () -> assertArrayEquals(constructor.getExceptionTypes(), exceptionTypes),
                () -> assertArrayEquals(constructor.getParameterTypes(), parameterTypes)
        );
    }

    @Test
    public void testReflect() throws Exception {
        var clazz = Sample.class;
        // Parse bytecode
        var byteClass = PARSER.parse(getByteCode(Sample.class));
        // Assert clazz name, type and annotation
        assertEquals(clazz.getName(), byteClass.getName());
        assertEquals(clazz, byteClass.getType().getType());
        assertAnnotation(byteClass.getAnnotation(B.class));
        // Assert parents
        assertEquals(clazz.getSuperclass(), byteClass.getSuperclass().getType());
        assertArrayEquals(clazz.getInterfaces(), byteClass.getInterfaces().stream().map(LazyType::getType).toArray());

        // Assert fields
        // Descriptors
        var aDescriptor = Type.getDescriptor(A.class);
        var stringDescriptor = Type.getDescriptor(String.class);
        var intDescriptor = Type.getDescriptor(int.class);
        // Check annotation on first field
        var field = byteClass.getField("field", aDescriptor);
        assertAnnotation(field.getAnnotation(B.class));
        // Check field value
        var field2 = byteClass.getField("field2", intDescriptor);
        assertEquals(190, field2.getValue());
        // Assertions
        assertField(clazz.getDeclaredField("field"), field);
        assertField(clazz.getDeclaredField("field1"), byteClass.getField("field1", stringDescriptor));
        assertField(clazz.getDeclaredField("field2"), field2);

        // Assert methods
        // Default reflection
        var method1 = clazz.getDeclaredMethod("getField");
        var method2 = clazz.getDeclaredMethod("getField1");
        var method3 = clazz.getDeclaredMethod("test", A.class, int.class);
        // Check annotation on getField method
        var getField = byteClass.getMethod("getField", Type.getMethodDescriptor(method1));
        assertAnnotation(getField.getAnnotation(B.class));
        // Check parameter annotation on test method
        var test = byteClass.getMethod("test", Type.getMethodDescriptor(method3));
        assertAnnotation(test.getParameters().get(1).getAnnotation(B.class));
        // Assertions
        assertMethod(method1, getField);
        assertMethod(method2, byteClass.getMethod("getField1", Type.getMethodDescriptor(method2)));
        assertMethod(method3, test);

        // Assert constructors
        // Check annotation
        var ctor = byteClass.getMethod("<init>", "()V");
        assertAnnotation(ctor.getAnnotation(B.class));
        // Assertion
        assertConstructor(clazz.getDeclaredConstructor(), ctor);
    }

    @interface B {
        String[] value1();

        int[] value2();

        int value3();

        Class<?> value4();
    }

    static class A {

    }

    @B(value1 = {"a", "b"}, value2 = {5, 6}, value3 = 5, value4 = Object.class)
    static class Sample implements Cloneable {
        private final int field2 = 190;
        @B(value1 = {"a", "b"}, value2 = {5, 6}, value3 = 5, value4 = Object.class)
        private A field;
        private String field1;

        @B(value1 = {"a", "b"}, value2 = {5, 6}, value3 = 5, value4 = Object.class)
        public Sample() {

        }

        @B(value1 = {"a", "b"}, value2 = {5, 6}, value3 = 5, value4 = Object.class)
        private A getField() {
            return field;
        }

        public String getField1() throws Exception {
            return field1;
        }

        void test(A value, @B(value1 = {"a", "b"}, value2 = {5, 6}, value3 = 5, value4 = Object.class) int param) {

        }
    }
}
