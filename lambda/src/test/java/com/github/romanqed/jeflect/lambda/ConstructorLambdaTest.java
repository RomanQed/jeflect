package com.github.romanqed.jeflect.lambda;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public final class ConstructorLambdaTest {
    private static final LambdaFactory FACTORY = new BytecodeLambdaFactory();

    @Test
    public void testEmptyConstructor() throws Throwable {
        var packed = FACTORY.packConstructor(A.class.getDeclaredConstructor());
        var a = (A) packed.invoke();
        assertEquals(10, a.a);
    }

    @Test
    public void testOneArgumentConstructor() throws Throwable {
        var packed = FACTORY.packConstructor(A.class.getDeclaredConstructor(int.class));
        var a = (A) packed.invoke(new Object[]{1});
        assertEquals(1, a.a);
    }

    @Test
    public void testMultiArgumentConstructor() throws Throwable {
        var packed = FACTORY.packConstructor(A.class.getDeclaredConstructor(int.class, int.class));
        var a = (A) packed.invoke(new Object[]{5, 6});
        assertEquals(11, a.a);
    }

    @Test
    public void testConstructorWithException() throws Throwable {
        var packed = FACTORY.packConstructor(A.class.getDeclaredConstructor(String.class));
        assertThrowsExactly(Exception.class, () -> packed.invoke(new Object[]{""}));
    }

    public static class A {
        int a;

        public A() {
            this.a = 10;
        }

        public A(int a) {
            this.a = a;
        }

        public A(int a, int b) {
            this.a = a + b;
        }

        public A(String value) throws Exception {
            if (value.isEmpty()) {
                throw new Exception("invalid");
            }
        }
    }
}
