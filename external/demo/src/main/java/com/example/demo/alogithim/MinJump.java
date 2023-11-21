package com.example.demo.alogithim;

public class MinJump {
    static int minJumps(int arr[], int source, int destination)
    {
        // Base case: when source
        // and destination are same
        if (source == destination)
            return 0;

        // When nothing is reachable
        // from the given source
        if (arr[source] == 0)
            return Integer.MAX_VALUE;

        // Traverse through all the points
        // reachable from arr[source]. Recursively
        // get the minimum number of jumps
        // needed to reach arr[destination`] from these
        // reachable points.
        int min = Integer.MAX_VALUE;
        for (int i = source + 1; i <= destination && i <= source + arr[source];
             i++) {
            int jumps = minJumps(arr, i, destination);
//            if (jumps != Integer.MAX_VALUE
//                    && jumps + 1 < min)
                min = jumps + 1;
        }
        return min;
    }

    // Driver code
    public static void main(String args[])
    {
        int arr[] = { 1, 3, 5, 8};
        int n = arr.length;
        System.out.print(
                "Minimum number of jumps to reach end is "
                        + minJumps(arr, 0, n - 1));
    }
}
