package tech.insight.spring;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  
 */
public class ApplicaiotnContext {

    private final Map<String, Object> ioc = new HashMap<>();

    private final Map<String, BeanDefinition> beanDefnitionMap = new HashMap<>();

    ApplicaiotnContext(String packageName) throws Exception {
        scanPackage(packageName).stream()
                .filter(f -> f.isAnnotationPresent(Component.class))
                .forEach(this::wrapper);
    }

    protected void wrapper(Class<?> type) {
        BeanDefinition beanDefinition = new BeanDefinition(type);
        if (beanDefnitionMap.containsKey(beanDefinition.getName())) {
            throw new RuntimeException();
        }
        beanDefnitionMap.put(beanDefinition.getName(), beanDefinition);
    }

    private List<Class<?>> scanPackage(String packageName) throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource(packageName.replace(".", File.separator));
        Path path = Path.of(url.toURI());

        // todo..
        return null;
    }

    public Object getBean(String name) {
        return null;
    }

    public <T> T getBean(Class<T> type) {
        return null;
    }

    public <T> List<T> getBeans(Class<T> type) {
        return null;
    }
}
