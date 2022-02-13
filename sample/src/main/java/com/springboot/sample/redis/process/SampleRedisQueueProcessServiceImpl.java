package com.springboot.sample.redis.process;

import com.springboot.sample.bean.RedisQueueMessage;
import com.springboot.sample.bean.RedisQueueProcessResp;
import com.springboot.sample.redis.process.impl.AbstractRedisQueueProcessServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class SampleRedisQueueProcessServiceImpl extends AbstractRedisQueueProcessServiceImpl  {


    @Override
    protected RedisQueueProcessResp doHandler(RedisQueueMessage redisQueueMessage) {
        System.out.println("消费到数据 " + redisQueueMessage);
//                int a = 1 / 0;
        return RedisQueueProcessResp.success();
    }
}
