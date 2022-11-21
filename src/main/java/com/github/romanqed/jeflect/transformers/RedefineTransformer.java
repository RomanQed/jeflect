package com.github.romanqed.jeflect.transformers;

import com.github.romanqed.jeflect.ByteClass;
import com.github.romanqed.jeflect.parsers.AsmClassFileParser;
import com.github.romanqed.jeflect.parsers.ClassFileParser;

import java.security.ProtectionDomain;

public abstract class RedefineTransformer extends CheckedTransformer {
    private final ClassFileParser parser;

    protected RedefineTransformer(ClassFileParser parser) {
        this.parser = parser;
    }

    protected RedefineTransformer() {
        this(new AsmClassFileParser());
    }

    protected abstract byte[] transform(ClassLoader loader,
                                        ByteClass byteClass,
                                        ProtectionDomain domain,
                                        byte[] classFileBuffer) throws Throwable;

    @Override
    protected byte[] checkedTransform(ClassLoader loader,
                                      String className,
                                      Class<?> classBeingRedefined,
                                      ProtectionDomain protectionDomain,
                                      byte[] classfileBuffer) throws Throwable {
        if (classBeingRedefined != null) {
            throw new IllegalStateException("It is not possible to change the class because it is already loaded");
        }
        ByteClass byteClass = parser.parse(classfileBuffer);
        return transform(loader, byteClass, protectionDomain, classfileBuffer);
    }
}
