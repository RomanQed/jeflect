# jeflect [![maven-central](https://img.shields.io/maven-central/v/com.github.romanqed/jeflect?color=blue)](https://repo1.maven.org/maven2/com/github/romanqed/jeflect/)

A set of utilities designed to interact with reflection and speed up it.

## Getting Started

To install it, you will need:

* java 8+
* Maven/Gradle

### Features

* Getting values from annotations
* Packaging methods using a proxy
* Packaging methods with meta-lambdas

## Installing

### Gradle dependency

```Groovy
dependencies {
    implementation group: 'com.github.romanqed', name: 'jeflect', version: 'LATEST'
}
```

### Maven dependency

```
<dependency>
    <groupId>com.github.romanqed</groupId>
    <artifactId>jeflect</artifactId>
    <version>LATEST</version>
</dependency>
```

## Examples

### Packaging of the method using meta-lambdas

```Java
import com.github.romanqed.jeflect.ReflectUtil;

import java.util.concurrent.Callable;

public class Main {
    public static void main(String[] args) throws Throwable {
        Callable<String> packed = ReflectUtil.packCallable(ToPack.class.getMethod("packMe"), new ToPack());
        System.out.println(packed.call());
    }

    public static class ToPack {
        public String packMe() {
            return "Hello, I'm packed!";
        }
    }
}

```

### Packaging of the method using proxy

```Java
import com.github.romanqed.jeflect.Lambda;
import com.github.romanqed.jeflect.ReflectUtil;

public class Main {
    public static void main(String[] args) throws Throwable {
        Lambda packed = ReflectUtil.packMethod(ToPack.class.getMethod("packMe"), new ToPack());
        System.out.println(packed.call(new Object[0]));
    }

    public static class ToPack {
        public String packMe() {
            return "Hello, I'm packed!";
        }
    }
}
```

### Packaging of the method using custom lambda

```Java
import com.github.romanqed.jeflect.ReflectUtil;
import com.github.romanqed.jeflect.meta.LambdaClass;

public class Main {
    public static void main(String[] args) throws Throwable {
        LambdaClass<MyLambda> clazz = LambdaClass.fromClass(MyLambda.class);
        MyLambda lambda = ReflectUtil.packLambdaMethod(clazz, Main.class.getDeclaredMethod("toPack", int.class), new Main());
        System.out.println(lambda.increment(0));
    }

    public int toPack(int number) {
        return number + 1;
    }

    public interface MyLambda {
        int increment(int number);
    }
}
```

### Packaging of the method using LambdaMethod

```Java
import com.github.romanqed.jeflect.LambdaMethod;
import com.github.romanqed.jeflect.ReflectUtil;

public class Main {
    public static void main(String[] args) throws Throwable {
        LambdaMethod lambda = ReflectUtil.packLambdaMethod(Main.class.getDeclaredMethod("toPack", int.class));
        System.out.println(lambda.call(new Main(), new Object[]{0}));
    }

    public int toPack(int number) {
        return number + 1;
    }
}
```

## Built With

* [Gradle](https://gradle.org) - Dependency management
* [ASM](https://asm.ow2.io) - Generation of dynamic proxies.

## Authors

* **RomanQed** - *Main work* - [RomanQed](https://github.com/RomanQed)

See also the list of [contributors](https://github.com/RomanQed/jeflect/contributors)
who participated in this project.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details
