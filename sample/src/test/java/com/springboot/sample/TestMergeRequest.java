package com.springboot.sample;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.springboot.sample.bean.Users;
import com.springboot.sample.mapper.UsersDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@SpringBootTest(classes = SampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestMergeRequest {
    private static int threadCount = 30;

    private final static CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(threadCount); //为保证30个线程同时并发运行

    private final static ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(threadCount, threadCount, 0, TimeUnit.MINUTES, new ArrayBlockingQueue<>(threadCount),
            new ThreadFactoryBuilder()
            .setNameFormat("test-merge-request-%d").build());

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private UsersDao usersDao;

    @BeforeEach
    public void init() {

        Users users = new Users();
        users.setId(1L);
        users.setName("小明");
        users.setIsVip(0);
        users.setMoney(100L);
        usersDao.save(users);
        Users users1 = new Users();
        users1.setId(2L);
        users1.setName("李雷");
        users1.setIsVip(0);
        users1.setMoney(200L);
        usersDao.save(users1);
        Users users2 = new Users();
        users2.setId(3L);
        users2.setName("韩梅梅");
        users2.setIsVip(1);
        users2.setMoney(300L);
        usersDao.save(users2);
        Users users3 = new Users();
        users3.setId(4L);
        users3.setName("张三");
        users3.setIsVip(1);
        users3.setMoney(1000000L);
        usersDao.save(users3);
    }


    @Test
    public void testRequest() {

        List<Future<Boolean>> futureList = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {//循环开30个线程
            EXECUTOR.submit(() -> {
                COUNT_DOWN_LATCH.countDown();//每次减一
                try {
                    COUNT_DOWN_LATCH.await(); //此处等待状态，为了让30个线程同时进行
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (int j = 1; j <= 30; j++) {
                    int param = new Random().nextInt(4) + 1;
                    String responseBody = restTemplate.getForObject("http://localhost:8080/asyncAndMerge/merge?userId=" + param, String.class);
                    System.out.println(Thread.currentThread().getName() + "参数 " + param + " 返回值 " + responseBody);
                }

            });
        }

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
