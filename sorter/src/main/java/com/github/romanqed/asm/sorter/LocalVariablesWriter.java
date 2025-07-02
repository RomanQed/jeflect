package com.github.romanqed.asm.sorter;

import org.objectweb.asm.ClassWriter;

/**
 * A specialized {@link ClassWriter} that provides convenient support for creating methods
 * with properly managed local variables via {@link LocalVariablesSorter}.
 * <p>
 * This class simplifies the integration of {@code LocalVariablesSorter} by offering a
 * single method to visit a method with automatic local variable remapping.
 * It is particularly useful when dynamically generating methods that require new
 * local variables to be added, such as during instrumentation or bytecode transformations.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>
 *     var cw = new LocalVariablesWriter(ClassWriter.COMPUTE_FRAMES);
 *     var mv = cw.visitMethodWithLocals(
 *         Opcodes.ACC_PUBLIC,
 *         "myMethod",
 *         "()V",
 *         null,
 *         null
 *     );
 *     // You can now call mv.newLocal(...) to allocate new local variables.
 * </pre>
 * </p>
 *
 * @see LocalVariablesSorter
 * @see org.objectweb.asm.MethodVisitor
 * @see org.objectweb.asm.ClassWriter
 */
public class LocalVariablesWriter extends ClassWriter {

    /**
     * Constructs a new {@code LocalVariablesWriter} with the given flags.
     * <p>
     * These flags control the behavior of the class writer, such as whether it
     * automatically computes stack map frames or maximum stack/local sizes.
     *
     * @param flags option flags, such as {@link #COMPUTE_FRAMES} or {@link #COMPUTE_MAXS}
     */
    public LocalVariablesWriter(int flags) {
        super(flags);
    }

    /**
     * Visits a method and wraps its visitor with a {@link LocalVariablesSorter},
     * enabling support for dynamic local variable allocation.
     * <p>
     * This method delegates to {@link #visitMethod} and wraps the resulting
     * {@code MethodVisitor} with a {@code LocalVariablesSorter}, which automatically
     * tracks local variable indices and remaps them as needed.
     *
     * @param access     the method's access flags (e.g., {@code Opcodes.ACC_PUBLIC})
     * @param name       the method's name
     * @param descriptor the method's descriptor (see {@link org.objectweb.asm.Type})
     * @param signature  the method's generic signature, or {@code null} if not generic
     * @param exceptions the internal names of the method's exception classes, or {@code null}
     * @return a {@link LocalVariablesSorter} for the method, allowing new locals to be declared
     */
    public LocalVariablesSorter visitMethodWithLocals(final int access,
                                                      final String name,
                                                      final String descriptor,
                                                      final String signature,
                                                      final String[] exceptions) {
        var visitor = this.visitMethod(access, name, descriptor, signature, exceptions);
        return new LocalVariablesSorter(access, descriptor, visitor);
    }
}
