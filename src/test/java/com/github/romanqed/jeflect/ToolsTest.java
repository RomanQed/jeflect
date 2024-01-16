package com.github.romanqed.jeflect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ToolsTest extends Assertions {
    @Test
    public void testExtractionFromAnnotation() throws Exception {
        var clazz = TestClass.class;
        var classAnnotation = clazz.getAnnotation(TestAnnotation.class);
        var fieldAnnotation = clazz.getField("a").getAnnotation(TestAnnotation.class);
        var methodAnnotation = clazz.getMethod("b").getAnnotation(TestAnnotation.class);
        assertAll(
                () -> assertEquals(1, JeflectUtil.<Integer>extractAnnotationValue(classAnnotation, "a")),
                () -> assertEquals("str1", JeflectUtil.<String>extractAnnotationValue(classAnnotation, "b")),
                () -> assertEquals(2, JeflectUtil.<Integer>extractAnnotationValue(fieldAnnotation, "a")),
                () -> assertEquals("str2", JeflectUtil.<String>extractAnnotationValue(fieldAnnotation, "b")),
                () -> assertEquals(3, JeflectUtil.<Integer>extractAnnotationValue(methodAnnotation, "a")),
                () -> assertEquals("str3", JeflectUtil.<String>extractAnnotationValue(methodAnnotation, "b"))
        );
    }

    @Test
    public void testExtractionFromEnum() {
        var map = JeflectUtil.enumToMap(TestEnum.class, TestEnum::getValue);
        assertAll(
                () -> assertEquals(1, map.get(TestEnum.VALUE1)),
                () -> assertEquals(2, map.get(TestEnum.VALUE2)),
                () -> assertEquals(3, map.get(TestEnum.VALUE3))
        );
    }

    enum TestEnum {
        VALUE1(1),
        VALUE2(2),
        VALUE3(3);

        final int value;

        TestEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface TestAnnotation {
        int a();

        String b();
    }

    @TestAnnotation(a = 1, b = "str1")
    static class TestClass {
        @TestAnnotation(a = 2, b = "str2")
        public int a;

        @TestAnnotation(a = 3, b = "str3")
        public void b() {
        }
    }
}
