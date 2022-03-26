package com.springboot.sample.controller;

import com.springboot.sample.bean.Users;
import com.springboot.sample.service.TestService;
import com.springboot.sample.service.impl.UserWrapBatchQueueService;
import com.springboot.sample.service.impl.UserWrapBatchService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * zzq
 * 2022年3月19日11:00:18
 * 异步和请求合并
 * */
@RestController
@RequestMapping("/asyncAndMerge")
public class AsyncAndMergeController {

    @Resource
//    private UserWrapBatchService userBatchService;
    private UserWrapBatchQueueService userWrapBatchQueueService;

    @Resource
    private TestService testService;
    @Resource
    private ExecutorService executorService;


    /*** 异步，不阻塞Tomcat的线程 ，提升Tomcat吞吐量***/
    @RequestMapping("/async")
    public DeferredResult<String> async() {
        System.out.println(" 当前线程 外部 " + Thread.currentThread().getName());
        DeferredResult<String> result = new DeferredResult<>();
        CompletableFuture.supplyAsync(testService::testDeferredResult, executorService)
                .whenCompleteAsync((res, throwable) -> result.setResult(res));
        return result;
    }

    /*** 异步，不阻塞Tomcat的线程 ，提升Tomcat吞吐量***/
    @RequestMapping("/async2")
    public Callable<String> async2() {
        System.out.println(" 当前线程 外部 " + Thread.currentThread().getName());
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println(" 当前线程 内部 " + Thread.currentThread().getName());
                return "success";
            }
        };
        return callable;
    }

    /***
     * 请求合并
     * */
/*    @RequestMapping("/merge")
    public Users merge(Long userId) {
       return userBatchService.queryUser(userId);
    }*/

    /***
     * 请求合并
     * */
    @RequestMapping("/merge")
    public Callable<Users> merge(Long userId) {
        return new Callable<Users>() {
            @Override
            public Users call() throws Exception {
//                return userBatchService.queryUser(userId);
                return userWrapBatchQueueService.queryUser(userId);
            }
        };
    }

}
