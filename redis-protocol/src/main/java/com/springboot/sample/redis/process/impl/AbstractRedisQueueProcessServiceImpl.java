package com.springboot.sample.redis.process.impl;

import com.springboot.sample.bean.RedisQueueMessage;
import com.springboot.sample.bean.RedisQueueProcessResp;
import com.springboot.sample.redis.DelayingQueueService;
import com.springboot.sample.redis.process.RedisQueueProcessService;
import org.springframework.http.HttpStatus;

import javax.annotation.Resource;

public abstract class AbstractRedisQueueProcessServiceImpl implements RedisQueueProcessService {

    @Resource
    private DelayingQueueService delayingQueueService;

    protected abstract RedisQueueProcessResp doHandler(RedisQueueMessage redisQueueMessage);

    @Override
    public RedisQueueProcessResp handler(RedisQueueMessage redisQueueMessage) {
        RedisQueueProcessResp result;
        try {
            result =  doHandler(redisQueueMessage);
        }catch (Exception e){
            // 执行出现异常重新加入队列
            delayingQueueService.push(redisQueueMessage);
            throw new RuntimeException("执行业务代码程序异常");
        }
        ifFailAgainAddQueue(redisQueueMessage,result);
        return result;
    }

    protected  void ifFailAgainAddQueue(RedisQueueMessage redisQueueMessage, RedisQueueProcessResp result){
        if (HttpStatus.OK.value() != result.getCode()){
            // 错误要重新加入队列
            delayingQueueService.push(redisQueueMessage);
        }
    }

}
