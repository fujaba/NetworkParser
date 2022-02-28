package de.uniks.networkparser.ext.generic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleMock implements InvocationHandler{
    private Object instanceObject;
    private Map<Method, Integer> history = new HashMap<>();
    private Map<Method, List<Consumer<MethodMock>>> mocks;
    //    private Object callBack;
    //    private boolean callSuper;
 
    /**
     * Mock an Interface or Create a Instance
     * @param <T> ClassType
     * @param clazz Class for Mocking
     * @return MockInstance
     */
    public static <T> T mock(Class<T> clazz) {
        if(!clazz.isInterface()) {
            T instance = mockClass(clazz);
            if(instance != null) {
                return instance;
            }
        }
        return mockInterface(clazz);
        
    }
    @SuppressWarnings("unchecked")
    public static <T> T mockInterface(Class<T> clazz) {
        if(clazz.isInterface()) {
            return (T) Proxy.newProxyInstance(
                        clazz.getClassLoader(),
                        new Class[] {clazz},
                        new SimpleMock()
                        );
        }
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                clazz.getInterfaces(),
                new SimpleMock()
                );
    }
    
    /**
     * Create a new Instance
     * @param <T> ClassType
     * @param clazz Class for Mocking
     * @return A new Instance
     */
    public static <T> T mockClass(Class<T> clazz) {
        if(!clazz.isInterface()) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                // DO NOTHING
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T mock(Class<T> clazz, T instance) {
        return (T) Proxy.newProxyInstance(
                    clazz.getClassLoader(),
                    new Class[] {clazz},
                    new SimpleMock().withInstance(instance)
                    );
    }
    private InvocationHandler withInstance(Object instance) {
        this.instanceObject = instance;
        return this;
    }
    public Object getInstance() {
        return instanceObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Integer integer = history.get(method);
        if(integer == null) {
            history.put(method, 0);
        }else {
            history.put(method, integer+1);
        }
        if(instanceObject != null) {
            return method.invoke(instanceObject, args);
        }
        return getDefaultResult(method);
    }
    
    public Object getDefaultResult(Method method) {
        Class<?> returnType = method.getReturnType();
        if(Number.class.isAssignableFrom(returnType)
           || returnType.isAssignableFrom(int.class) 
           || returnType.isAssignableFrom(long.class)
           || returnType.isAssignableFrom(float.class)
           || returnType.isAssignableFrom(double.class)) {
            return 0;
        } else if(returnType.equals(String.class)) {
            return "";
        }
        return SimpleMock.mock(returnType);
    }
    
    
    public static <T> T WHEN(Object mock) {
        return null;
    }
}
