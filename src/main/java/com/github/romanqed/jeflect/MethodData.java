package com.github.romanqed.jeflect;

import org.objectweb.asm.Type;

class MethodData {
    final Type method;
    final String methodName;
    final Type returnType;

    MethodData(Type method, String methodName, Type returnType) {
        this.method = method;
        this.methodName = methodName;
        this.returnType = returnType;
    }

    Type[] getArguments() {
        return method.getArgumentTypes();
    }

    String getDescriptor() {
        return method.getDescriptor();
    }
}
