package com.example.demo.alogithim;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class LRUCacheImpl {
    Map<Integer, Integer> data = new HashMap<Integer, Integer>(); //data store
    LinkedList<Integer> cache = new LinkedList<Integer>();  //cache store
    Integer capacity; //size of cache

    public LRUCacheImpl(Integer capacity) {
        this.capacity = capacity;
    }

    public void put(int key, Integer val) {
        // Check if the cache is full
        if (cache.size() >= capacity) {
            // if the cache is full, then remove the last element
            int keyRemoved = cache.removeLast(); // remove from cache
            data.remove(keyRemoved);// remove from data
        }
        // add the new cache
        cache.addFirst(key);// add to top of the list
        data.put(key, val);// add to data

    }

    public Integer get(Integer key) {
        Integer value = data.get(key); //get the value from map using the key
        if (value != null) {
            //if the data is present then we need to update the access cache
            cache.remove(key);
            cache.addFirst(key);
        }
        return value;

    }

    public static void main(String[] args) {
        LRUCacheImpl lRUCache = new LRUCacheImpl(2);
        lRUCache.put(1, 1); // cache is {1=1}
        lRUCache.put(2, 2); // cache is {1=1, 2=2}
        System.out.println(lRUCache.get(1));    // return 1
        lRUCache.put(3, 3); // LRU key was 2, evicts key 2, cache is {1=1, 3=3}
        System.out.println(lRUCache.get(2));    // returns null (not found)
        lRUCache.put(4, 4); // LRU key was 1, evicts key 1, cache is {4=4, 3=3}
        System.out.println(lRUCache.get(1));    // return null (not found)
        System.out.println(lRUCache.get(3));    // return 3
        System.out.println(lRUCache.get(4));    // return 4
    }

}
