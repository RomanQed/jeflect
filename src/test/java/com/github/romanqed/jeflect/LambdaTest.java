package com.github.romanqed.jeflect;

import com.github.romanqed.jeflect.lambdas.Lambda;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class LambdaTest extends Assertions {
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
                () -> assertEquals(I_S, lsm.call(args)),
                () -> assertEquals(I_DV, ldm.call(args)),
                () -> assertEquals(I_V, lvm.call(args))
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
                () -> assertEquals(C_S, lsm.call(args)),
                () -> assertEquals(C_V, lvm.call(args)),
                () -> assertEquals(C_VF, lfm.call(args))
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
                () -> assertEquals(AC_S, lsm.call(args)),
                () -> assertEquals(AC_V, lvm.call(args)),
                () -> assertEquals(AC_VF, lfm.call(args)),
                () -> assertEquals(AC_VA, lam.call(args))
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
                () -> assertEquals(Boolean.FALSE, lBool.call(new Object[]{Boolean.FALSE})),
                () -> assertEquals('.', lChar.call(new Object[]{'.'})),
                () -> assertEquals(Byte.MIN_VALUE, lByte.call(new Object[]{Byte.MIN_VALUE})),
                () -> assertEquals(Short.MIN_VALUE, lShort.call(new Object[]{Short.MIN_VALUE})),
                () -> assertEquals(Integer.MIN_VALUE, lInt.call(new Object[]{Integer.MIN_VALUE})),
                () -> assertEquals(Float.MIN_VALUE, lFloat.call(new Object[]{Float.MIN_VALUE})),
                () -> assertEquals(Long.MIN_VALUE, lLong.call(new Object[]{Long.MIN_VALUE})),
                () -> assertEquals(Double.MIN_VALUE, lDouble.call(new Object[]{Double.MIN_VALUE})),
                () -> assertEquals(array, lArray.call(new Object[]{array}))
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
                () -> assertEquals(1, one.call(new Object[]{"1", new Character[0]})),
                () -> assertEquals(21, two.call(new Object[]{5, 6})),
                () -> assertEquals("message", three.call(new Object[]{"messag", 'e'}))
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

    @Test
    public void testVarArgs() throws Throwable {
        Method method = VarArgs.class.getDeclaredMethod("sum", int[].class);
        // Pack
        Lambda sum = ReflectUtil.packMethod(method);
        // Test
        assertEquals(6, sum.call(new Object[]{new int[]{1, 2, 3}}));
    }

    @Test
    public void testVeryLongMethod() throws Throwable {
        Method method = Arrays.stream(VeryLongMethod.class.getDeclaredMethods()).
                filter(e -> e.getName().equals("longMethod")).
                findFirst().
                orElse(null);
        // Pack
        Lambda longMethod = ReflectUtil.packMethod(method);
        // Test
        Object[] args = new Object[255];
        int sum = 0;
        for (int i = 0; i < 255; ++i) {
            sum += i;
            args[i] = i;
        }
        assertEquals(sum, longMethod.call(args));
    }

    public interface Interface {
        static int sm() {
            return LambdaTest.I_S;
        }

        default int dm() {
            return LambdaTest.I_DV;
        }

        int vm();
    }

    public static class VeryLongMethod {
        public static int longMethod(int arg0,
                                     int arg1,
                                     int arg2,
                                     int arg3,
                                     int arg4,
                                     int arg5,
                                     int arg6,
                                     int arg7,
                                     int arg8,
                                     int arg9,
                                     int arg10,
                                     int arg11,
                                     int arg12,
                                     int arg13,
                                     int arg14,
                                     int arg15,
                                     int arg16,
                                     int arg17,
                                     int arg18,
                                     int arg19,
                                     int arg20,
                                     int arg21,
                                     int arg22,
                                     int arg23,
                                     int arg24,
                                     int arg25,
                                     int arg26,
                                     int arg27,
                                     int arg28,
                                     int arg29,
                                     int arg30,
                                     int arg31,
                                     int arg32,
                                     int arg33,
                                     int arg34,
                                     int arg35,
                                     int arg36,
                                     int arg37,
                                     int arg38,
                                     int arg39,
                                     int arg40,
                                     int arg41,
                                     int arg42,
                                     int arg43,
                                     int arg44,
                                     int arg45,
                                     int arg46,
                                     int arg47,
                                     int arg48,
                                     int arg49,
                                     int arg50,
                                     int arg51,
                                     int arg52,
                                     int arg53,
                                     int arg54,
                                     int arg55,
                                     int arg56,
                                     int arg57,
                                     int arg58,
                                     int arg59,
                                     int arg60,
                                     int arg61,
                                     int arg62,
                                     int arg63,
                                     int arg64,
                                     int arg65,
                                     int arg66,
                                     int arg67,
                                     int arg68,
                                     int arg69,
                                     int arg70,
                                     int arg71,
                                     int arg72,
                                     int arg73,
                                     int arg74,
                                     int arg75,
                                     int arg76,
                                     int arg77,
                                     int arg78,
                                     int arg79,
                                     int arg80,
                                     int arg81,
                                     int arg82,
                                     int arg83,
                                     int arg84,
                                     int arg85,
                                     int arg86,
                                     int arg87,
                                     int arg88,
                                     int arg89,
                                     int arg90,
                                     int arg91,
                                     int arg92,
                                     int arg93,
                                     int arg94,
                                     int arg95,
                                     int arg96,
                                     int arg97,
                                     int arg98,
                                     int arg99,
                                     int arg100,
                                     int arg101,
                                     int arg102,
                                     int arg103,
                                     int arg104,
                                     int arg105,
                                     int arg106,
                                     int arg107,
                                     int arg108,
                                     int arg109,
                                     int arg110,
                                     int arg111,
                                     int arg112,
                                     int arg113,
                                     int arg114,
                                     int arg115,
                                     int arg116,
                                     int arg117,
                                     int arg118,
                                     int arg119,
                                     int arg120,
                                     int arg121,
                                     int arg122,
                                     int arg123,
                                     int arg124,
                                     int arg125,
                                     int arg126,
                                     int arg127,
                                     int arg128,
                                     int arg129,
                                     int arg130,
                                     int arg131,
                                     int arg132,
                                     int arg133,
                                     int arg134,
                                     int arg135,
                                     int arg136,
                                     int arg137,
                                     int arg138,
                                     int arg139,
                                     int arg140,
                                     int arg141,
                                     int arg142,
                                     int arg143,
                                     int arg144,
                                     int arg145,
                                     int arg146,
                                     int arg147,
                                     int arg148,
                                     int arg149,
                                     int arg150,
                                     int arg151,
                                     int arg152,
                                     int arg153,
                                     int arg154,
                                     int arg155,
                                     int arg156,
                                     int arg157,
                                     int arg158,
                                     int arg159,
                                     int arg160,
                                     int arg161,
                                     int arg162,
                                     int arg163,
                                     int arg164,
                                     int arg165,
                                     int arg166,
                                     int arg167,
                                     int arg168,
                                     int arg169,
                                     int arg170,
                                     int arg171,
                                     int arg172,
                                     int arg173,
                                     int arg174,
                                     int arg175,
                                     int arg176,
                                     int arg177,
                                     int arg178,
                                     int arg179,
                                     int arg180,
                                     int arg181,
                                     int arg182,
                                     int arg183,
                                     int arg184,
                                     int arg185,
                                     int arg186,
                                     int arg187,
                                     int arg188,
                                     int arg189,
                                     int arg190,
                                     int arg191,
                                     int arg192,
                                     int arg193,
                                     int arg194,
                                     int arg195,
                                     int arg196,
                                     int arg197,
                                     int arg198,
                                     int arg199,
                                     int arg200,
                                     int arg201,
                                     int arg202,
                                     int arg203,
                                     int arg204,
                                     int arg205,
                                     int arg206,
                                     int arg207,
                                     int arg208,
                                     int arg209,
                                     int arg210,
                                     int arg211,
                                     int arg212,
                                     int arg213,
                                     int arg214,
                                     int arg215,
                                     int arg216,
                                     int arg217,
                                     int arg218,
                                     int arg219,
                                     int arg220,
                                     int arg221,
                                     int arg222,
                                     int arg223,
                                     int arg224,
                                     int arg225,
                                     int arg226,
                                     int arg227,
                                     int arg228,
                                     int arg229,
                                     int arg230,
                                     int arg231,
                                     int arg232,
                                     int arg233,
                                     int arg234,
                                     int arg235,
                                     int arg236,
                                     int arg237,
                                     int arg238,
                                     int arg239,
                                     int arg240,
                                     int arg241,
                                     int arg242,
                                     int arg243,
                                     int arg244,
                                     int arg245,
                                     int arg246,
                                     int arg247,
                                     int arg248,
                                     int arg249,
                                     int arg250,
                                     int arg251,
                                     int arg252,
                                     int arg253,
                                     int arg254) {
            return arg0 + arg1
                    + arg2
                    + arg3
                    + arg4
                    + arg5
                    + arg6
                    + arg7
                    + arg8
                    + arg9
                    + arg10
                    + arg11
                    + arg12
                    + arg13
                    + arg14
                    + arg15
                    + arg16
                    + arg17
                    + arg18
                    + arg19
                    + arg20
                    + arg21
                    + arg22
                    + arg23
                    + arg24
                    + arg25
                    + arg26
                    + arg27
                    + arg28
                    + arg29
                    + arg30
                    + arg31
                    + arg32
                    + arg33
                    + arg34
                    + arg35
                    + arg36
                    + arg37
                    + arg38
                    + arg39
                    + arg40
                    + arg41
                    + arg42
                    + arg43
                    + arg44
                    + arg45
                    + arg46
                    + arg47
                    + arg48
                    + arg49
                    + arg50
                    + arg51
                    + arg52
                    + arg53
                    + arg54
                    + arg55
                    + arg56
                    + arg57
                    + arg58
                    + arg59
                    + arg60
                    + arg61
                    + arg62
                    + arg63
                    + arg64
                    + arg65
                    + arg66
                    + arg67
                    + arg68
                    + arg69
                    + arg70
                    + arg71
                    + arg72
                    + arg73
                    + arg74
                    + arg75
                    + arg76
                    + arg77
                    + arg78
                    + arg79
                    + arg80
                    + arg81
                    + arg82
                    + arg83
                    + arg84
                    + arg85
                    + arg86
                    + arg87
                    + arg88
                    + arg89
                    + arg90
                    + arg91
                    + arg92
                    + arg93
                    + arg94
                    + arg95
                    + arg96
                    + arg97
                    + arg98
                    + arg99
                    + arg100
                    + arg101
                    + arg102
                    + arg103
                    + arg104
                    + arg105
                    + arg106
                    + arg107
                    + arg108
                    + arg109
                    + arg110
                    + arg111
                    + arg112
                    + arg113
                    + arg114
                    + arg115
                    + arg116
                    + arg117
                    + arg118
                    + arg119
                    + arg120
                    + arg121
                    + arg122
                    + arg123
                    + arg124
                    + arg125
                    + arg126
                    + arg127
                    + arg128
                    + arg129
                    + arg130
                    + arg131
                    + arg132
                    + arg133
                    + arg134
                    + arg135
                    + arg136
                    + arg137
                    + arg138
                    + arg139
                    + arg140
                    + arg141
                    + arg142
                    + arg143
                    + arg144
                    + arg145
                    + arg146
                    + arg147
                    + arg148
                    + arg149
                    + arg150
                    + arg151
                    + arg152
                    + arg153
                    + arg154
                    + arg155
                    + arg156
                    + arg157
                    + arg158
                    + arg159
                    + arg160
                    + arg161
                    + arg162
                    + arg163
                    + arg164
                    + arg165
                    + arg166
                    + arg167
                    + arg168
                    + arg169
                    + arg170
                    + arg171
                    + arg172
                    + arg173
                    + arg174
                    + arg175
                    + arg176
                    + arg177
                    + arg178
                    + arg179
                    + arg180
                    + arg181
                    + arg182
                    + arg183
                    + arg184
                    + arg185
                    + arg186
                    + arg187
                    + arg188
                    + arg189
                    + arg190
                    + arg191
                    + arg192
                    + arg193
                    + arg194
                    + arg195
                    + arg196
                    + arg197
                    + arg198
                    + arg199
                    + arg200
                    + arg201
                    + arg202
                    + arg203
                    + arg204
                    + arg205
                    + arg206
                    + arg207
                    + arg208
                    + arg209
                    + arg210
                    + arg211
                    + arg212
                    + arg213
                    + arg214
                    + arg215
                    + arg216
                    + arg217
                    + arg218
                    + arg219
                    + arg220
                    + arg221
                    + arg222
                    + arg223
                    + arg224
                    + arg225
                    + arg226
                    + arg227
                    + arg228
                    + arg229
                    + arg230
                    + arg231
                    + arg232
                    + arg233
                    + arg234
                    + arg235
                    + arg236
                    + arg237
                    + arg238
                    + arg239
                    + arg240
                    + arg241
                    + arg242
                    + arg243
                    + arg244
                    + arg245
                    + arg246
                    + arg247
                    + arg248
                    + arg249
                    + arg250
                    + arg251
                    + arg252
                    + arg253
                    + arg254;
        }
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
            return LambdaTest.I_V;
        }
    }

    public static class Class {
        public static int sm() {
            return LambdaTest.C_S;
        }

        public int vm() {
            return LambdaTest.C_V;
        }

        public final int vfm() {
            return LambdaTest.C_VF;
        }
    }

    public static abstract class AbstractClass {
        public static int sm() {
            return LambdaTest.AC_S;
        }

        public int vm() {
            return LambdaTest.AC_V;
        }

        public final int vfm() {
            return LambdaTest.AC_VF;
        }

        public abstract int vam();
    }

    static class ClassImpl extends AbstractClass {

        @Override
        public int vam() {
            return LambdaTest.AC_VA;
        }
    }
}
