package tech.insight.spring.sub;

import tech.insight.spring.Autowired;
import tech.insight.spring.Component;
import tech.insight.spring.PostConstruct;

/**
 *  
 */
@Component(name = "dog")
public class Dog {

    private String name;

    @Autowired
    private Cat cat;
    
    @PostConstruct
    public void init(){
        this.name = "kk";
        System.out.println("dog 的名字是" + name + cat);
    }

}
