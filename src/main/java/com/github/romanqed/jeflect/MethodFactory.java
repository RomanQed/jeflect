package com.github.romanqed.jeflect;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.github.romanqed.jeflect.Constants.LAMBDA_METHOD;
import static com.github.romanqed.jeflect.Constants.OBJECT;

public final class MethodFactory extends PackFactory {
    private static final MethodCreatorFactory FACTORY = new MethodCreatorFactory();
    private static final Map<Long, Class<?>> VIRTUALS = new ConcurrentHashMap<>();
    private static final Map<Long, Class<?>> STATICS = new ConcurrentHashMap<>();

    public MethodFactory(DefineLoader loader) {
        super(VIRTUALS, STATICS, loader);
    }

    public MethodFactory() {
        super(VIRTUALS, STATICS, new DefineClassLoader());
    }

    @Override
    protected byte[] createClass(String name, Class<?> owner, Method method, boolean isStatic) {
        ClassWriter ret = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ret.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, name, null, OBJECT, new String[]{LAMBDA_METHOD});
        Consumer<MethodVisitor> methodCreator = FACTORY.createMethod(owner, method);
        LambdaMethodCreator creator = new LambdaMethodCreator(methodCreator);
        creator.accept(ret);
        ret.visitEnd();
        return ret.toByteArray();
    }

    public LambdaMethod packMethod(Method method) throws
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Objects.requireNonNull(method);
        checkMethod(method);
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        Class<?> found = findClass(method, isStatic);
        Constructor<?> constructor = found.getDeclaredConstructors()[0];
        return (LambdaMethod) constructor.newInstance();
    }
}
