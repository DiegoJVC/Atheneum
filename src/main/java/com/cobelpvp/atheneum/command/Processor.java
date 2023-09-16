package com.cobelpvp.atheneum.command;

@FunctionalInterface
public interface Processor<T, R> {

    R process(T p0);
}
