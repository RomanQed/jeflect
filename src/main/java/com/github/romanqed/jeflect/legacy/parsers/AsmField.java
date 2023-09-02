package com.github.romanqed.jeflect.legacy.parsers;

import com.github.romanqed.jeflect.legacy.ByteAnnotation;
import com.github.romanqed.jeflect.legacy.ByteClass;
import com.github.romanqed.jeflect.legacy.ByteField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class AsmField extends ByteField {
    final List<ByteAnnotation> annotations;

    AsmField(ByteClass parent, String descriptor, Object value, String name, int modifiers) {
        super(parent, descriptor, value, name, modifiers);
        this.annotations = new ArrayList<>();
    }

    @Override
    public List<ByteAnnotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }
}
