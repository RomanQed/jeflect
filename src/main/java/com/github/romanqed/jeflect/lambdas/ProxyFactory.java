package com.github.romanqed.jeflect.lambdas;

import java.lang.reflect.Method;

interface ProxyFactory {
    byte[] create(String name, Method source);
}
