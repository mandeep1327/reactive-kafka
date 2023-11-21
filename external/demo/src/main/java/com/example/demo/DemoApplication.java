package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
     // setLRUCache(1,1000);

       // SpringApplication.run(DemoApplication.class, args);
    }
//    public static void setLRUCache(int key,int value){
//        HashMap<Integer,Integer> map=new HashMap<Integer,Integer>();
//        LinkedList doubleQueue=new LinkedList();
//            if(!doubleQueue.contains(key)) {
//                map.put(key, map.getOrDefault(value) + 1);
//                doubleQueue.removeLast();
//            }
//            else {
//                doubleQueue.add(key,value);
//            }
//
//            doubleQueue.add(map.get(key));
//        }
//        public static Map<Integer,Integer> getCache(int key){
//            return map.get(key);
//        }
//    }

}
