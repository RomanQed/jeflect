package com.github.romanqed.jeflect.access;

import com.github.romanqed.jeflect.AsmUtil;
import org.objectweb.asm.*;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

final class AccessModifier extends ClassVisitor {
    private final Set<String> fields;
    private final Set<String> methods;

    AccessModifier(ClassVisitor classVisitor) {
        super(Opcodes.ASM8, classVisitor);
        this.fields = new HashSet<>();
        this.methods = new HashSet<>();
    }

    void addField(Field field) {
        this.fields.add(Type.getDescriptor(field.getType()) + field.getName());
    }

    void addMethod(Executable method) {
        StringBuilder descriptor = new StringBuilder();
        descriptor.append('(');
        for (Class<?> type : method.getParameterTypes()) {
            descriptor.append(Type.getDescriptor(type));
        }
        descriptor.append(')');
        if (method instanceof Method) {
            Class<?> type = ((Method) method).getReturnType();
            descriptor
                    .append(Type.getDescriptor(type))
                    .append(method.getName());
        } else {
            descriptor.append("V<init>");
        }
        this.methods.add(descriptor.toString());
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version,
                AsmUtil.resetAccess(access) | Opcodes.ACC_PUBLIC,
                name,
                signature,
                superName,
                interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (methods.contains(descriptor + name)) {
            access = AsmUtil.resetAccess(access) | Opcodes.ACC_PUBLIC;
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (fields.contains(descriptor + name)) {
            access = AsmUtil.resetAccess(access) | Opcodes.ACC_PUBLIC;
        }
        return super.visitField(access, name, descriptor, signature, value);
    }
}
