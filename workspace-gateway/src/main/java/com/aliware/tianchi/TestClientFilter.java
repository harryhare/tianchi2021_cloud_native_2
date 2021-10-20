package com.aliware.tianchi;

import com.aliware.tianchi.util.InvokersStat;
import com.aliware.tianchi.util.MyLog;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.remoting.TimeoutException;
import org.apache.dubbo.remoting.exchange.Response;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.support.RpcUtils;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext.getClientAttachment().setAttachment("timeout", 100);
        Result result = invoker.invoke(invocation);
        InvokersStat s = InvokersStat.getInstance();
        if (s != null) {
            long start = System.nanoTime();
            s.invoke(invoker);
//            result.whenCompleteWithContext((r, t) -> {
//                int duration = (int) (System.currentTimeMillis() - start);
//                if (r.hasException()) {
//                    s.err(invoker);
//                } else {
//                    s.ok(invoker);
//                }
//            });
        }
        return result;
    }

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
//        String value = appResponse.getAttachment("TestKey");
//        System.out.println("TestKey From Filter, value: " + value);
        MyLog.println("TestClientFilter.ok");
        InvokersStat s = InvokersStat.getInstance();
        if (s != null) {
            s.ok(invoker);
        }
    }

    @Override
    public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {
        MyLog.println("TestClientFilter.err");
        InvokersStat s = InvokersStat.getInstance();
        if (s != null) {
            s.err(invoker);
        }
    }
}
