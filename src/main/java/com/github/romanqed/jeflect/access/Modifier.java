package com.github.romanqed.jeflect.access;

import org.objectweb.asm.Opcodes;

public enum Modifier {
    PUBLIC(Util.getMask(Opcodes.ACC_PROTECTED, 3), Opcodes.ACC_PUBLIC),
    PROTECTED(Util.getMask(Opcodes.ACC_PROTECTED, 3), Opcodes.ACC_PROTECTED),
    PRIVATE(Util.getMask(Opcodes.ACC_PROTECTED, 3), Opcodes.ACC_PRIVATE),
    STATIC(Util.getMask(Opcodes.ACC_STATIC), Opcodes.ACC_STATIC),
    FINAL(Util.getMask(Opcodes.ACC_FINAL), Opcodes.ACC_FINAL),
    SYNCHRONIZED(Util.getMask(Opcodes.ACC_SYNCHRONIZED), Opcodes.ACC_SYNCHRONIZED),
    VOLATILE(Util.getMask(Opcodes.ACC_VOLATILE), Opcodes.ACC_VOLATILE),
    TRANSIENT(Util.getMask(Opcodes.ACC_TRANSIENT), Opcodes.ACC_TRANSIENT),
    NATIVE(Util.getMask(Opcodes.ACC_NATIVE), Opcodes.ACC_NATIVE),
    INTERFACE(Util.getMask(Opcodes.ACC_INTERFACE), Opcodes.ACC_INTERFACE),
    ABSTRACT(Util.getMask(Opcodes.ACC_ABSTRACT), Opcodes.ACC_ABSTRACT),
    STRICT(Util.getMask(Opcodes.ACC_STRICT), Opcodes.ACC_STRICT);

    private final int mask;
    private final int value;

    Modifier(int mask, int value) {
        this.mask = mask;
        this.value = value;
    }

    public int set(int modifiers, boolean reset) {
        return (modifiers & mask) | (reset ? 0 : value);
    }

    public int set(int modifiers) {
        return set(modifiers, false);
    }
}
