package com.github.romanqed.jeflect;

/**
 * Represents the general format of a method that is not bound to a specific object.
 */
public interface LambdaMethod extends Lambda {
    /**
     * Calls the target method with the passed parameters.
     *
     * @param object    the object of the class that the method belongs to.
     * @param arguments a set of arguments that will be passed to the method as input.
     * @return the value that the target method will return
     * @throws Throwable if any exception occurred during the call/preparation of the packed method call.
     */
    Object call(Object object, Object[] arguments) throws Throwable;

    @Override
    default Object call(Object[] arguments) throws Throwable {
        return call(null, arguments);
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
}
