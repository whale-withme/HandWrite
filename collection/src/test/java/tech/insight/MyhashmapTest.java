package tech.insight;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 *  
 */
public class MyhashmapTest {
    
    @Test
    public void hashmapTest(){
        MyHashMap<String, String> hashMap = new MyHashMap<>();
        int cnt = 1000000;
        for(int i = 0; i < cnt; i++){
            hashMap.put(String.valueOf(i), String.valueOf(i));
        }

        assertEquals(hashMap.size(), cnt);

        assertEquals(hashMap.remove(String.valueOf(cnt-1)), String.valueOf(cnt-1));

    }
}
