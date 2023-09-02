package com.github.romanqed.jeflect.legacy.binding;

final class Util {
    static final String PROXY = "Proxy";
    static final String FIELD_NAME = "body";

    static String getName(Class<?> interfaceClass, Class<?> target) {
        return interfaceClass.getSimpleName() + target.getSimpleName() + PROXY;
    }
}
