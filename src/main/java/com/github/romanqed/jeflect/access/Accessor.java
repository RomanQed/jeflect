package com.github.romanqed.jeflect.access;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;

public interface Accessor {
    void modifyAccess(Class<?> clazz);

    void modifyAccess(Field field);

    void modifyAccess(Executable method);
}
