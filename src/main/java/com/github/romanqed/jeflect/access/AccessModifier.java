package com.github.romanqed.jeflect.access;

import com.github.romanqed.jeflect.ByteClass;
import com.github.romanqed.jeflect.ByteField;
import com.github.romanqed.jeflect.ByteMethod;

public interface AccessModifier {
    void setAccess(ByteClass clazz, int access);

    void setAccess(ByteMethod method, int access);

    void setAccess(ByteField field, int access);
}
