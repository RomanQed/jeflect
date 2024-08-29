package com.github.romanqed.jeflect.meta;

import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class MetaLambdaTest {
    static final MetaLambdaFactory FACTORY = new LookupMetaFactory(MethodHandles.lookup());
    static final LambdaType<Caller> CALLER = LambdaType.of(Caller.class);
    static final LambdaType<FreeCaller> FREE_CALLER = LambdaType.of(FreeCaller.class);
    static final int S = 0;
    static final int V = 1;

    @Test
    public void testStatic() throws Throwable {
        var sm = Class.class.getDeclaredMethod("sm");
        var caller = FACTORY.packLambdaMethod(CALLER, sm);
        assertEquals(S, caller.call());
    }

    @Test
    public void testVirtual() throws Throwable {
        var vm = Class.class.getDeclaredMethod("vm");
        var caller = FACTORY.packLambdaMethod(FREE_CALLER, vm);
        assertEquals(V, caller.call(new Class()));
    }

    @Test
    public void testBoundVirtual() throws Throwable {
        var vm = Class.class.getDeclaredMethod("vm");
        var caller = FACTORY.packLambdaMethod(CALLER, vm, new Class());
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
