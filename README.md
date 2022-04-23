# jeflect
A set of utilities designed to interact with reflection and speed up it.

## Getting Started

To install it, you will need:

* any build of the JDK no older than version 8
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

## Built With

* [Gradle](https://gradle.org) - Dependency management
* [ASM](https://asm.ow2.io) - Generation of dynamic proxies.

## Authors
* **RomanQed** - *Main work* - [RomanQed](https://github.com/RomanQed)

See also the list of [contributors](https://github.com/RomanQed/jeflect/contributors)
who participated in this project.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details