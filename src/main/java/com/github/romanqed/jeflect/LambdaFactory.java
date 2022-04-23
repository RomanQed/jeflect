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
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.github.romanqed.jeflect.Constants.*;

/**
 *
 */
public final class LambdaFactory {
    private static final CreatorFactory FACTORY = new CreatorFactory();
    private static final Map<Long, Class<?>> VIRTUALS;
    private static final Map<Long, Class<?>> STATICS;

    static {
        VIRTUALS = new ConcurrentHashMap<>();
        STATICS = new ConcurrentHashMap<>();
    }

    private final Definer definer;

    public LambdaFactory(Definer definer) {
        this.definer = Objects.requireNonNull(definer);
    }

    public LambdaFactory() {
        this(new DefineClassLoader());
    }

    private long combineHashes(int left, int right) {
        return left > right ? (right | (long) left << 32) : (left | (long) right << 32);
    }

    private void checkMethod(Method method, boolean isStatic) {
        int modifiers = method.getModifiers();
        // Check for class accessibility
        if (!Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
            throw new IllegalArgumentException("The class must be public");
        }
        // Check for method accessibility
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalArgumentException("The method must be public");
        }
        // Check for static state
        if (Modifier.isStatic(modifiers) != isStatic) {
            throw new IllegalArgumentException("Invalid method");
        }
    }

    private Class<?> findClass(Method method) {
        Class<?> owner = method.getDeclaringClass();
        long hash = combineHashes(owner.hashCode(), method.hashCode());
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        Class<?> ret = isStatic ? STATICS.get(hash) : VIRTUALS.get(hash);
        if (ret != null) {
            return ret;
        }
        ret = createClass(owner, method, isStatic);
        if (isStatic) {
            STATICS.put(hash, ret);
        } else {
            VIRTUALS.put(hash, ret);
        }
        return ret;
    }

    private Class<?> createClass(Class<?> owner, Method method, boolean isStatic) {
        long hash = combineHashes(owner.hashCode(), method.hashCode());
        String name = PROXY + hash;
        ClassWriter ret = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ret.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, name, null, OBJECT, INTERFACES);
        Type classType = Type.getType(owner);
        if (!isStatic) {
            ret.visitField(FIELD_ACCESS, FIELD_NAME, classType.getDescriptor(), null, null).visitEnd();
        }
        String descriptor = String.format("(%s)V", (isStatic ? "" : classType.getDescriptor()));
        Consumer<MethodVisitor> constructor = FACTORY.createConstructor(name, isStatic, classType);
        Consumer<MethodVisitor> methodCreator = FACTORY.createMethod(name, owner, method);
        ProxyCreator creator = new ProxyCreator(descriptor, constructor, methodCreator);
        creator.accept(ret);
        ret.visitEnd();
        byte[] bytes = ret.toByteArray();
        return definer.define(name, bytes);
    }

    /**
     * @param method
     * @param bind
     * @param <T>
     * @return
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public <T> Lambda packMethod(Method method, T bind) throws
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Objects.requireNonNull(method);
        checkMethod(method, false);
        Objects.requireNonNull(bind);
        Class<?> found = findClass(method);
        Constructor<?> constructor = found.getDeclaredConstructors()[0];
        return (Lambda) constructor.newInstance(bind);
    }

    /**
     * @param method
     * @return
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Lambda packMethod(Method method) throws
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Objects.requireNonNull(method);
        checkMethod(method, true);
        Class<?> found = findClass(method);
        Constructor<?> constructor = found.getDeclaredConstructors()[0];
        return (Lambda) constructor.newInstance();
    }
}
