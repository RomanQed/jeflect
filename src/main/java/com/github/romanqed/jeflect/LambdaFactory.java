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
 * <p>A class describing a factory that packages methods with any signature into a general-looking lambda interface.</p>
 * <h3>Does not support methods with varargs!</h3>
 * <p>However, calls to target methods can be made a little slower,</p>
 * <p>since copying arguments on the stack and possible packing/unpacking are inevitable.</p>
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

    private Class<?> findClass(Method method, boolean isStatic) {
        Class<?> owner = method.getDeclaringClass();
        long hash = combineHashes(owner.hashCode(), method.hashCode());
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
     * Packages the passed virtual method into a {@link Lambda}.
     *
     * @param method method for packaging
     * @param bind   instance of the object to which the packaged method will be bound
     * @return the object instantiating the {@link Lambda}
     * @throws InvocationTargetException if an error occurred inside the proxy constructor
     * @throws InstantiationException    if the proxy could not be created
     * @throws IllegalAccessException    if the proxy could not be accessed
     */
    public Lambda packMethod(Method method, Object bind) throws
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Objects.requireNonNull(method);
        if (!method.getDeclaringClass().isAssignableFrom(bind.getClass())) {
            throw new IllegalArgumentException("The bind object is not the owner of the method");
        }
        checkMethod(method, false);
        Objects.requireNonNull(bind);
        Class<?> found = findClass(method, false);
        Constructor<?> constructor = found.getDeclaredConstructors()[0];
        return (Lambda) constructor.newInstance(bind);
    }

    /**
     * Packages the passed static method into a {@link Lambda}.
     *
     * @param method method for packaging
     * @return the object instantiating the {@link Lambda}
     * @throws InvocationTargetException if an error occurred inside the proxy constructor
     * @throws InstantiationException    if the proxy could not be created
     * @throws IllegalAccessException    if the proxy could not be accessed
     */
    public Lambda packMethod(Method method) throws
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Objects.requireNonNull(method);
        checkMethod(method, true);
        Class<?> found = findClass(method, true);
        Constructor<?> constructor = found.getDeclaredConstructors()[0];
        return (Lambda) constructor.newInstance();
    }
}
