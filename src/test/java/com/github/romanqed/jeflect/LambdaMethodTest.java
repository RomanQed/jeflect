package com.github.romanqed.jeflect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;

public class LambdaMethodTest extends Assertions {
    // Interface codes
    static final int I_S = 1;
    static final int I_DV = 2;
    static final int I_V = 3;
    // Class codes
    static final int C_S = 4;
    static final int C_V = 5;
    static final int C_VF = 6;
    // Abstract class codes
    static final int AC_S = 7;
    static final int AC_V = 8;
    static final int AC_VF = 9;
    static final int AC_VA = 10;

    @Test
    public void testInterface() throws Throwable {
        Interface i = new InterfaceImpl();
        Object[] args = new Object[0];
        Method sm = Interface.class.getDeclaredMethod("sm");
        Method dm = Interface.class.getDeclaredMethod("dm");
        Method vm = Interface.class.getDeclaredMethod("vm");
        // Pack
        LambdaMethod lsm = ReflectUtil.packLambdaMethod(sm);
        LambdaMethod ldm = ReflectUtil.packLambdaMethod(dm);
        LambdaMethod lvm = ReflectUtil.packLambdaMethod(vm);
        assertAll(
                () -> assertEquals(lsm.call(args), I_S),
                () -> assertEquals(ldm.call(i, args), I_DV),
                () -> assertEquals(lvm.call(i, args), I_V)
        );
    }

    @Test
    public void testClass() throws Throwable {
        Class c = new Class();
        Object[] args = new Object[0];
        Method sm = Class.class.getDeclaredMethod("sm");
        Method vm = Class.class.getDeclaredMethod("vm");
        Method vfm = Class.class.getDeclaredMethod("vfm");
        // Pack
        LambdaMethod lsm = ReflectUtil.packLambdaMethod(sm);
        LambdaMethod lvm = ReflectUtil.packLambdaMethod(vm);
        LambdaMethod lfm = ReflectUtil.packLambdaMethod(vfm);
        assertAll(
                () -> assertEquals(lsm.call(args), C_S),
                () -> assertEquals(lvm.call(c, args), C_V),
                () -> assertEquals(lfm.call(c, args), C_VF)
        );
    }

    @Test
    public void testAbstractClass() throws Throwable {
        AbstractClass c = new ClassImpl();
        Object[] args = new Object[0];
        Method sm = AbstractClass.class.getDeclaredMethod("sm");
        Method vm = AbstractClass.class.getDeclaredMethod("vm");
        Method vfm = AbstractClass.class.getDeclaredMethod("vfm");
        Method vam = AbstractClass.class.getDeclaredMethod("vam");
        // Pack
        LambdaMethod lsm = ReflectUtil.packLambdaMethod(sm);
        LambdaMethod lvm = ReflectUtil.packLambdaMethod(vm);
        LambdaMethod lfm = ReflectUtil.packLambdaMethod(vfm);
        LambdaMethod lam = ReflectUtil.packLambdaMethod(vam);
        assertAll(
                () -> assertEquals(lsm.call(args), AC_S),
                () -> assertEquals(lvm.call(c, args), AC_V),
                () -> assertEquals(lfm.call(c, args), AC_VF),
                () -> assertEquals(lam.call(c, args), AC_VA)
        );
    }

    @Test
    public void testCommon() throws Throwable {
        Method bool = Common.class.getDeclaredMethod("getBool", boolean.class);
        Method chr = Common.class.getDeclaredMethod("getChar", char.class);
        Method bt = Common.class.getDeclaredMethod("getByte", byte.class);
        Method sh = Common.class.getDeclaredMethod("getShort", short.class);
        Method it = Common.class.getDeclaredMethod("getInt", int.class);
        Method flt = Common.class.getDeclaredMethod("getFloat", float.class);
        Method lng = Common.class.getDeclaredMethod("getLong", long.class);
        Method dbl = Common.class.getDeclaredMethod("getDouble", double.class);
        Method arr = Common.class.getDeclaredMethod("getArray", Object[].class);
        // Pack
        LambdaMethod lBool = ReflectUtil.packLambdaMethod(bool);
        LambdaMethod lChar = ReflectUtil.packLambdaMethod(chr);
        LambdaMethod lByte = ReflectUtil.packLambdaMethod(bt);
        LambdaMethod lShort = ReflectUtil.packLambdaMethod(sh);
        LambdaMethod lInt = ReflectUtil.packLambdaMethod(it);
        LambdaMethod lFloat = ReflectUtil.packLambdaMethod(flt);
        LambdaMethod lLong = ReflectUtil.packLambdaMethod(lng);
        LambdaMethod lDouble = ReflectUtil.packLambdaMethod(dbl);
        LambdaMethod lArray = ReflectUtil.packLambdaMethod(arr);
        // Test
        String[] array = new String[]{"a", "b", "c"};
        assertAll(
                () -> assertEquals(lBool.call(new Object[]{Boolean.FALSE}), Boolean.FALSE),
                () -> assertEquals(lChar.call(new Object[]{'.'}), '.'),
                () -> assertEquals(lByte.call(new Object[]{Byte.MIN_VALUE}), Byte.MIN_VALUE),
                () -> assertEquals(lShort.call(new Object[]{Short.MIN_VALUE}), Short.MIN_VALUE),
                () -> assertEquals(lInt.call(new Object[]{Integer.MIN_VALUE}), Integer.MIN_VALUE),
                () -> assertEquals(lFloat.call(new Object[]{Float.MIN_VALUE}), Float.MIN_VALUE),
                () -> assertEquals(lLong.call(new Object[]{Long.MIN_VALUE}), Long.MIN_VALUE),
                () -> assertEquals(lDouble.call(new Object[]{Double.MIN_VALUE}), Double.MIN_VALUE),
                () -> assertEquals(lArray.call(new Object[]{array}), array)
        );
    }

    @Test
    public void testPairs() throws Throwable {
        Pairs p = new Pairs(10);
        Method oneM = Pairs.class.getDeclaredMethod("one", String.class, Character[].class);
        Method twoM = Pairs.class.getDeclaredMethod("two", int.class, Integer.class);
        Method threeM = Pairs.class.getDeclaredMethod("three", String.class, char.class);
        // Pack
        LambdaMethod one = ReflectUtil.packLambdaMethod(oneM);
        LambdaMethod two = ReflectUtil.packLambdaMethod(twoM);
        LambdaMethod three = ReflectUtil.packLambdaMethod(threeM);
        // Test
        assertAll(
                () -> assertEquals(one.call(new Object[]{"1", new Character[0]}), 1),
                () -> assertEquals(two.call(p, new Object[]{5, 6}), 21),
                () -> assertEquals(three.call(new Object[]{"messag", 'e'}), "message")
        );
    }

    @Test
    public void testException() throws Throwable {
        Method method = ExceptClass.class.getDeclaredMethod("throwsException");
        // Pack
        LambdaMethod except = ReflectUtil.packLambdaMethod(method);
        // Test
        assertThrows(IOException.class, () -> except.call(new Object[0]));
    }

    @Test
    public void testVarArgs() throws Throwable {
        Method method = VarArgs.class.getDeclaredMethod("sum", int[].class);
        // Pack
        LambdaMethod sum = ReflectUtil.packLambdaMethod(method);
        // Test
        assertEquals(sum.call(new Object[]{new int[]{1, 2, 3}}), 6);
    }

    public interface Interface {
        static int sm() {
            return I_S;
        }

        default int dm() {
            return I_DV;
        }

        int vm();
    }

    public static class VarArgs {
        public static int sum(int... numbers) {
            int ret = 0;
            for (int number : numbers) {
                ret += number;
            }
            return ret;
        }
    }

    public static class ExceptClass {
        public static void throwsException() throws IOException {
            // Oh no, EXCEPTION!
            throw new IOException();
        }
    }

    public static class Pairs {
        private final int add;

        public Pairs(int add) {
            this.add = add;
        }

        public static int one(String str, Character[] chars) {
            return str.length() + chars.length;
        }

        public static String three(String message, char sym) {
            return message + sym;
        }

        public int two(int left, Integer right) {
            return left + right + add;
        }
    }

    public static class Common {
        public static boolean getBool(boolean a) {
            return a;
        }

        public static char getChar(char a) {
            return a;
        }

        public static byte getByte(byte a) {
            return a;
        }

        public static short getShort(short a) {
            return a;
        }

        public static int getInt(int a) {
            return a;
        }

        public static float getFloat(float a) {
            return a;
        }

        public static long getLong(long a) {
            return a;
        }

        public static double getDouble(double a) {
            return a;
        }

        public static <T> T[] getArray(T[] a) {
            return a;
        }
    }

    static class InterfaceImpl implements Interface {
        @Override
        public int vm() {
            return I_V;
        }
    }

    public static class Class {
        public static int sm() {
            return C_S;
        }

        public int vm() {
            return C_V;
        }

        public final int vfm() {
            return C_VF;
        }
    }

    public static abstract class AbstractClass {
        public static int sm() {
            return AC_S;
        }

        public int vm() {
            return AC_V;
        }

        public final int vfm() {
            return AC_VF;
        }

        public abstract int vam();
    }

    static class ClassImpl extends AbstractClass {

        @Override
        public int vam() {
            return AC_VA;
        }
    }
}
