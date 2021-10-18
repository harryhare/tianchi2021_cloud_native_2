package com.aliware.tianchi;

import com.aliware.tianchi.util.MyLog;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.remoting.exchange.Response;
import org.apache.dubbo.rpc.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            MyLog.println("TestClientFilter.invoke.before");
//            Map<String, String> attachments = invocation.getAttachments();
//            attachments.get(Constants.TIMEOUT_KEY);
            CompletableFuture<Result> f1 = CompletableFuture.supplyAsync(new Supplier<Result>() {
                @Override
                public Result get() {
                    Result r=invoker.invoke(invocation);
                    try {
                        r.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return r;
                }
            });
            CompletableFuture<Result> f2 = CompletableFuture.supplyAsync(new Supplier<Result>() {
                @Override
                public Result get() {
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //return new AppResponse();

//                    CompletableFuture b = new CompletableFuture ();
//                    AsyncRpcResult asyncRpcResult = new AsyncRpcResult(b,invocation);
//                    asyncRpcResult.setValue(0);
//                    return asyncRpcResult;
                    return null;
                }
            });
            CompletableFuture<Object> f = CompletableFuture.anyOf(f1, f2);
            //CompletableFuture<Object> f = CompletableFuture.anyOf(f1);
            Result result = (Result) f.get();
            MyLog.printf("TestClientFilter.invoke.after %s\n", result);
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RpcException();
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
