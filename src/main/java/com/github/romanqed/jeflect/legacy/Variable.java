package com.github.romanqed.jeflect.legacy;

import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * A class describing an abstract variable with a name and type.
 */
public final class Variable {
    private final String name;
    private final Type type;

    public Variable(String name, Type type) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
    }

    /**
     * Creates a variable from {@link Field}
     *
     * @param field field instance
     * @return the created variable
     */
    public static Variable fromField(Field field) {
        return new Variable(field.getName(), Type.getType(field.getType()));
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return name + ":" + type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Variable)) {
            return false;
        }
        Variable variable;
        try {
            variable = (Variable) o;
        } catch (Exception e) {
            return false;
        }
        return Objects.equals(name, variable.name) && Objects.equals(type, variable.type);
    }
}
