package com.github.romanqed.jeflect.legacy.parsers;

import com.github.romanqed.jeflect.legacy.ByteClass;
import org.objectweb.asm.ClassReader;

/**
 * {@link ClassFileParser} implementation uses ASM.
 */
public final class AsmClassFileParser implements ClassFileParser {
    private static final int OPTIONS = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES | ClassReader.SKIP_CODE;

    @Override
    public ByteClass parse(byte[] classFileBuffer) {
        ClassReader reader = new ClassReader(classFileBuffer);
        ClassParser parser = new ClassParser();
        reader.accept(parser, OPTIONS);
        return parser.getAsmClass();
    }
}
