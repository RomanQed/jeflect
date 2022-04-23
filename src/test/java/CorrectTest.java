import com.github.romanqed.jeflect.Lambda;
import com.github.romanqed.jeflect.ReflectUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;

public class CorrectTest extends Assertions {
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
        Lambda lsm = ReflectUtil.packMethod(sm);
        Lambda ldm = ReflectUtil.packMethod(dm, i);
        Lambda lvm = ReflectUtil.packMethod(vm, i);
        assertAll(
                () -> assertEquals(lsm.call(args), I_S),
                () -> assertEquals(ldm.call(args), I_DV),
                () -> assertEquals(lvm.call(args), I_V)
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
        Lambda lsm = ReflectUtil.packMethod(sm);
        Lambda lvm = ReflectUtil.packMethod(vm, c);
        Lambda lfm = ReflectUtil.packMethod(vfm, c);
        assertAll(
                () -> assertEquals(lsm.call(args), C_S),
                () -> assertEquals(lvm.call(args), C_V),
                () -> assertEquals(lfm.call(args), C_VF)
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
        Lambda lsm = ReflectUtil.packMethod(sm);
        Lambda lvm = ReflectUtil.packMethod(vm, c);
        Lambda lfm = ReflectUtil.packMethod(vfm, c);
        Lambda lam = ReflectUtil.packMethod(vam, c);
        assertAll(
                () -> assertEquals(lsm.call(args), AC_S),
                () -> assertEquals(lvm.call(args), AC_V),
                () -> assertEquals(lfm.call(args), AC_VF),
                () -> assertEquals(lam.call(args), AC_VA)
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
        Lambda lBool = ReflectUtil.packMethod(bool);
        Lambda lChar = ReflectUtil.packMethod(chr);
        Lambda lByte = ReflectUtil.packMethod(bt);
        Lambda lShort = ReflectUtil.packMethod(sh);
        Lambda lInt = ReflectUtil.packMethod(it);
        Lambda lFloat = ReflectUtil.packMethod(flt);
        Lambda lLong = ReflectUtil.packMethod(lng);
        Lambda lDouble = ReflectUtil.packMethod(dbl);
        Lambda lArray = ReflectUtil.packMethod(arr);
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
        Lambda one = ReflectUtil.packMethod(oneM);
        Lambda two = ReflectUtil.packMethod(twoM, p);
        Lambda three = ReflectUtil.packMethod(threeM);
        // Test
        assertAll(
                () -> assertEquals(one.call(new Object[]{"1", new Character[0]}), 1),
                () -> assertEquals(two.call(new Object[]{5, 6}), 21),
                () -> assertEquals(three.call(new Object[]{"messag", 'e'}), "message")
        );
    }

    @Test
    public void testException() throws Throwable {
        Method method = ExceptClass.class.getDeclaredMethod("throwsException");
        // Pack
        Lambda except = ReflectUtil.packMethod(method);
        // Test
        assertThrows(IOException.class, () -> except.call(new Object[0]));
    }

    public interface Interface {
        static int sm() {
            return CorrectTest.I_S;
        }

        default int dm() {
            return CorrectTest.I_DV;
        }

        int vm();
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
            return CorrectTest.I_V;
        }
    }

    public static class Class {
        public static int sm() {
            return CorrectTest.C_S;
        }

        public int vm() {
            return CorrectTest.C_V;
        }

        public final int vfm() {
            return CorrectTest.C_VF;
        }
    }

    public static abstract class AbstractClass {
        public static int sm() {
            return CorrectTest.AC_S;
        }

        public int vm() {
            return CorrectTest.AC_V;
        }

        public final int vfm() {
            return CorrectTest.AC_VF;
        }

        public abstract int vam();
    }

    static class ClassImpl extends AbstractClass {

        @Override
        public int vam() {
            return CorrectTest.AC_VA;
        }
    }
}
