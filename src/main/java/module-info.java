open module com.github.romanqed.jeflect {
    // Imports
    requires java.instrument;
    requires org.objectweb.asm;
    requires org.objectweb.asm.commons;
    requires com.github.romanqed.jfunc;
    // Exports
    exports com.github.romanqed.jeflect;
    exports com.github.romanqed.jeflect.meta;
    exports com.github.romanqed.jeflect.lambdas;
    exports com.github.romanqed.jeflect.parsers;
    exports com.github.romanqed.jeflect.transformers;
}
