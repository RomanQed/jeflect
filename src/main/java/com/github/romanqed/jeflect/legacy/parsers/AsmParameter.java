package com.github.romanqed.jeflect.legacy.parsers;

import com.github.romanqed.jeflect.legacy.ByteAnnotation;
import com.github.romanqed.jeflect.legacy.ByteParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class AsmParameter extends ByteParameter {
    final List<ByteAnnotation> annotations;

    AsmParameter(String descriptor, int modifiers) {
        super(descriptor, modifiers);
        this.annotations = new ArrayList<>();
    }

    @Override
    public List<ByteAnnotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }
}
