package com.github.romanqed.jeflect.access;

final class Util {
    // (2 ^ 16) - 1, because modifiers stores in 16 bits
    private static final int MODIFIER_MASK = (1 << 16) - 1;

    static int getMask(int modifier, int fill) {
        int ret = MODIFIER_MASK;
        for (int i = 0; modifier > 0 && i < fill; ++i) {
            ret = ret & ~modifier;
            modifier = modifier >> 1;
        }
        return ret;
    }

    static int getMask(int modifier) {
        return getMask(modifier, 1);
    }
}
