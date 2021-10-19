package com.aliware.tianchi;

import com.aliware.tianchi.util.MyLog;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 集群实现
 * 必选接口，核心接口
 * 此类可以修改实现，不可以移动类或者修改包名
 * 选手需要基于此类实现自己的集群调度算法
 */
public class UserClusterInvoker<T> extends AbstractClusterInvoker<T> {
    private final static Logger logger = LoggerFactory.getLogger(UserClusterInvoker.class);
    public UserClusterInvoker(Directory<T> directory) {
        super(directory);
    }

    @Override
    protected Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
        MyLog.println("UserClusterInvoker.doInvoke.before");

        Result re=select(loadbalance, invocation, invokers, null).invoke(invocation);
//                .whenCompleteWithContext((r, t) -> {
//                    String value = r.getAttachment("TestKey");
//                    logger.info("TestKey From ClusterInvoker, value: " + value);
 //               });
        MyLog.println("UserClusterInvoker.doInvoke.after");

        return re;
    }
}
