package com.github.romanqed.jeflect;

/**
 * Represents the general format of the target method.
 * If it has no return value, {@link Lambda#call} will return null.
 */
@FunctionalInterface
public interface Lambda {
    /**
     * Calls the target method with the passed parameters.
     *
     * @param arguments A set of arguments that will be passed to the method as input.
     * @return the value that the target method will return
     * @throws Throwable if any exception occurred during the call/preparation of the packed method call.
     */
    Object call(Object[] arguments) throws Throwable;

    default Object call() throws Throwable {
        return call(Constants.EMPTY_ARGUMENTS);
    }
}
