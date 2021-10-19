# nacos
https://nacos.io/zh-cn/docs/quick-start.html
下载：
https://github.com/alibaba/nacos/releases
nacos-server-2.0.3.zip

运行：
startup.cmd -m standalone

控制台的默认用户名密码：
http://192.168.56.1:8848/nacos/index.html
http://127.0.0.1:8848/nacos/index.html
nacos
nacos

## wrk
windows 不支持，要用docker
https://blog.csdn.net/fwhezfwhez/article/details/90371710

## 本地测试地址
curl http://localhost:8087/invoke
provider 的运行参数 JVM option 
-Dquota=small
-Dquota=medium
-Dquota=large

## 时序
1 TestClientClusterFilter:invoke before
	2 UserClusterInvoker.doInvoke.before
		3 LoadBalance.select.before
		3 LoadBalance.select.after
		4 TestClientFilter.invoke.before
		4 TestClientFilter.invoke.after
	2 UserClusterInvoker.doInvoke.after	
1 TestClientClusterFilter:invoke after

5 TestClientFilter.err
6 TestClientClusterFilter.err

## completefutrue
```text
            RpcContext.getClientAttachment().setAttachment("timeout", 10);
            CompletableFuture<Result> f1 = CompletableFuture.supplyAsync(new Supplier<Result>() {
                @Override
                public Result get() {
                    Result r = invoker.invoke(invocation);
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

                    Result rpcResult = new AppResponse();
                    rpcResult.setValue(0);
                    return rpcResult;

//                    CompletableFuture b = new CompletableFuture ();
//                    AsyncRpcResult asyncRpcResult = new AsyncRpcResult(b,invocation);
//                    asyncRpcResult.setValue("");
//                    return asyncRpcResult;
                    //return null;
                }
            });
//            CompletableFuture<Object> f = CompletableFuture.anyOf(f1, f2);
            //CompletableFuture<Object> f = CompletableFuture.anyOf(f1);
//            Result result = (Result) f.get();

//            Result x = new AppResponse();
//            x.setValue(0);
//            CompletableFuture<AppResponse> r = new CompletableFuture<>(x);
//            AsyncRpcResult result = new AsyncRpcResult(r, invocation);
            //           result.setValue("");
//            Result result = new AppResponse();
//            result.setValue(CompletableFuture.completedFuture(0));
//            result.setValue(0);
```