package com.springboot.sample;

import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;

public class TestBatch {
    private static int threadCount = 30;

    private final static CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(threadCount); //为保证30个线程同时并发运行

    private static final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {


        for (int i = 0; i < threadCount; i++) {//循环开30个线程
            new Thread(new Runnable() {
                public void run() {
                    COUNT_DOWN_LATCH.countDown();//每次减一
                    try {
                        COUNT_DOWN_LATCH.await(); //此处等待状态，为了让30个线程同时进行
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    for (int j = 0; j < 10; j++) {
                        String responseBody = restTemplate.getForObject("http://localhost:8080/asyncAndMerge/merge?userId=" + j, String.class);
                        System.out.println(Thread.currentThread().getName() + "参数 " + j + " 返回值 " + responseBody);
                    }
                }
            }).start();

        }
    }
}
