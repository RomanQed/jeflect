package com.github.romanqed.jeflect;

import com.github.romanqed.jeflect.legacy.ReflectUtil;
import com.github.romanqed.jeflect.legacy.meta.LambdaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

public class MetaLambdaTest extends Assertions {
    static final int S = 0;
    static final int V = 1;
    static final LambdaType<Caller> CALLER = LambdaType.fromClass(Caller.class);
    static final LambdaType<FreeCaller> FREE_CALLER = LambdaType.fromClass(FreeCaller.class);

    @Test
    public void testStatic() throws Throwable {
        Method sm = Class.class.getDeclaredMethod("sm");
        Caller caller = ReflectUtil.packLambdaMethod(CALLER, sm);
        assertEquals(S, caller.call());
    }

    @Test
    public void testVirtual() throws Throwable {
        Method vm = Class.class.getDeclaredMethod("vm");
        FreeCaller caller = ReflectUtil.packLambdaMethod(FREE_CALLER, vm);
        assertEquals(V, caller.call(new Class()));
    }

    @Test
    public void testBoundVirtual() throws Throwable {
        Method vm = Class.class.getDeclaredMethod("vm");
        Caller caller = ReflectUtil.packLambdaMethod(CALLER, vm, new Class());
        assertEquals(V, caller.call());
    }

    public interface Caller {
        int call();
    }

    public interface FreeCaller {
        int call(Class cls);
    }

    public static class Class {
        public static int sm() {
            return S;
        }

        public int vm() {
            return V;
        }
    }
}
