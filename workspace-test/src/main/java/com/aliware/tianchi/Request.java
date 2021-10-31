package com.aliware.tianchi;

import java.util.Comparator;

public class Request {
    int id;
    int duration;//1e-6
    double time_out_queue;// 出队列时间

    Request(int id, double time_out_queue, int duration) {
        this.id = id;
        this.time_out_queue = time_out_queue;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return String.format("{%d:%.2f}", this.id, this.time_out_queue);
    }

    static Comparator<Request> comparator = Comparator.comparingDouble(o -> o.time_out_queue);
}
