
package tech.insight.spring;

import java.lang.reflect.Constructor;

/**
 *  
 */
public class BeanDefinition {

    private final Class<?> beanType;
    private String name;
    private final Constructor<?> constructor;

    BeanDefinition(Class<?> type) {
        this.beanType = type;
        Component component = type.getDeclaredAnnotation(Component.class);
        this.name = component.name().isEmpty() ? type.getPackageName() : component.name();
        this.constructor = getConstructor(); // 无参构造函数
    }

    String getName() {
        return null;
    }

    Constructor getConstructor() {
        return null;
    }
}
