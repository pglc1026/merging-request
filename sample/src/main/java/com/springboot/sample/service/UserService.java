package com.springboot.sample.service;

import com.springboot.sample.bean.Users;
import com.springboot.sample.service.impl.UserWrapBatchService;

import java.util.List;
import java.util.Map;

public interface UserService {

    Map<String, Users> queryUserByIdBatch(List<UserWrapBatchService.Request> userReqs);
}
