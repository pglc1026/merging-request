package com.springboot.sample.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConditionalValidateField {

    /***
     * 关联字段
     * */
    String relationField();

    /***
     * 要执行的的校验动作
     * */
    int action();

    /***
     * 异常信息
     * */
    String message() default "";


}
