package com.github.romanqed.jeflect;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

import static com.github.romanqed.jeflect.Constants.VOID;

class CreatorFactory {
    Consumer<MethodVisitor> createConstructor(boolean isStatic, Type type) {
        if (isStatic) {
            return new StaticConstructorCreator();
        }
        return new VirtualConstructorCreator(type.getInternalName(), type.getDescriptor());
    }

    Consumer<MethodVisitor> createMethod(String proxy, Class<?> clazz, Method method) {
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        Type type = Type.getType(clazz);
        boolean isInterface = clazz.isInterface();
        MethodData methodData = new MethodData(
                Type.getType(method),
                method.getName(),
                method.getReturnType() != VOID);
        if (isStatic) {
            return new StaticMethodCreator(type, isInterface, methodData);
        }
        return new VirtualMethodCreator(proxy, type, isInterface, methodData);
    }
}
