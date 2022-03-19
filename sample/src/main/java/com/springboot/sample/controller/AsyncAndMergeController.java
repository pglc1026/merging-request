package com.springboot.sample.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

/*
* zzq
* 2022年3月19日11:00:18
* 异步和请求合并
* */
@RestController
@RequestMapping("/asyncAndMerge")
public class AsyncAndMergeController {

    /*** 异步，不阻塞Tomcat的线程 ***/
    @RequestMapping("/async")
    public Callable<String> async() {
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



}
