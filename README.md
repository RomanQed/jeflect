# jeflect
A set of utilities designed to interact with reflection and speed up it.

## Getting Started

To install it, you will need:

* any build of the JDK no older than version 8
* Maven/Gradle

### Why do I need it?
* Jeflect provides a simple and convenient set of utilities for interacting with reflection and speeding it up.
* Using meta-lambdas and dynamic proxy generation allows you to make calls almost as fast as in user code.

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