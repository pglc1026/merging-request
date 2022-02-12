package com.springboot.sample.redis;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.springboot.sample.bean.Message;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Set;
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

    /**
     * 可以不同业务用不同的key
     */
    public static final String QUEUE_NAME = "message:queue";


    /**
     * 插入消息
     *
     * @param message
     * @return
     */
    public Boolean push(Message message) throws JsonProcessingException {
        Boolean addFlag = redisTemplate.opsForZSet().add(QUEUE_NAME, mapper.writeValueAsString(message), message.getDelayTime());
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
            remove = redisTemplate.opsForZSet().remove(QUEUE_NAME, mapper.writeValueAsString(message));
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
    public List<Message> pull() {
        Set<String> strings = redisTemplate.opsForZSet().rangeByScore(QUEUE_NAME, 0, System.currentTimeMillis());
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
        msgList.forEach(val -> {
            if (remove(val)) { // 为true 表示抢占到了

            }
        });
        return msgList;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    List<Message> pull = pull();

                    System.out.println("拉取的数据 " + pull);
                    pull.forEach(v -> {
                        Boolean remove = remove(v);
                        System.out.println("删除是否成功 " + remove);
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }
}
