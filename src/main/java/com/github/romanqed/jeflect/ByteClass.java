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
        if (interfaces == null) {
            this.interfaces = Collections.emptyList();
        } else {
            this.interfaces = Collections.unmodifiableList(Arrays
                    .stream(interfaces)
                    .map(LazyType::new)
                    .collect(Collectors.toList()));
        }
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

    public ByteMethod getMethod(String name, String descriptor) throws NoSuchMethodException {
        List<ByteMethod> methods = getMethods();
        for (ByteMethod method : methods) {
            if (method.getName().equals(name) && method.getDescriptor().equals(descriptor)) {
                return method;
            }
        }
        throw new NoSuchMethodException("Cannot find method " + name + " " + descriptor);
    }

    public ByteField getField(String name, String descriptor) throws NoSuchFieldException {
        List<ByteField> fields = getFields();
        for (ByteField field : fields) {
            if (field.getName().equals(name) && field.getDescriptor().equals(descriptor)) {
                return field;
            }
        }
        throw new NoSuchFieldException("Cannot find field " + name + " " + descriptor);
    }

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
