package cz.upce.boop.ex.di;

import cz.upce.boop.ex.collection.BGenericList;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.function.Function;

public class DIContainer {

    public static class ServiceNotFoundException extends RuntimeException {

        public ServiceNotFoundException(String message) {
            super(message);
        }
    }

    public static class ServiceCreationException extends RuntimeException {

        public ServiceCreationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private record PairClassObject(Class<?> clazz, Object object) {

    }

    private record ServiceDefinition(Class<?> interfaceType, Class<?> implementationType, Function<DIContainer, ?> supplier, Scope scope) {

    }
    private final BGenericList<ServiceDefinition> definitions;
    private final BGenericList<PairClassObject> singletons;

    public DIContainer() {
        this.definitions = new BGenericList<>();
        this.singletons = new BGenericList<>();
    }

    public void register(Class<?> interfaceType, Class<?> implementationType, Scope scope) {
        ServiceDefinition def = new ServiceDefinition(interfaceType, implementationType, null, scope);
        definitions.add(def);
    }

    public void register(Class<?> interfaceType, Class<?> implementationType, Function<DIContainer, ?> supplier, Scope scope) {
        ServiceDefinition def = new ServiceDefinition(interfaceType, implementationType, supplier, scope);
        definitions.add(def);
    }

    public <T> T getInstance(Class<T> interfaceType) {
        ServiceDefinition def = findDefinition(interfaceType);
        if (def == null) {
            throw new ServiceNotFoundException("No registration found for: " + interfaceType.getName());
        }

        return (T) getOrCreateInstance(def);
    }

    private ServiceDefinition findDefinition(Class<?> interfaceType) {
        for (int i = 0; i < definitions.size(); i++) {
            ServiceDefinition def = definitions.get(i);
            if (def.interfaceType().equals(interfaceType)) {
                return def;
            }
        }
        return null;
    }

    private Object getOrCreateInstance(ServiceDefinition def) {
        if (def.scope() == Scope.SINGLETON) {
            Object instance = singletons.stream().filter(p -> p.clazz().equals(def.implementationType())).findFirst().map(o -> o.object()).orElse(null);
            if (instance == null) {
                instance = createInstance(def);
                singletons.add(new PairClassObject(def.implementationType(), instance));
            }
            return instance;
        } else {
            return createInstance(def);
        }
    }

    private Object createInstance(ServiceDefinition def) {
        try {
            Object instance;

            if (def.supplier() != null) {
                instance = def.supplier().apply(this);
            } else {
                // Find injectable constructor
                Constructor<?> constructor = findInjectableConstructor(def.implementationType());

                Object[] params = resolveConstructorDependencies(constructor);
                instance = constructor.newInstance(params);
            }
            
            // Process field injection
            injectFields(instance);

            // Process method injection
            injectMethods(instance);

            // Call @Init methods
            invokeLifecycleMethod(instance, Init.class);

            return instance;
        } catch (Exception e) {
            throw new ServiceCreationException("Failed to create instance of: " + def.implementationType().getName(), e);
        }
    }

    private Constructor<?> findInjectableConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        // Find constructor with @Inject
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                constructor.setAccessible(true);
                return constructor;
            }
        }

        // If no @Inject constructor, use the default constructor
        try {
            Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
            defaultConstructor.setAccessible(true);
            return defaultConstructor;
        } catch (NoSuchMethodException e) {
            throw new ServiceCreationException("No suitable constructor found for " + clazz.getName(), e);
        }
    }

    private Object[] resolveConstructorDependencies(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        Object[] params = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Class<?> paramType = parameters[i].getType();

            params[i] = getInstance(paramType);
        }

        return params;
    }

    private void injectFields(Object instance) throws IllegalAccessException {
        Class<?> clazz = instance.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                Object dependency = getInstance(field.getType());
                field.set(instance, dependency);
            }
        }
    }

    private void injectMethods(Object instance) throws InvocationTargetException, IllegalAccessException {
        Class<?> clazz = instance.getClass();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Inject.class)) {
                method.setAccessible(true);

                Parameter[] parameters = method.getParameters();
                Object[] params = new Object[parameters.length];

                for (int i = 0; i < parameters.length; i++) {
                    Class<?> paramType = parameters[i].getType();
                    params[i] = getInstance(paramType);
                }

                method.invoke(instance, params);
            }
        }
    }

    private void invokeLifecycleMethod(Object instance, Class<? extends Annotation> annotationType)
            throws InvocationTargetException, IllegalAccessException {
        Class<?> clazz = instance.getClass();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationType)) {
                method.setAccessible(true);
                method.invoke(instance);
            }
        }
    }

    public void shutdown() {
        // Call @Destroy methods for all singleton instances
        for (Object instance : singletons.stream().map(p -> p.object()).toList()) {
            try {
                invokeLifecycleMethod(instance, Destroy.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        singletons.clear();
    }

}
