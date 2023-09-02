package com.github.romanqed.jeflect.legacy.lambdas;

import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class MethodData {
    final boolean isInterface;
    final Type owner;
    final Type method;
    final String methodName;
    final Type returnType;
    final boolean isStatic;

    MethodData(Method method) {
        Class<?> owner = method.getDeclaringClass();
        this.isInterface = owner.isInterface();
        this.owner = Type.getType(owner);
        this.method = Type.getType(method);
        this.methodName = method.getName();
        this.returnType = Type.getReturnType(method);
        this.isStatic = Modifier.isStatic(method.getModifiers());
    }

    Type[] getArguments() {
        return method.getArgumentTypes();
    }

    String getDescriptor() {
        return method.getDescriptor();
    }
}
