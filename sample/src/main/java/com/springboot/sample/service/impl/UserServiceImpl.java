package com.springboot.sample.service.impl;

import com.springboot.sample.bean.Users;
import com.springboot.sample.mapper.UsersDao;
import com.springboot.sample.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UsersDao usersDao;

    @Override
    public Map<String, Optional<Users>> queryUserByIdBatch(List<UserWrapBatchService.Request> userReqs) {
        // 全部参数
        List<Long> userIds = userReqs.stream().map(UserWrapBatchService.Request::getUserId).collect(Collectors.toList());
        // 用in语句合并成一条SQL，避免多次请求数据库的IO
        List<Users> userList = usersDao.findAllById(userIds);
        Map<Long, Users> userGroup = userList.stream().collect(Collectors.toMap(Users::getId, data -> data, (ov, nv) -> ov));
        Map<String, Optional<Users>> result = new HashMap<>();
        userReqs.forEach(val -> {
            Users user = userGroup.get(val.getUserId());
            if (Objects.nonNull(user)) {
                result.put(val.getRequestId(), Optional.of(user));
            } else {
                // 表示没数据
                result.put(val.getRequestId(), Optional.empty());
            }
        });
        return result;
    }

    @Override
    public Map<String, Optional<Users>> queryUserByIdBatchQueue(List<UserWrapBatchQueueService.Request> userReqs) {
        // 全部参数
        List<Long> userIds = userReqs.stream().map(UserWrapBatchQueueService.Request::getUserId).collect(Collectors.toList());
        // 用in语句合并成一条SQL，避免多次请求数据库的IO
        List<Users> userList = usersDao.findAllById(userIds);
        Map<Long, Users> userGroup = userList.stream().collect(Collectors.toMap(Users::getId, data -> data, (ov, nv) -> ov));
        Map<String, Optional<Users>> result = new HashMap<>();
        // 数据分组
        userReqs.forEach(val -> {
            Users user = userGroup.get(val.getUserId());
            if (Objects.nonNull(user)) {
                result.put(val.getRequestId(), Optional.of(user));
            } else {
                // 表示没数据 , 这里要new，不然加入队列会空指针
                result.put(val.getRequestId(), Optional.empty());
            }
        });
        return result;
    }


    @Override
    public long sumRecord(int toId, int fromId) {
        // 用in语句合并成一条SQL，避免多次请求数据库的IO
        List<Users> users = usersDao.selectSum(fromId, toId);
        if (!CollectionUtils.isEmpty(users)) {
            return users.get(0).getMoney();
        }
        return 0;
    }
}