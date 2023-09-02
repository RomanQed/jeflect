package com.github.romanqed.jeflect.legacy.transformers;

import com.github.romanqed.jeflect.legacy.ByteClass;
import com.github.romanqed.jeflect.legacy.parsers.AsmClassFileParser;
import com.github.romanqed.jeflect.legacy.parsers.ClassFileParser;

import java.security.ProtectionDomain;

public abstract class RedefineTransformer extends CheckedTransformer {
    private static final String IMMUTABLE_PREFIX = "java";
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
        if (className != null && className.startsWith(IMMUTABLE_PREFIX)) {
            return classfileBuffer;
        }
        ByteClass byteClass = parser.parse(classfileBuffer);
        return transform(loader, byteClass, protectionDomain, classfileBuffer);
    }
}
