package com.github.romanqed.jeflect;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.github.romanqed.jeflect.Constants.*;

public final class LambdaFactory {
    private static final Definer DEFINER = new DefineClassLoader();
    private static final CreatorFactory FACTORY = new CreatorFactory();

    private final Map<Class<?>, Class<?>> virtuals;
    private final Map<Class<?>, Class<?>> statics;

    public LambdaFactory() {
        virtuals = new ConcurrentHashMap<>();
        statics = new ConcurrentHashMap<>();
    }

    private Class<?> findClass(Class<?> owner, Method method) {
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        Class<?> ret = isStatic ? statics.get(owner) : virtuals.get(owner);
        if (ret != null) {
            return ret;
        }
        ret = createClass(owner, method, isStatic);
        if (isStatic) {
            statics.put(owner, ret);
        } else {
            virtuals.put(owner, ret);
        }
        return ret;
    }

    private Class<?> createClass(Class<?> owner, Method method, boolean isStatic) {
        String name = PROXY + (isStatic ? owner.hashCode() : method.hashCode());
        ClassWriter ret = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ret.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, name, null, OBJECT, INTERFACES);
        Type classType = Type.getType(owner);
        if (!isStatic) {
            ret.visitField(FIELD_ACCESS, FIELD_NAME, classType.getDescriptor(), null, null).visitEnd();
        }
        String descriptor = String.format("(%s)V", (isStatic ? "" : classType.getDescriptor()));
        Consumer<MethodVisitor> constructor = FACTORY.createConstructor(isStatic, classType);
        Consumer<MethodVisitor> methodCreator = FACTORY.createMethod(name, owner, method);
        ProxyCreator creator = new ProxyCreator(descriptor, constructor, methodCreator);
        creator.accept(ret);
        ret.visitEnd();
        byte[] bytes = ret.toByteArray();
        return DEFINER.define(name, bytes);
    }

    public <T> Lambda packMethod(Class<T> owner, Method method, T bind) throws
            InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!Modifier.isStatic(method.getModifiers()) && bind == null) {
            throw new IllegalArgumentException("Missing bind object for virtual method");
        }
        Class<?> found = findClass(owner, method);
        Constructor<?> constructor = found.getDeclaredConstructors()[0];
        if (bind != null) {
            return (Lambda) constructor.newInstance(bind);
        }
        return (Lambda) constructor.newInstance();
    }

    public Lambda packMethod(Class<?> owner, Method method) throws
            InvocationTargetException, InstantiationException, IllegalAccessException {
        return packMethod(owner, method, null);
    }
}
