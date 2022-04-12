package com.springboot.sample;

import com.springboot.sample.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/***
 * ForkJoin测试，寻找最大数
 * */
@RunWith(SpringRunner.class)
@SpringBootTest/*(classes = SampleApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)*/
public class ForkJoinTest {


    @Resource
    private UserService userService;


    // 如果是继承RecursiveAction无返回值
    public static class MyTask extends RecursiveTask<Integer> {

        int innerSrc[];

        public MyTask(int[] innerSrc) {
            this.innerSrc = innerSrc;
        }

        @Override
        protected Integer compute() {

            if (innerSrc.length <= 2) {
                // 子任务已经比较小了，可以直接比较
                if (innerSrc.length == 1) {
                    return innerSrc[0];

                } else {
                    return innerSrc[0] > innerSrc[1] ? innerSrc[0] : innerSrc[1];
                }
            } else {
                // 子任务比较大，要拆分

                int mid = innerSrc.length / 2;
                MyTask left = new MyTask(Arrays.copyOf(innerSrc, mid));
                MyTask right = new MyTask(Arrays.copyOfRange(innerSrc, mid, innerSrc.length));
                invokeAll(left, right);
                return left.join() > right.join() ? left.join() : right.join();
            }
        }
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
    /*    int[] src = {1, 23, 46, 7, 89, 43, 67};
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 2);
        MyTask myTask = new MyTask(src);
        pool.invoke(myTask);
        System.out.println("最大值:" + myTask.join());*/


/*
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 2);
        // 模拟千万数据
        int min = 1;
        int max = 10000000;
        SunTask sunTask = new SunTask(min, max);
        pool.invoke(sunTask);

        System.out.println("总数 " + sunTask.join() +
                " 执行时间 " + (System.currentTimeMillis() - startTime));
*/

    }


    @Test
    public void sunTask() {
        long startTime = System.currentTimeMillis();
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 2);
        // 模拟千万数据
        int min = 1;
        int max = 10556466;
        SumTask sumTask = new SumTask(min, max,userService);
        pool.invoke(sumTask);

        System.out.println("总数 " + sumTask.join() +
                " 执行时间 " + (System.currentTimeMillis() - startTime));

    }

    public static final Integer THRESHOLD = 1000000;

    public static class SumTask extends RecursiveTask<Long> {

        int fromId;
        int toId;
        private UserService userService;


        public SumTask(int fromId, int toId, UserService userService) {
            this.fromId = fromId;
            this.toId = toId;
            this.userService = userService;
        }

        @Override
        protected Long compute() {
            if (toId - fromId < THRESHOLD) {
                return sumRecord(toId, fromId);
            } else {
                int mid = (fromId + toId) / 2;
                SumTask left = new SumTask(fromId, mid, userService);
                SumTask right = new SumTask(mid + 1, toId, userService);
                invokeAll(left, right);
                return left.join() + right.join();
            }
        }

        public Long sumRecord(int toId, int fromId) {
            System.out.println(" 参数 " + fromId + " " + toId);
            return  userService.sumRecord(toId, fromId);
        }


    }


}
