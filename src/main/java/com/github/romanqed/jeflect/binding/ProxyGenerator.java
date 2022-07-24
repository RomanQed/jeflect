package com.github.romanqed.jeflect.binding;

import com.github.romanqed.jeflect.AsmUtil;
import com.github.romanqed.jeflect.Variable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.github.romanqed.jeflect.AsmUtil.INTERNAL_OBJECT_NAME;
import static com.github.romanqed.jeflect.binding.Util.FIELD_NAME;

final class ProxyGenerator {
    private static final int PACKAGE_PRIVATE = 0;
    private static final Map<Type, Integer> RET_OPCODES = getRetOpcodes();
    private static final Map<Type, Integer> LOAD_OPCODES = getLoadOpcodes();
    private final InterfaceType<?> interfaceType;
    private final Class<?> target;
    private final Type targetType;
    private final Map<String, Method> namedMethods;

    ProxyGenerator(InterfaceType<?> interfaceType, Class<?> target) {
        this.interfaceType = interfaceType;
        this.target = target;
        this.namedMethods = getNamedMethods();
        this.targetType = Type.getType(target);
    }

    private static Map<Type, Integer> getRetOpcodes() {
        Map<Type, Integer> ret = new HashMap<>();
        ret.put(Type.VOID_TYPE, Opcodes.RETURN);
        // Int return for boolean, short, char, int, byte
        ret.put(Type.BOOLEAN_TYPE, Opcodes.IRETURN);
        ret.put(Type.SHORT_TYPE, Opcodes.IRETURN);
        ret.put(Type.CHAR_TYPE, Opcodes.IRETURN);
        ret.put(Type.BYTE_TYPE, Opcodes.IRETURN);
        ret.put(Type.INT_TYPE, Opcodes.IRETURN);
        // Another return for long
        ret.put(Type.LONG_TYPE, Opcodes.LRETURN);
        // Float types
        ret.put(Type.FLOAT_TYPE, Opcodes.FRETURN);
        ret.put(Type.DOUBLE_TYPE, Opcodes.DRETURN);
        return ret;
    }

    private static Map<Type, Integer> getLoadOpcodes() {
        Map<Type, Integer> ret = new HashMap<>();
        // Int load for boolean, short, char, int, byte
        ret.put(Type.BOOLEAN_TYPE, Opcodes.ILOAD);
        ret.put(Type.SHORT_TYPE, Opcodes.ILOAD);
        ret.put(Type.CHAR_TYPE, Opcodes.ILOAD);
        ret.put(Type.BYTE_TYPE, Opcodes.ILOAD);
        ret.put(Type.INT_TYPE, Opcodes.ILOAD);
        // Another load for long
        ret.put(Type.LONG_TYPE, Opcodes.LLOAD);
        // Float types
        ret.put(Type.FLOAT_TYPE, Opcodes.FLOAD);
        ret.put(Type.DOUBLE_TYPE, Opcodes.DLOAD);
        return ret;
    }

    private static boolean checkReturnType(Method left, Method right) {
        Class<?> leftType = left.getReturnType();
        Class<?> rightType = right.getReturnType();
        return leftType == rightType || left.getReturnType().isAssignableFrom(right.getReturnType());
    }

    private static int getAccess(Method method) {
        int modifiers = method.getModifiers();
        if (Modifier.isPublic(modifiers)) {
            return Opcodes.ACC_PUBLIC;
        }
        if (Modifier.isProtected(modifiers)) {
            return Opcodes.ACC_PROTECTED;
        }
        if (Modifier.isPrivate(modifiers)) {
            return Opcodes.ACC_PRIVATE;
        }
        return PACKAGE_PRIVATE;
    }

    private Map<String, Method> getNamedMethods() {
        Map<String, Method> ret = new HashMap<>();
        for (Method method : target.getMethods()) {
            BindName bindName = method.getAnnotation(BindName.class);
            if (bindName != null) {
                String key = bindName.value() + Arrays.toString(Type.getType(method).getArgumentTypes());
                ret.put(key, method);
            }
        }
        return ret;
    }

    private Map<Method, Method> bindMethods(Collection<Method> methods, boolean force) {
        Map<Method, Method> ret = new HashMap<>();
        for (Method method : methods) {
            String name = method.getName();
            Method found;
            try {
                found = target.getMethod(name, method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                found = namedMethods.get(name + Arrays.toString(Type.getType(method).getArgumentTypes()));
            }
            if (found != null && checkReturnType(method, found)) {
                ret.put(method, found);
            } else if (force) {
                throw new IllegalStateException("The method for binding with " + method + " was not found");
            }
        }
        return ret;
    }

    private void createMethod(ClassWriter writer, String owner, Method source, Method target) {
        Type sourceType = Type.getType(source);
        Type targetType = Type.getType(target);
        Class<?>[] exceptionTypes = source.getExceptionTypes();
        String[] exceptions = new String[exceptionTypes.length];
        for (int i = 0; i < exceptionTypes.length; ++i) {
            exceptions[i] = Type.getType(exceptionTypes[i]).getInternalName();
        }
        MethodVisitor visitor = writer.visitMethod(
                getAccess(source),
                source.getName(),
                sourceType.getDescriptor(),
                null,
                exceptions);
        visitor.visitCode();
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitFieldInsn(Opcodes.GETFIELD, owner, FIELD_NAME, this.targetType.getDescriptor());
        Type[] arguments = sourceType.getArgumentTypes();
        for (int i = 0; i < arguments.length; ++i) {
            visitor.visitVarInsn(LOAD_OPCODES.getOrDefault(arguments[i], Opcodes.ALOAD), i + 1);
        }
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                this.targetType.getInternalName(),
                target.getName(),
                targetType.getDescriptor(),
                false);
        visitor.visitInsn(RET_OPCODES.getOrDefault(sourceType.getReturnType(), Opcodes.ARETURN));
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    byte[] create() {
        Map<Method, Method> abstractMethods = bindMethods(interfaceType.getAbstractMethods(), true);
        Map<Method, Method> defaultMethods = bindMethods(interfaceType.getDefaultMethods(), false);
        Class<?> clazz = interfaceType.getType();
        String name = Util.getName(clazz, target);
        Type type = Type.getType(clazz);
        // Generate proxy class
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        writer.visit(Opcodes.V1_8,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                name,
                null,
                INTERNAL_OBJECT_NAME,
                new String[]{type.getInternalName()});
        // Generate constructor
        AsmUtil.createConstructor(writer, name, new Variable(FIELD_NAME, targetType));
        // Generate methods
        abstractMethods.forEach((key, value) -> createMethod(writer, name, key, value));
        defaultMethods.forEach((key, value) -> createMethod(writer, name, key, value));
        return writer.toByteArray();
    }
}
