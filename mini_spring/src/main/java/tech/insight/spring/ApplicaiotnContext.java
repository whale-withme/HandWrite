package tech.insight.spring;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
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
                .map(this::wrapper)
                .forEach(this::createBean);
    }

    private void createBean(BeanDefinition beanDefinition) {
        Object bean = null;
        if (ioc.containsKey(beanDefinition.getName())) {
            throw new RuntimeException("ioc container exists object bean");
        }

        Constructor<?> constructor = beanDefinition.getConstructor();
        try {
            bean = constructor.newInstance();
            System.out.println(beanDefinition.getName() + " init");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        ioc.put(beanDefinition.getName(), bean);
        return;
    }

    /*
     * class类创建beandefinition
     * @param type
     * @return beanDefinition
     */
    protected BeanDefinition wrapper(Class<?> type) {
        BeanDefinition beanDefinition = new BeanDefinition(type);
        if (beanDefnitionMap.containsKey(beanDefinition.getName())) {
            throw new RuntimeException();
        }
        beanDefnitionMap.put(beanDefinition.getName(), beanDefinition);
        return beanDefinition;
    }

    private List<Class<?>> scanPackage(String packageName) throws Exception {
        List<Class<?>> classList = new ArrayList<>();
        URL url = this.getClass().getClassLoader().getResource(packageName.replace(".", File.separator));
        Path path = Path.of(url.toURI());

        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path absolutePath = file.toAbsolutePath();
                if (absolutePath.toString().endsWith(".class")) {
                    String replaceStr = absolutePath.toString().replace(File.separator, ".");
                    int packageIndex = replaceStr.indexOf(packageName);
                    String className = replaceStr.substring(packageIndex, replaceStr.length() - ".class".length());
                    System.out.println(className);
                    try {
                        classList.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return classList;
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
