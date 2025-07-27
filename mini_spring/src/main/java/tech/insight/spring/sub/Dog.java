
package tech.insight.spring.sub;

import tech.insight.spring.Component;
import tech.insight.spring.PostConstruct;

/**
 *  
 */
@Component(name = "dog")
public class Dog {
    
    @PostConstruct
    public void init(){
        System.out.println("dog @postConstruct execute");
    }

}
