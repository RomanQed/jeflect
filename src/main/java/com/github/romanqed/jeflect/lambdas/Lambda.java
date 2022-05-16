package com.github.romanqed.jeflect.lambdas;

/**
 * Represents the general format of the target method.
 * If it has no return value, {@link Lambda#call} will return null.
 */
public interface Lambda {
    /**
     * Calls the target method with the passed parameters.
     *
     * @param arguments a set of arguments that will be passed to the method as input.
     * @return the value that the target method will return
     * @throws Throwable if any exception occurred during the call/preparation of the packed method call.
     */
    default Object call(Object[] arguments) throws Throwable {
        return call(null, arguments);
    }

    /**
     * Calls the target method with the passed parameters.
     *
     * @param object    the object of the class that the method belongs to.
     * @param arguments a set of arguments that will be passed to the method as input.
     * @return the value that the target method will return
     * @throws Throwable if any exception occurred during the call/preparation of the packed method call.
     */
    default Object call(Object object, Object[] arguments) throws Throwable {
        throw new UnsupportedOperationException();
    }

    /**
     * Calls the target method without parameters.
     *
     * @param object the object of the class that the method belongs to.
     * @return the value that the target method will return
     * @throws Throwable if any exception occurred during the call/preparation of the packed method call.
     */
    default Object call(Object object) throws Throwable {
        return call(object, Constants.EMPTY_ARGUMENTS);
    }

    /**
     * Calls the target method without parameters.
     *
     * @return the value that the target method will return
     * @throws Throwable if any exception occurred during the call/preparation of the packed method call.
     */
    default Object call() throws Throwable {
        return call(Constants.EMPTY_ARGUMENTS);
    }
}
