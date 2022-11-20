package com.github.romanqed.jeflect.access;

import com.github.romanqed.jeflect.ByteClass;

import java.util.function.BiConsumer;

public interface Accessor {
    void setAccess(Runnable loader, BiConsumer<AccessModifier, ByteClass> consumer);
}
