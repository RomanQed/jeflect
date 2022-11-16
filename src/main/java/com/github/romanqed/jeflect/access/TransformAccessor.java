package com.github.romanqed.jeflect.access;

import com.github.romanqed.jeflect.BytecodeTransformer;
import com.github.romanqed.jeflect.Main;
import org.objectweb.asm.ClassVisitor;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Function;

public class TransformAccessor implements Accessor {
    private final Instrumentation instrumentation;

    public TransformAccessor(Instrumentation instrumentation) {
        this.instrumentation = Objects.requireNonNull(instrumentation);
    }

    private void modify(Class<?> clazz, Function<ClassVisitor, ClassVisitor> provider) {
//        String internal = clazz.getName().replace('.', '/');
        BytecodeTransformer transformer = new BytecodeTransformer("com/github/romanqed/jeflect/Main$Test", provider);
        instrumentation.addTransformer(transformer, true);
        try {
            instrumentation.retransformClasses(Main.Test.class);
        } catch (Throwable e) {
            throw new IllegalStateException("Cannot modify class due to", e);
        } finally {
            instrumentation.removeTransformer(transformer);
        }
        transformer.validate();
    }

    @Override
    public void modifyAccess(Class<?> clazz) {
        modify(clazz, AccessModifier::new);
    }

    @Override
    public void modifyAccess(Field field) {

    }

    @Override
    public void modifyAccess(Executable method) {

    }
}
