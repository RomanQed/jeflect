package com.github.romanqed.jeflect;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

public class MethodCreatorFactory {
    public Consumer<MethodVisitor> createMethod(Class<?> clazz, Method method) {
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        Type type = Type.getType(clazz);
        boolean isInterface = clazz.isInterface();
        MethodData methodData = new MethodData(
                Type.getType(method),
                method.getName(),
                Type.getType(method.getReturnType()));
        if (isStatic) {
            return new StaticMethodCreator(type, isInterface, methodData, 2);
        }
        return new VirtualMethodCreator(type, isInterface, methodData);
    }
}
