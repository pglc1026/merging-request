package com.springboot.sample.service.impl;

import com.springboot.sample.annotation.ConditionalValidate;
import com.springboot.sample.bean.Users;
import com.springboot.sample.mapper.UsersMapper;
import com.springboot.sample.service.TestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
@Service
public class TestServiceImpl implements TestService {

    @Resource
    private UsersMapper usersMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void testInsert() {
//                int a = 1 / 0;
        Users entity = new Users();
        entity.setName("张三啊");
        usersMapper.insert(entity);
        System.out.println(entity.getId());
        if (true){
            throw new RuntimeException("测试事务回滚");
        }
    }
}
