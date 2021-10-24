package com.aliware.tianchi;

import com.aliware.tianchi.util.InvokersStat;
import com.aliware.tianchi.util.MyLog;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Constants;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 负载均衡扩展接口
 * 必选接口，核心接口
 * 此类可以修改实现，不可以移动类或者修改包名
 * 选手需要基于此类实现自己的负载均衡算法
 */
public class UserLoadBalance implements LoadBalance {
    private boolean inited = false;

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        if (!inited) {
            InvokersStat.init(invokers);
            inited = true;
        }
        MyLog.println("LoadBalance.select.before");
        //int r = ThreadLocalRandom.current().nextInt(invokers.size());
        int r = InvokersStat.getInstance().chooseByWeight();
        //int r = InvokersStat.getInstance().chooseByQueue();
        MyLog.printf("LoadBalance.select.%s\n",invokers.get(r).getUrl().getHost());
        MyLog.println("LoadBalance.select.after");
        return invokers.get(r);
    }
}
