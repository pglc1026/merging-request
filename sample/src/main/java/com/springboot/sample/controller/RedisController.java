package com.springboot.sample.controller;

import com.springboot.sample.bean.Message;
import com.springboot.sample.redis.DelayingQueueService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private static String USER_CHANNEL = "USER_CHANNEL";

    @Resource
    private DelayingQueueService delayingQueueService;

    /**
     * 发送消息
     *
     * @param msg
     */
    @RequestMapping("/sendMessage")
    public String sendMessage(String msg, long delay) {
        try {
            if (msg != null) {
                String seqId = UUID.randomUUID().toString();
                Message message = new Message();
                //时间戳默认为毫秒 延迟5s即为 5*1000
                long time = System.currentTimeMillis();
                LocalDateTime dateTime = Instant.ofEpochMilli(time).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
                message.setDelayTime(time +  (delay * 1000));
                message.setCreateTime(dateTime);
                message.setBody(msg);
                message.setId(seqId);
                message.setChannel(USER_CHANNEL);
                delayingQueueService.push(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }
}