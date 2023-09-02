//package com.github.romanqed.jeflect;
//
//import com.github.romanqed.jeflect.legacy.binding.BindName;
//import com.github.romanqed.jeflect.legacy.binding.BindingFactory;
//import com.github.romanqed.jeflect.legacy.binding.InterfaceType;
//import com.github.romanqed.jeflect.legacy.binding.Overridable;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//public class BindingTest extends Assertions {
//    static final BindingFactory FACTORY = new BindingFactory();
//    static final InterfaceType<IA> IA_TYPE = InterfaceType.fromClass(IA.class);
//    static final InterfaceType<IB> IB_TYPE = InterfaceType.fromClass(IB.class);
//    static final InterfaceType<IC> IC_TYPE = InterfaceType.fromClass(IC.class);
//    static final InterfaceType<Types> TYPES_TYPE = InterfaceType.fromClass(Types.class);
//
//    @Test
//    public void testInterface() {
//        IA ia = FACTORY.bind(IA_TYPE, new Impl());
//        assertEquals("iA", ia.iA());
//    }
//
//    @Test
//    public void testInheritedInterface() {
//        IB ib = FACTORY.bind(IB_TYPE, new Impl());
//        assertEquals("iB", ib.iB());
//    }
//
//    @Test
//    public void testInheritedTwiceInterface() {
//        IC ic = FACTORY.bind(IC_TYPE, new Impl());
//        assertEquals("iC", ic.iC());
//    }
//
//    @Test
//    public void testInterfaces() {
//        IC ic = FACTORY.bind(IC_TYPE, new Impl());
//        assertAll(
//                () -> assertEquals("iA", ic.iA()),
//                () -> assertEquals("iB", ic.iB()),
//                () -> assertEquals("iC", ic.iC())
//        );
//    }
//
//    @Test
//    public void testOverridable() {
//        IC ic = FACTORY.bind(IC_TYPE, new OverridableImpl());
//        assertEquals("dIA", ic.dIA());
//    }
//
//    @Test
//    public void testTypes() {
//        Types types = FACTORY.bind(TYPES_TYPE, new TypesImpl());
//        assertAll(
//                () -> assertEquals(0, types.i(0)),
//                () -> assertEquals(1, types.s((short) 1)),
//                () -> assertEquals(2, types.b((byte) 2)),
//                () -> assertEquals('z', types.c('z')),
//                () -> assertTrue(types.bool(true)),
//                () -> assertEquals(11111111110L, types.l(11111111110L)),
//                () -> assertEquals(0.5, types.f((float) 0.5)),
//                () -> assertEquals(0.0005, types.d(0.0005)),
//                () -> assertEquals(1, types.v()),
//                () -> assertEquals("zov", types.o("zov")),
//                () -> assertEquals(5, types.oA(new Object[]{1, 2, 3, 4, 5}).length),
//                () -> assertEquals(2, types.ia(new int[]{2})[0]),
//                () -> assertEquals('z', types.ca(new char[]{'z'})[0]),
//                () -> assertTrue(types.ba(new boolean[]{true})[0])
//        );
//    }
//
//    public interface Types {
//        int i(int a);
//
//        short s(short a);
//
//        byte b(byte a);
//
//        char c(char a);
//
//        boolean bool(boolean a);
//
//        long l(long a);
//
//        float f(float a);
//
//        double d(double a);
//
//        int v();
//
//        Object o(Object a);
//
//        Object[] oA(Object[] a);
//
//        int[] ia(int[] a);
//
//        char[] ca(char[] a);
//
//        boolean[] ba(boolean[] a);
//    }
//
//    public interface IA {
//        String iA();
//
//        @Overridable
//        default String dIA() {
//            return "dIA";
//        }
//    }
//
//    public interface IB extends IA {
//        String iB();
//    }
//
//    public interface IC extends IB {
//        String iC();
//    }
//
//    public static class TypesImpl {
//
//        public int i(int a) {
//            return a;
//        }
//
//        public short s(short a) {
//            return a;
//        }
//
//        public byte b(byte a) {
//            return a;
//        }
//
//        public char c(char a) {
//            return a;
//        }
//
//        public boolean bool(boolean a) {
//            return a;
//        }
//
//        public long l(long a) {
//            return a;
//        }
//
//        public float f(float a) {
//            return a;
//        }
//
//        public double d(double a) {
//            return a;
//        }
//
//        public int v() {
//            return 1;
//        }
//
//        public Object o(Object a) {
//            return a;
//        }
//
//        public Object[] oA(Object[] a) {
//            return a;
//        }
//
//        public int[] ia(int[] a) {
//            return a;
//        }
//
//        public char[] ca(char[] a) {
//            return a;
//        }
//
//        public boolean[] ba(boolean[] a) {
//            return a;
//        }
//    }
//
//    public static class Impl {
//
//        public String iA() {
//            return "iA";
//        }
//
//        public String iB() {
//            return "iB";
//        }
//
//        public String iC() {
//            return "iC";
//        }
//    }
//
//    public static class OverridableImpl extends Impl {
//
//        @BindName("dIA")
//        public String test() {
//            return "dIA";
//        }
//    }
//}
