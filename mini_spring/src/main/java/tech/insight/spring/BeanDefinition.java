
package tech.insight.spring;

import java.lang.reflect.Constructor;

/**
 *  
 */
public class BeanDefinition {

    private final Class<?> beanType;
    private final String name;
    private final Constructor<?> constructor;

    BeanDefinition(Class<?> type) {
        this.beanType = type;
        Component component = type.getDeclaredAnnotation(Component.class);
        this.name = component.name().isEmpty() ? type.getPackageName() : component.name();
        try {
            this.constructor = type.getConstructor();
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    String getName() {
        return name;
    }

    Constructor getConstructor() {
        return constructor;
    }
}
