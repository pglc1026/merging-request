package com.springboot.sample.redis;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.springboot.sample.bean.Message;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @description: 延时队列功能类
 * @author: smalljop
 * @create: 2020-01-03 10:11
 **/
@Component
public class DelayingQueueService implements InitializingBean {

    private static ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();

    @Resource
    private RedisTemplate redisTemplate;

    private static final int SELECTOR_AUTO_REBUILD_THRESHOLD = 1;

    // deadline 以及任务穿插逻辑处理  ，业务处理事件可能是2毫秒
    private  long timeoutMillis = TimeUnit.MILLISECONDS.toNanos(2);
    /**
     * 可以不同业务用不同的key
     */
    @Value("${redisQueue.name}")
    public static final String queueName = "redis_queue";


    /**
     * 插入消息
     *
     * @param message
     * @return
     */
    public Boolean push(Message message) throws JsonProcessingException {
        Boolean addFlag = redisTemplate.opsForZSet().add(queueName, mapper.writeValueAsString(message), message.getDelayTime());
        return addFlag;
    }

    /**
     * 移除消息
     *
     * @param message
     * @return
     */
    public Boolean remove(Message message) {
        Long remove = 0L;
        try {
            remove = redisTemplate.opsForZSet().remove(queueName, mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return remove > 0 ? true : false;
    }


    /**
     * 拉取最新需要
     * 被消费的消息
     * rangeByScore 根据score范围获取 0-当前时间戳可以拉取当前时间及以前的需要被消费的消息
     *
     * @return
     */
    public Message pop() {
        Set<String> strings = redisTemplate.opsForZSet().rangeByScore(queueName, 0, System.currentTimeMillis());
        if (strings == null) {
            return null;
        }
        List<Message> msgList = strings.stream().map(msg -> {
            Message message = null;
            try {
                message = mapper.readValue(msg, Message.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return message;
        }).collect(Collectors.toList());

        // 有可能是集群，多个节点，设置抢占
        for (Message message : msgList) {
            if (remove(message)) { // 为true 表示抢占到了
                return message;
            }
        }
        return null;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Thread thread = new Thread() {
            @Override
            public void run() {
                int selectCnt = 0;

                while (true) {
                    long currentTimeNanos = System.nanoTime();

                    Message pull = pop();

                    System.out.println("拉取的数据 " + pull);
                    selectCnt++;


                    // 解决空轮询问题
                    long time = System.nanoTime();
                    System.out.println("执行纳秒数" + (time - currentTimeNanos));
                    System.out.println(time + " -- " + (time - TimeUnit.MILLISECONDS.toNanos(timeoutMillis)) + "--" + currentTimeNanos);
                    // 当前时间减去阻塞使用的时间  >= 上面的当前时间
                    if (time - timeoutMillis >= currentTimeNanos) {
                        // 有效的轮询
                        selectCnt = 1;
                    } else if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
                        // 如果空轮询次数大于等于SELECTOR_AUTO_REBUILD_THRESHOLD 默认512
                        selectCnt = 1;
                        threadSleep();
                    }

                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    private void threadSleep(){
        try {
            System.out.println("睡眠了");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
