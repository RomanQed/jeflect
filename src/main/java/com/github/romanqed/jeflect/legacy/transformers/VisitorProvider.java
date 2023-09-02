package com.github.romanqed.jeflect.legacy.transformers;

import org.objectweb.asm.ClassVisitor;

import java.util.function.Function;

@FunctionalInterface
public interface VisitorProvider {
    Function<ClassVisitor, ClassVisitor> get(String className);
}
