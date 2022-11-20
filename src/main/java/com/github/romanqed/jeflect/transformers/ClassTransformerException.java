package com.github.romanqed.jeflect.transformers;

import java.util.List;

public class ClassTransformerException extends IllegalStateException {
    private final List<Throwable> problems;

    public ClassTransformerException(List<Throwable> problems) {
        super("Cannot finalize class transforming due to unexpected exceptions");
        this.problems = problems;
    }

    public List<Throwable> getProblems() {
        return problems;
    }
}
