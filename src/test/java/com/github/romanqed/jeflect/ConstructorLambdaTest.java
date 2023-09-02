package com.github.romanqed.jeflect;

import com.github.romanqed.jeflect.legacy.lambdas.Lambda;
import com.github.romanqed.jeflect.legacy.lambdas.LambdaFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConstructorLambdaTest extends Assertions {
    private static final LambdaFactory FACTORY = new LambdaFactory();

    @Test
    public void testEmptyConstructor() throws Throwable {
        Lambda packed = FACTORY.packConstructor(A.class.getDeclaredConstructor());
        A a = (A) packed.call();
        assertEquals(10, a.a);
    }

    @Test
    public void testOneArgumentConstructor() throws Throwable {
        Lambda packed = FACTORY.packConstructor(A.class.getDeclaredConstructor(int.class));
        A a = (A) packed.call(new Object[]{1});
        assertEquals(1, a.a);
    }

    @Test
    public void testMultiArgumentConstructor() throws Throwable {
        Lambda packed = FACTORY.packConstructor(A.class.getDeclaredConstructor(int.class, int.class));
        A a = (A) packed.call(new Object[]{5, 6});
        assertEquals(11, a.a);
    }

    @Test
    public void testConstructorWithException() throws Throwable {
        Lambda packed = FACTORY.packConstructor(A.class.getDeclaredConstructor(String.class));
        assertThrowsExactly(Exception.class, () -> packed.call(new Object[]{""}));
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
