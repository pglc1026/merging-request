package com.springboot.sample;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/***
 * ForkJoin测试，寻找最大数
 * */
public class ForkJoinTest {
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


        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 2);
        // 模拟千万数据
        int min = 1;
        int max = 10000000;
        SunTask sunTask = new SunTask(min, max);
        pool.invoke(sunTask);

        System.out.println("总数 " + sunTask.join() +
                " 执行时间 " + (System.currentTimeMillis() - startTime));

    }

    public static final Integer THRESHOLD = 5000;

    public static class SunTask extends RecursiveTask<Long> {

        int fromId;
        int toId;

        public SunTask(int fromId, int toId) {
            this.fromId = fromId;
            this.toId = toId;
        }

        @Override
        protected Long compute() {
            if (toId - fromId < THRESHOLD) {
                return sumRecord(toId, fromId);
            } else {
                int mid = (fromId + toId) / 2;
                SunTask left = new SunTask(fromId, mid);
                SunTask right = new SunTask(mid + 1, toId);
                invokeAll(left, right);

                return left.join() + right.join();
            }
        }


    }

    public static Long sumRecord(int toId, int fromId) {
        // 模拟查询数据库, 延迟执行
        String sql = "select xx from xxx.... where id > " + fromId + " and id < " + toId;
        System.out.println(" 参数 " + fromId +" " + toId);
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1L;
    }
}
