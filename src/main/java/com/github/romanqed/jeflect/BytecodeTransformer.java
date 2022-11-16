package com.github.romanqed.jeflect;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;


public final class BytecodeTransformer extends CheckedTransformer {
    private final int readerOptions;
    private final int writerOptions;
    private final Map<String, Function<ClassVisitor, ClassVisitor>> providers;

    public BytecodeTransformer(int readerOptions,
                               int writerOptions,
                               Map<String, Function<ClassVisitor, ClassVisitor>> providers) {
        this.readerOptions = readerOptions;
        this.writerOptions = writerOptions;
        this.providers = Objects.requireNonNull(providers);
    }

    private static <K, V> Map<K, V> toMap(K key, V value) {
        Map<K, V> ret = new HashMap<>();
        ret.put(key, value);
        return ret;
    }

    public BytecodeTransformer(String className, Function<ClassVisitor, ClassVisitor> provider) {
        this(ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES,
                ClassWriter.COMPUTE_FRAMES,
                toMap(className, provider));
    }

    @Override
    protected byte[] checkedTransform(ClassLoader loader,
                                      String className,
                                      Class<?> classBeingRedefined,
                                      ProtectionDomain protectionDomain,
                                      byte[] classfileBuffer) {
        Function<ClassVisitor, ClassVisitor> provider = providers.get(className);
        if (provider == null) {
            return classfileBuffer;
        }
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, writerOptions);
        ClassVisitor visitor = provider.apply(writer);
        reader.accept(visitor, readerOptions);
        return writer.toByteArray();
    }
}
