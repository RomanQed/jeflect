package com.github.romanqed.jeflect.parser;

import com.github.romanqed.jeflect.ByteClass;

public interface ClassFileParser {
    ByteClass parse(byte[] classFileBuffer);
}
