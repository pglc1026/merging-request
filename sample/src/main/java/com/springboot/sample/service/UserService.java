package com.springboot.sample.service;

import com.springboot.sample.bean.Users;
import com.springboot.sample.service.impl.UserWrapBatchQueueService;
import com.springboot.sample.service.impl.UserWrapBatchService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

    Map<String, Optional<Users>> queryUserByIdBatch(List<UserWrapBatchService.Request> userReqs);

    /***
     * zzq
     * 使用队列的方式
     * 2022年3月26日16:00:16
     * */
    Map<String, Optional<Users>> queryUserByIdBatchQueue(List<UserWrapBatchQueueService.Request> userReqs);

    long sumRecord(int toId, int fromId);
}
