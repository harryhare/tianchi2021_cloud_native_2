package com.aliware.tianchi;

import com.aliware.tianchi.util.InvokersStat;
import com.aliware.tianchi.util.MyLog;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.TimeoutException;
import org.apache.dubbo.remoting.exchange.Response;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.support.RpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(TestClientFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        InvokersStat s = InvokersStat.getInstance();
        if (s != null) {
            RpcContext.getClientAttachment().setAttachment("timeout", s.get_timout(invoker)/1000);
        } else {
            RpcContext.getClientAttachment().setAttachment("timeout", 100);
        }
        Result result = invoker.invoke(invocation);
        if (s != null) {
            long start = System.nanoTime();
            s.invoke(invoker);
            result.whenCompleteWithContext((r, t) -> {
                int duration = (int) (System.nanoTime() - start) / 1000;
                if (t == null) {
                    MyLog.printf("result.whenCompleteWithContext: %d\n", duration);
                    s.ok(invoker, duration);
                } else {
                    Throwable cause = t.getCause();
                    InvokersStat.ErrorType type = InvokersStat.ErrorType.OTHER;
                    if (cause instanceof TimeoutException) {
                        LOGGER.info("err:timeout");
                        type = InvokersStat.ErrorType.TIMEOUT;
                    } else if (cause instanceof RemotingException) {
                        LOGGER.info("err:server down");
                        type = InvokersStat.ErrorType.OFFLINE;
                    } else {
                        LOGGER.info("err:" + cause);
                    }
                    s.err(invoker, type);
                }
            });
        }
        return result;
    }

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
//        String value = appResponse.getAttachment("TestKey");
//        System.out.println("TestKey From Filter, value: " + value);

        MyLog.printf("TestClientFilter.ok.%s\n", invoker.getUrl().getHost());
//        InvokersStat s = InvokersStat.getInstance();
//        if (s != null) {
//            s.ok(invoker);
//        }
    }

    @Override
    public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {
        MyLog.println("TestClientFilter.err");
//        InvokersStat s = InvokersStat.getInstance();
//        if (s != null) {
//            Throwable cause = t.getCause();
//            InvokersStat.ErrorType type = InvokersStat.ErrorType.OTHER;
//            if (cause instanceof TimeoutException) {
//                LOGGER.info("err:timeout");
//                type = InvokersStat.ErrorType.TIMEOUT;
//            } else if (cause instanceof RemotingException) {
//                LOGGER.info("err:server down");
//                type = InvokersStat.ErrorType.OFFLINE;
//            } else {
//                LOGGER.info("err:" + cause);
//            }
//            s.err(invoker, type);
//        }
    }
}
