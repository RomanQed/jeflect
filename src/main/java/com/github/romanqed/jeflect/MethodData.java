package com.github.romanqed.jeflect;

import org.objectweb.asm.Type;

class MethodData {
    final Type method;
    final String methodName;
    final boolean hasReturn;

    MethodData(Type method, String methodName, boolean hasReturn) {
        this.method = method;
        this.methodName = methodName;
        this.hasReturn = hasReturn;
    }

    Type[] getArguments() {
        return method.getArgumentTypes();
    }

    String getDescriptor() {
        return method.getDescriptor();
    }
}
