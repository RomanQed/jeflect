package com.github.romanqed.jeflect;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

class LambdaCreatorFactory {
    public Consumer<MethodVisitor> createConstructor(String proxy, boolean isStatic, Type type) {
        if (isStatic) {
            return new EmptyConstructorCreator();
        }
        return new VirtualConstructorCreator(proxy, type.getDescriptor());
    }

    public Consumer<MethodVisitor> createMethod(String proxy, Class<?> clazz, Method method) {
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        Type type = Type.getType(clazz);
        boolean isInterface = clazz.isInterface();
        MethodData methodData = new MethodData(
                Type.getType(method),
                method.getName(),
                Type.getType(method.getReturnType()));
        if (isStatic) {
            return new StaticMethodCreator(type, isInterface, methodData, 1);
        }
        return new VirtualBoundMethodCreator(proxy, type, isInterface, methodData);
    }
}
