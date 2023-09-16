package com.cobelpvp.atheneum.command.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EasyMethod {
    private final EasyClass<?> owner;
    private Method method;
    private final Object[] parameters;

    public EasyMethod(final EasyClass<?> owner, final String name, final Object... parameters) {
        this.owner = owner;
        this.parameters = parameters;
        final Class<?>[] classes = (Class<?>[]) new Class[parameters.length];
        for (int i = 0; i < parameters.length; ++i) {
            classes[i] = parameters[i].getClass();
        }
        try {
            this.method = owner.getClazz().getDeclaredMethod(name, classes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public Object invoke() {
        this.method.setAccessible(true);
        if (this.method.getReturnType().equals(Void.TYPE)) {
            try {
                this.method.invoke(this.owner.get(), this.parameters);
            } catch (InvocationTargetException | IllegalAccessException var2) {
                var2.printStackTrace();
            }

            return null;
        } else {
            try {
                return this.method.getReturnType().cast(this.method.invoke(this.owner.get(), this.parameters));
            } catch (InvocationTargetException | IllegalAccessException var3) {
                var3.printStackTrace();
                return null;
            }
        }
    }
}
