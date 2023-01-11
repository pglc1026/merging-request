package com.springboot.sample.service.impl;

import com.springboot.sample.bean.Users;
import com.springboot.sample.mapper.UsersDao;
import com.springboot.sample.service.TestExceptionService;
import com.springboot.sample.service.TestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class TestServiceImpl implements TestService {

    @Resource
    private UsersDao usersDao;

    @Resource
    private TestExceptionService testExceptionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void testInsert() {
        Users entity = new Users();
        entity.setName("张三啊");
        usersDao.save(entity);
        System.out.println(entity.getId());
        try {
            testExceptionService.test();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String testDeferredResult() {
        System.out.println("内部线程 名称 "+Thread.currentThread().getName());
        return "testDeferredResult";
    }
}
