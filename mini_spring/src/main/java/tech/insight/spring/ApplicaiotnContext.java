package tech.insight.spring;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
import tech.insight.spring.Util.*;

/**
 *  
 */
public class ApplicaiotnContext {

    private final Map<String, Object> ioc = new HashMap<>();

    private final Map<String, BeanDefinition> beanDefnitionMap = new HashMap<>();

    private final Map<String, Object> loadingIoc = new HashMap<>();

    ApplicaiotnContext(String packageName) throws Exception {
        scanPackage(packageName).stream()
                .filter(f -> f.isAnnotationPresent(Component.class))
                .forEach(this::wrapper);
        beanDefnitionMap.values().forEach(this::createBean);
    }

    private Object createBean(BeanDefinition beanDefinition) {
        Object bean = null;
        if (ioc.containsKey(beanDefinition.getName())) {
            return ioc.get(beanDefinition.getName());
        }

        if(loadingIoc.containsKey(beanDefinition.getName())){
            return loadingIoc.get(beanDefinition.getName());
        }

        Constructor<?> constructor = beanDefinition.getConstructor();
        try {
            bean = constructor.newInstance();
            loadingIoc.put(beanDefinition.getName(), bean);
            autowireInit(bean, beanDefinition);
            Util.Dprintf(beanDefinition.getName() + "初始化");
            bean = initializeBean(bean, beanDefinition); // 初始化bean
            loadingIoc.remove(beanDefinition.getName());
            ioc.put(beanDefinition.getName(), bean);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return bean;
    }

    private void autowireInit(Object bean, BeanDefinition beanDefinition) throws IllegalArgumentException, IllegalAccessException {
        for(Field autowiredField : beanDefinition.getAutowiredFields()){
            autowiredField.setAccessible(true);
            autowiredField.set(bean, getBean(autowiredField.getType()));
        }
    }

    private Object initializeBean(Object bean, BeanDefinition beanDefinition) throws Exception {
        Method postConstructMethod = beanDefinition.getPostConstructMethod();
        if (postConstructMethod != null) {
            postConstructMethod.invoke(bean);
        }

        return bean;
    }

    /*
     * class类创建beandefinition
     * 
     * @param type
     * 
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
                    Util.Dprintf(className);
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
        Object bean = ioc.get(name);
        if(bean != null){
            return bean;
        }

        if(beanDefnitionMap.containsKey(name)){
            return createBean(beanDefnitionMap.get(name));
        }
        return null;
    }

    public <T> T getBean(Class<T> type) {
        String beandefName = beanDefnitionMap.values().stream()
                .filter(bd -> type.isAssignableFrom(bd.getBeanType()))
                .map(BeanDefinition::getName)
                .findFirst().orElse(null);
        return (T)getBean(beandefName);     // 方法重载给name创建
    }

    public <T> List<T> getBeans(Class<T> type) {
        return ioc.values().stream()
                .filter(bean -> type.isAssignableFrom(bean.getClass()))
                .map(bean -> (T) bean)
                .toList();
    }
}
