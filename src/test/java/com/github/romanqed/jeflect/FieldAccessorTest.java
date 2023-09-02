//package com.github.romanqed.jeflect;
//
//import com.github.romanqed.jeflect.legacy.fields.FieldAccessor;
//import com.github.romanqed.jeflect.legacy.fields.FieldAccessorFactory;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.lang.reflect.Field;
//
//public class FieldAccessorTest extends Assertions {
//    private static final FieldAccessorFactory FACTORY = new FieldAccessorFactory();
//
//    @Test
//    public void testStatic() throws NoSuchFieldException {
//        Field field1 = A.class.getField("field1");
//        FieldAccessor accessor = FACTORY.packField(field1);
//        assertEquals(123, (Integer) accessor.get());
//        accessor.set(456);
//        assertEquals(456, A.field1);
//    }
//
//    @Test
//    public void testVirtual() throws NoSuchFieldException {
//        Field field2 = A.class.getField("field2");
//        FieldAccessor accessor = FACTORY.packField(field2);
//        A a = new A();
//        assertEquals(321, (Integer) accessor.get(a));
//        accessor.set(a, 654);
//        assertEquals(654, a.field2);
//    }
//
//    public static class A {
//        public static int field1 = 123;
//
//        public int field2 = 321;
//    }
//}
