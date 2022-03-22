package com.springboot.sample.aspectj.action.impl;

import com.springboot.sample.annotation.ConditionalValidateField;
import com.springboot.sample.aspectj.ConditionalValidateAspect;
import com.springboot.sample.aspectj.action.ValidateHandle;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;

/*
* 如果相等，判断是否为空
*spring.factories 自动装配的 beanName是 com.springboot.sample.aspectj.action.impl.IfEqNotNullHandleImpl 即使@Service被扫描也不会beanName冲突
* */
@Service
public class IfEqNotNullHandleImpl implements ValidateHandle {


    @Override
    public void doValidate(ConditionalValidateField conditionalValidateField, Map<String, Class> fieldClzMap, ExpressionParser parser, ConditionalValidateAspect.ConditionalValidateFieldInfo conditionalValidateFieldInfo, EvaluationContext context, String[] paramsName) {
        // 判断该字段类型
        Class originalClz = fieldClzMap.get(conditionalValidateFieldInfo.getFieldName());
        //TODO 只写了Integer类型的
        if (Integer.class.getSimpleName().equals(originalClz.getSimpleName())) {
            Expression expression = parser.parseExpression("#" + paramsName[0] + "." + conditionalValidateFieldInfo.getFieldName());
            Integer originalValue = expression.getValue(context, Integer.class);
            if (!StringUtils.isEmpty(conditionalValidateField.ifValue())) {
                // 如果是相等的
                if (Integer.valueOf(conditionalValidateField.ifValue()).equals(originalValue)) {
                    Expression relationExpression = parser.parseExpression("#" + paramsName[0] + "." + conditionalValidateField.relationField());
                    String relationField = conditionalValidateField.relationField();
                    Object value = relationExpression.getValue(context, fieldClzMap.get(relationField));
                    Assert.isTrue(!StringUtils.isEmpty(value), conditionalValidateField.message());
                }
            } else {
                // 为空的情况,有可能要求原字段为空，关联字段不能为空的情况；判断都是空就校验
                if (StringUtils.isEmpty(conditionalValidateField.ifValue()) && StringUtils.isEmpty(originalValue)) {
                    Expression relationExpression = parser.parseExpression("#" + paramsName[0] + "." + conditionalValidateField.relationField());
                    String relationField = conditionalValidateField.relationField();
                    Object value = relationExpression.getValue(context, fieldClzMap.get(relationField));
                    Assert.isTrue(!StringUtils.isEmpty(value), conditionalValidateField.message());
                }
            }
        }
    }
}
