package com.github.romanqed.jeflect;

public interface ByteMember extends ByteAnnotated {
    String getName();

    int getModifiers();

    ByteClass getDeclaringClass();
}
