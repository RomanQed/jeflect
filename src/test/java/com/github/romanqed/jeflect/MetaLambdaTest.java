package com.github.romanqed.jeflect;

import com.github.romanqed.jeflect.meta.LambdaType;
import com.github.romanqed.jeflect.meta.MetaFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;

public class MetaLambdaTest extends Assertions {
    static final int S = 0;
    static final int V = 1;
    static final MetaFactory FACTORY = new MetaFactory(MethodHandles.lookup());
    static final LambdaType<Caller> CALLER = LambdaType.fromClass(Caller.class);
    static final LambdaType<FreeCaller> FREE_CALLER = LambdaType.fromClass(FreeCaller.class);

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
