package me.bramhaag.owo.util;

import lombok.NonNull;

/**
 * Represents an operation that accepts a single input argument and returns no
 * result. Unlike most other functional interfaces, {@code Consumer} is expected
 * to operate via side-effects.
 *
 * This is a Java 7 backport of Java 8's Consumer class.
 *
 * @param <T> the type of the input to the operation
 *
 */

//Imaginary @FunctionalInterface annotation here :<
public interface Consumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(@NonNull T t);
}
