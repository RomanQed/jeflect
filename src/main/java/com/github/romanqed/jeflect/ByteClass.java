package com.github.romanqed.jeflect;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ByteClass extends AbstractMember {
    private final Package classPackage;
    private final List<LazyType> interfaces;
    private final LazyType type;
    private final LazyType superType;


    protected ByteClass(String superType,
                        String[] interfaces,
                        String name,
                        int modifiers) {
        super(null, name, modifiers);
        this.type = new LazyType(name);
        this.superType = new LazyType(superType);
        this.interfaces = Collections.unmodifiableList(Arrays
                .stream(interfaces)
                .map(LazyType::new)
                .collect(Collectors.toList()));
        this.classPackage = extractPackage(name);
    }

    private Package extractPackage(String name) {
        int index = name.lastIndexOf('.');
        if (index < 0) {
            return null;
        }
        return Package.getPackage(name.substring(0, index));
    }

    public abstract List<ByteField> getFields();

    public abstract List<ByteMethod> getMethods();

    public LazyType getSuperclass() {
        return superType;
    }

    public LazyType getType() {
        return type;
    }

    public Package getPackage() {
        return classPackage;
    }

    public List<LazyType> getInterfaces() {
        return interfaces;
    }
}
