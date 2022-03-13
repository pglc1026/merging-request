package com.springboot.sample.bean;

import com.springboot.sample.annotation.ConditionalValidateField;
import com.springboot.sample.constant.ValidateFieldAction;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class TestValidate {


    /**
     * 姓名
     **/
    @NotBlank(message = "姓名必填")
    private String name;

    /*** 是否有房 0 - 没房子 1 - 有房子 */
    @NotNull(message = "是否有房必填")
    @ConditionalValidateField(relationField = "hoursAreas", ifValue = "1",
            action = ValidateFieldAction.IF_EQ_NOT_NULL,
            message = "有房子，房子面积必填")
    private Integer isHaveHours;

    /***
     * 房子面积
     * */
    private Integer hoursAreas;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsHaveHours() {
        return isHaveHours;
    }

    public void setIsHaveHours(Integer isHaveHours) {
        this.isHaveHours = isHaveHours;
    }

    public Integer getHoursAreas() {
        return hoursAreas;
    }

    public void setHoursAreas(Integer hoursAreas) {
        this.hoursAreas = hoursAreas;
    }
}
