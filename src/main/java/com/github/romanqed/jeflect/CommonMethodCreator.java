package com.github.romanqed.jeflect;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

import static com.github.romanqed.jeflect.Constants.*;

abstract class CommonMethodCreator implements Consumer<MethodVisitor> {
    private static final int INT_0 = Opcodes.ICONST_0;
    private static final String DESCRIPTOR = "(%s)%s";
    private final Type[] arguments;
    private final Type returnType;
    private final int argument;

    CommonMethodCreator(Type returnType, Type[] arguments, int argument) {
        this.returnType = returnType;
        this.arguments = arguments;
        this.argument = argument;
    }

    private void castArgument(MethodVisitor visitor, Type argument) {
        String name = argument.getInternalName();
        if (name.startsWith("[")) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, name);
            visitor.visitTypeInsn(Opcodes.CHECKCAST, name);
            return;
        }
        String wrap = PRIMITIVES.get(name);
        if (wrap != null) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, wrap);
            String method = PRIMITIVE_METHODS.get(name);
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, wrap, method, "()" + name, false);
            return;
        }
        visitor.visitTypeInsn(Opcodes.CHECKCAST, name);
    }

    protected void createArguments(MethodVisitor visitor) {
        for (int i = 0; i < arguments.length; ++i) {
            visitor.visitVarInsn(Opcodes.ALOAD, argument);
            if (i < 6) {
                visitor.visitInsn(INT_0 + i);
            } else {
                visitor.visitVarInsn(Opcodes.BIPUSH, i);
            }
            visitor.visitInsn(Opcodes.AALOAD);
            castArgument(visitor, arguments[i]);
        }
    }

    private void packPrimitive(MethodVisitor visitor) {
        String name = returnType.getInternalName();
        String wrap = PRIMITIVES.get(name);
        if (wrap == null) {
            return;
        }
        String descriptor = String.format(DESCRIPTOR, name, "L" + wrap + ";");
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, wrap, "valueOf", descriptor, false);
    }

    @Override
    public void accept(MethodVisitor visitor) {
        if (returnType.getDescriptor().equals(VOID)) {
            visitor.visitInsn(Opcodes.ACONST_NULL);
        } else {
            packPrimitive(visitor);
        }
        visitor.visitInsn(Opcodes.ARETURN);
    }
}
