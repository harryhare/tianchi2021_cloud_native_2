package com.aliware.tianchi;

import com.aliware.tianchi.util.MyLog;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.remoting.TimeoutException;
import org.apache.dubbo.remoting.exchange.Response;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.support.RpcUtils;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 客户端过滤器（选址后）
 * 可选接口
 * 此类可以修改实现，不可以移动类或者修改包名
 * 用户可以在客户端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = CommonConstants.CONSUMER)
public class TestClientFilter implements Filter, BaseFilter.Listener {
    static long start = System.nanoTime();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Result result = invoker.invoke(invocation);
        try {
            MyLog.println("TestClientFilter.invoke.before");
            boolean isAsync = RpcUtils.isAsync(invoker.getUrl(), invocation);
            boolean isOneway = RpcUtils.isOneway(invoker.getUrl(), invocation);
            System.out.println(invoker.getUrl());
            System.out.printf("invocation attachment: %s\n", invocation.getAttachments());
            System.out.printf("invocation attr: %s \n", invocation.getAttributes());
            System.out.printf("rpc context: %s \n", RpcContext.getContext());
            System.out.printf("rpc client context: %s \n", RpcContext.getClientAttachment());
            System.out.printf("rpc server attachment: %s \n", RpcContext.getServerAttachment());
            System.out.printf("rpc server context: %s \n", RpcContext.getServerContext());
            System.out.printf("rpc service context: %s \n", RpcContext.getServiceContext());
            System.out.printf("isAsync:%s\n", isAsync);
            System.out.printf("isOneway:%s\n", isOneway);
            System.out.println(RpcUtils.getTimeout(invocation, -1));

            long cur=(System.nanoTime()-start)/1000_000_000;//0-240
            long timeout=(240-cur)*cur/5000+100;
            RpcContext.getClientAttachment().setAttachment("timeout", 100);
            result.get(100, TimeUnit.MILLISECONDS);
            MyLog.printf("TestClientFilter.invoke.after %s\n", result);
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            MyLog.println("timeout");
            //throw new RpcException();
            result.setValue(0);
            return result;
        }
    }

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
//        String value = appResponse.getAttachment("TestKey");
//        System.out.println("TestKey From Filter, value: " + value);

        MyLog.println("TestClientFilter.ok");
    }

    @Override
    public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {
        MyLog.println("TestClientFilter.err");
    }
}
