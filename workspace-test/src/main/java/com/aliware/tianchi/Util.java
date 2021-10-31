package com.aliware.tianchi;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Util {
    static double peekInMap(PriorityQueue<Request> q, Map<Integer, Integer> wait) {
        Request r = q.peek();

        while (r != null && wait.get(r.id) == null) {
            q.poll();
            r = q.peek();
        }
        if (r == null) {
            return Double.MAX_VALUE;
        }
        return r.time_out_queue;
    }

    static double peekInSet(PriorityQueue<Request> q, Set<Integer> wait) {
        Request r = q.peek();
        while (r != null && !wait.contains(r.id)) {
            q.poll();
            r = q.peek();
        }
        if (r == null) {
            return Double.MAX_VALUE;
        }
        return r.time_out_queue;
    }

}
