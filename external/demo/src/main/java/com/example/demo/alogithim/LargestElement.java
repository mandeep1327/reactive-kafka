package com.example.demo.alogithim;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LargestElement {
    public static void main(String[] args) {
        int[] num={4,-2};
        int largest=IntStream.range(0,num.length)
                .mapToObj(i->num[num.length-i-1])
                .sorted()
                .findFirst().orElse(null);
            //    .get();
        System.out.println(largest);
      int indexELement= findIndex(num,5);
      System.out.println(indexELement);
      int maxsum=maxSum(num);
        System.out.println(maxsum);
    }
    public static int findIndex(int[] num,int t ){
        return IntStream.range(0,num.length)
                .filter(i->t==num[i])
                .findFirst()
                .orElse(-1);
    }

    public static int maxSum(int[] array){
        int max_sum=0; //This variable will keep adding the individual elements in the array and hold the sum till that point in the array
        int max_store=Integer.MIN_VALUE; //This variable will store the maximum value stored in max_sum at any point of time.
        for(int i=0;i<array.length;i++){
            max_sum=max_sum+array[i];
            if( max_sum > max_store ) {
                max_store = max_sum;
            }
            if( max_sum < 0 ) {
                max_sum = 0;
            }
        }
return max_store;
    }
}
