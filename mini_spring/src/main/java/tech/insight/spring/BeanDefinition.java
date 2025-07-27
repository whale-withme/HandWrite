
package tech.insight.spring;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 *  
 */
public class BeanDefinition {

    private final Class<?> beanType;
    private final String name;
    private final Constructor<?> constructor;
    private final Method postConstructMethod;

    BeanDefinition(Class<?> type) {
        this.beanType = type;
        Component component = type.getDeclaredAnnotation(Component.class);
        this.name = component.name().isEmpty() ? type.getPackageName() : component.name();
        try {
            this.constructor = type.getConstructor();
            this.postConstructMethod = Arrays.stream(type.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(PostConstruct.class))
                    .findFirst().orElse(null);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return this.name;
    }

    public Constructor getConstructor() {
        return this.constructor;
    }

    public Method getPostConstructMethod() {
        return this.postConstructMethod;
    }
}
