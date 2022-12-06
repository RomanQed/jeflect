package com.github.romanqed.jeflect.lambdas;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

import static com.github.romanqed.jeflect.AsmUtil.INTERNAL_OBJECT_NAME;
import static com.github.romanqed.jeflect.AsmUtil.createEmptyConstructor;
import static com.github.romanqed.jeflect.lambdas.Util.LAMBDA;
import static com.github.romanqed.jeflect.lambdas.Util.createStaticMethod;

abstract class AbstractProxyFactory implements ProxyFactory {
    protected abstract void createConstructor(String owner, ClassWriter writer, Type source);

    protected abstract void createMethod(String owner, ClassWriter writer, MethodData data);

    @Override
    public byte[] create(String name, Method source) {
        ClassWriter ret = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ret.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, name, null, INTERNAL_OBJECT_NAME, new String[]{LAMBDA});
        MethodData data = new MethodData(source);
        if (data.isStatic) {
            createEmptyConstructor(ret);
            createStaticMethod(ret, data);
        } else {
            createConstructor(name, ret, data.owner);
            createMethod(name, ret, data);
        }
        ret.visitEnd();
        return ret.toByteArray();
    }
}
