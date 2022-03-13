package com.springboot.sample.controller;

import com.springboot.sample.annotation.ConditionalValidate;
import com.springboot.sample.bean.Person;
import com.springboot.sample.bean.TestValidate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validate")
public class ValidateController {

/*
    @RequestMapping("/test")
    public String test(@Validated Person person) {
        return "success";
    }*/


    @RequestMapping("/test")
    @ConditionalValidate
    public String test(@Validated TestValidate testValidate) {
        return "success";
    }

}
