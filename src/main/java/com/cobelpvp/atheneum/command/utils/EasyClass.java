package com.cobelpvp.atheneum.command.utils;

public class EasyClass<T> {
    private Class<T> clazz;
    private final T object;

    public EasyClass(final T object) {
        if (object != null) {
            this.clazz = (Class<T>) object.getClass();
        }
        this.object = object;
    }

    public Class<T> getClazz() {
        return this.clazz;
    }

    public T get() {
        return this.object;
    }

    public EasyMethod getMethod(final String name, final Object... parameters) {
        return new EasyMethod(this, name, parameters);
    }

    public <ST> EasyField<ST> getField(final String name) {
        return new EasyField<ST>(this, name);
    }
}
