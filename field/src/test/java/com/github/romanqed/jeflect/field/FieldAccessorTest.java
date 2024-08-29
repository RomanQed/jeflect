package com.github.romanqed.jeflect.field;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class FieldAccessorTest {
    private static final FieldAccessorFactory FACTORY = new FieldAccessorFactory();

    @Test
    public void testPrimitives() throws Exception {
        var clazz = Primitives.class;
        var field1 = FACTORY.packField(clazz.getDeclaredField("field1"));
        var field2 = FACTORY.packField(clazz.getDeclaredField("field2"));
        var object = new Primitives();
        assertAll(
                () -> assertEquals(Primitives.field1, (int) field1.get()),
                () -> assertEquals(object.field2, (int) field2.get(object)),
                () -> {
                    field1.set(12);
                    assertEquals(12, Primitives.field1);
                },
                () -> {
                    field2.set(object, 15);
                    assertEquals(15, object.field2);
                }
        );
    }

    @Test
    public void testReferences() throws Exception {
        var clazz = References.class;
        var field1 = FACTORY.packField(clazz.getDeclaredField("field1"));
        var field2 = FACTORY.packField(clazz.getDeclaredField("field2"));
        var object = new References();
        assertAll(
                () -> assertEquals(References.field1, field1.get()),
                () -> assertEquals(object.field2, field2.get(object)),
                () -> {
                    field1.set("12");
                    assertEquals("12", References.field1);
                },
                () -> {
                    field2.set(object, "15");
                    assertEquals("15", object.field2);
                }
        );
    }

    @Test
    public void testArrays() throws Exception {
        var clazz = Arrays.class;
        var field1 = FACTORY.packField(clazz.getDeclaredField("field1"));
        var field2 = FACTORY.packField(clazz.getDeclaredField("field2"));
        var object = new Arrays();
        assertAll(
                () -> assertEquals(Arrays.field1, field1.get()),
                () -> assertEquals(object.field2, field2.get(object)),
                () -> {
                    var value = new String[]{"12"};
                    field1.set(value);
                    assertEquals(value, Arrays.field1);
                },
                () -> {
                    var value = new String[]{"15"};
                    field2.set(object, value);
                    assertEquals(value, object.field2);
                }
        );
    }

    @Test
    public void testFinals() throws Exception {
        var clazz = Finals.class;
        var field1 = FACTORY.packField(clazz.getDeclaredField("field1"));
        var field2 = FACTORY.packField(clazz.getDeclaredField("field2"));
        var object = new Finals();
        assertAll(
                () -> assertEquals(Finals.field1, (int) field1.get()),
                () -> assertEquals(object.field2, (int) field2.get(object)),
                () -> assertThrowsExactly(UnsupportedOperationException.class, () -> field1.set(1)),
                () -> assertThrowsExactly(UnsupportedOperationException.class, () -> field2.set(object, 1))
        );
    }

    public static class Primitives {
        public static int field1 = 123;

        public int field2 = 321;
    }

    public static class References {
        public static String field1 = "123";

        public String field2 = "321";
    }

    public static class Arrays {
        public static String[] field1 = {"123"};

        public String[] field2 = {"321"};
    }

    public static class Finals {
        public static final int field1 = 123;

        public final int field2 = 321;
    }
}
