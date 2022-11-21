package com.github.romanqed.jeflect.access;

import com.github.romanqed.jeflect.ByteClass;
import com.github.romanqed.jeflect.ByteField;
import com.github.romanqed.jeflect.ByteMethod;
import com.github.romanqed.jeflect.parsers.AsmClassFileParser;
import com.github.romanqed.jeflect.parsers.ClassFileParser;
import com.github.romanqed.jeflect.transformers.RedefineTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Implementation of {@link Accessor} that uses {@link Instrumentation} and {@link ClassFileTransformer}.
 */
public final class TransformAccessor implements Accessor {
    private static final int OPTIONS = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES | ClassReader.SKIP_CODE;
    private final Instrumentation instrumentation;
    private final ClassFileParser parser;

    public TransformAccessor(Instrumentation instrumentation, ClassFileParser parser) {
        this.instrumentation = Objects.requireNonNull(instrumentation);
        this.parser = Objects.requireNonNull(parser);
    }

    public TransformAccessor(Instrumentation instrumentation) {
        this(instrumentation, new AsmClassFileParser());
    }

    @Override
    public void setAccess(Runnable loader, BiConsumer<AccessModifier, ByteClass> consumer) {
        Transformer transformer = new Transformer(parser, consumer);
        instrumentation.addTransformer(transformer, true);
        try {
            loader.run();
        } catch (Throwable e) {
            throw new IllegalStateException("Cannot setAccess class due to", e);
        } finally {
            instrumentation.removeTransformer(transformer);
        }
        transformer.validate();
    }

    private static final class Transformer extends RedefineTransformer {
        private final BiConsumer<AccessModifier, ByteClass> consumer;

        private Transformer(ClassFileParser parser, BiConsumer<AccessModifier, ByteClass> consumer) {
            super(parser);
            this.consumer = consumer;
        }

        @Override
        protected byte[] transform(ClassLoader loader,
                                   ByteClass byteClass,
                                   ProtectionDomain domain,
                                   byte[] classFileBuffer) {
            Modifier modifier = new Modifier();
            consumer.accept(modifier, byteClass);
            Function<ClassVisitor, ModifyVisitor> provider = modifier.provider;
            if (provider == null) {
                return classFileBuffer;
            }
            ClassReader reader = new ClassReader(classFileBuffer);
            ClassWriter writer = new ClassWriter(reader, 0);
            ClassVisitor visitor = provider.apply(writer);
            reader.accept(visitor, OPTIONS);
            return writer.toByteArray();
        }
    }

    private static final class Modifier implements AccessModifier {
        private Function<ClassVisitor, ModifyVisitor> provider;

        @Override
        public synchronized void setAccess(ByteClass clazz, int access) {
            provider = e -> new ModifyVisitor(e, clazz.getName(), access);
        }

        private void modify(ByteMethod method, Function<ClassVisitor, ModifyVisitor> provider, int access) {
            this.provider = e -> {
                ModifyVisitor visitor = provider.apply(e);
                visitor.addMethod(method, access);
                return visitor;
            };
        }

        @Override
        public synchronized void setAccess(ByteMethod method, int access) {
            if (provider == null) {
                setAccess(method.getDeclaringClass(), 0);
            }
            modify(method, provider, access);
        }

        private void modify(ByteField field, Function<ClassVisitor, ModifyVisitor> provider, int access) {
            this.provider = e -> {
                ModifyVisitor visitor = provider.apply(e);
                visitor.addField(field, access);
                return visitor;
            };
        }

        @Override
        public synchronized void setAccess(ByteField field, int access) {
            if (provider == null) {
                setAccess(field.getDeclaringClass(), 0);
            }
            modify(field, provider, access);
        }
    }
}
