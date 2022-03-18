package com.yun.server.utils;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtils {

    // 参数的含义：
    //1.初始化时候线程池内有20个线程
    //2.最大可扩充到100个线程
    //3和4参数表示新增出来的线程如果在60s内没有被执行，就会被回收，最后保留20
    //5.当大量的任务过来时，20根线程都被占用了，但是并不会立刻去创建新的线程，而是将任务放入queue队列中
    //线程处理完之后会再次回来继续处理这里面的请求，只有当处理不过来的请求数目超过capacity10个时，才扩展线程
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20, 100, 60,
            TimeUnit.SECONDS, new LinkedBlockingDeque<>(10));

    public static void add(Runnable runnable){
        threadPoolExecutor.execute(runnable);
    }
}
