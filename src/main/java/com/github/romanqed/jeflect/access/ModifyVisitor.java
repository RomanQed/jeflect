package com.github.romanqed.jeflect.access;

import com.github.romanqed.jeflect.ByteField;
import com.github.romanqed.jeflect.ByteMethod;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

final class ModifyVisitor extends ClassVisitor {
    private final String name;
    private final int access;
    private final Map<String, Integer> fields;
    private final Map<String, Integer> methods;

    ModifyVisitor(ClassVisitor classVisitor, String name, int access) {
        super(Opcodes.ASM8, classVisitor);
        this.name = name.replace('.', '/');
        this.access = access;
        this.fields = new HashMap<>();
        this.methods = new HashMap<>();
    }

    void addField(ByteField field, int access) {
        this.fields.put(field.getName() + field.getDescriptor(), access);
    }

    void addMethod(ByteMethod method, int access) {
        this.methods.put(method.getName() + method.getDescriptor(), access);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (this.name.equals(name)) {
            access = this.access;
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        access = methods.getOrDefault(name + descriptor, access);
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        access = fields.getOrDefault(name + descriptor, access);
        return super.visitField(access, name, descriptor, signature, value);
    }
}
