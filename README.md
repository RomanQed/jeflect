# jeflect [![maven-central](https://img.shields.io/maven-central/v/com.github.romanqed/jeflect/1.1.4L?color=blue)](https://repo1.maven.org/maven2/com/github/romanqed/jeflect/1.1.4L)

A set of utilities designed to interact with reflection and speed up it.

## Getting Started

To install it, you will need:

* Java 11+
* Maven/Gradle

### Features

* Getting values from annotations by name
* Packaging methods using a universal proxy lambdas
* Packaging methods with meta-lambdas
* Packaging fields with proxy accessor

## Installing

### Gradle dependency

```Groovy
dependencies {
    implementation group: 'com.github.romanqed', name: 'jeflect', version: '1.1.4L'
}
```

### Maven dependency

```
<dependency>
    <groupId>com.github.romanqed</groupId>
    <artifactId>jeflect</artifactId>
    <version>1.1.4L</version>
</dependency>
```

## Examples

### Field accessor

```Java
package com.github.romanqed.jeflect;

import com.github.romanqed.jeflect.fields.FieldAccessorFactory;

public class Main {
    public static final String README = "README";

    public static void main(String[] args) throws Exception {
        // So, we need to access field by name
        // How can we do this?
        var field = Main.class.getField("README");
        // Default, very slow, built-in reflection way
        System.out.println(field.get(null)); // <-- Very bad choice to use it during active calculating
        // A wonderful, ultra-fast, shining way with field accessor
        var factory = new FieldAccessorFactory();
        var accessor = factory.packField(field); // <-- This action takes a long time, do this only once
        System.out.println(accessor.get()); // <-- This action is performed as fast as a normal field access
    }
}
```

### Lambdas

```Java
package com.github.romanqed.jeflect;

import com.github.romanqed.jeflect.lambdas.LambdaFactory;

public class Main {
    public static void main(String[] args) throws Throwable {
        // So, we need to invoke method by name
        // How can we do this?
        var method = Main.class.getMethod("callMe", int.class, int.class, int.class);
        var params = new Object[]{1, 2, 3};
        // Default, very slow, built-in reflection way
        method.invoke(null, params); // <-- Very bad choice to use it during active calculating
        // A wonderful, ultra-fast, shining way with proxy lambdas (not so fast as meta-lambdas, but more universal)
        var factory = new LambdaFactory();
        var lambda = factory.packMethod(method); // <-- This action takes a long time, do this only once
        lambda.invoke(params); // <-- This action is performed as fast as a normal method call
    }

    public static void callMe(int first, int second, int third) {
        System.out.println("Hello, i am very useful method, i can sum your numbers: " + (first + second + third));
    }
}
```

### Meta lambdas

```Java
package com.github.romanqed.jeflect;

import com.github.romanqed.jeflect.meta.LambdaType;
import com.github.romanqed.jeflect.meta.MetaFactory;

import java.lang.invoke.MethodHandles;

public class Main {
    public static void main(String[] args) throws Exception {
        // So, we need to invoke method by name
        // How can we do this?
        var method = Main.class.getMethod("callMe");
        // Default, very slow, built-in reflection way
        method.invoke(null); // <-- Very bad choice to use it during active calculating
        // A wonderful, ultra-fast, shining way with meta-lambdas
        var factory = new MetaFactory(MethodHandles.lookup());
        var type = LambdaType.fromClass(Runnable.class);
        var runnable = factory.packLambdaMethod(type, method); // <-- This action takes a long time, do this only once
        runnable.run(); // <-- This action is performed as fast as a normal method call
    }

    public static void callMe() {
        System.out.println("Hello, i am very useful method");
    }
}
```

## Built With

* [Gradle](https://gradle.org) - Dependency management
* [ASM](https://asm.ow2.io) - Generation of dynamic proxies
* [jfunc](https://github.com/RomanQed/jfunc) - Lazy containers

## Authors

* **[RomanQed](https://github.com/RomanQed)** - *Main work*

See also the list of [contributors](https://github.com/RomanQed/jeflect/contributors)
who participated in this project.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details
