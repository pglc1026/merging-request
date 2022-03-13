package com.springboot.sample.aspectj;

import com.springboot.sample.annotation.ConditionalValidate;
import com.springboot.sample.annotation.ConditionalValidateField;
import com.springboot.sample.constant.ValidateFieldAction;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Aspect
@Component
public class ConditionalValidateAspect {

    //将方法参数纳入Spring管理
    private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    //解析spel表达式
    private final ExpressionParser parser = new SpelExpressionParser();

    @Before("@annotation(conditionalValidate)")
    public void doBefore(JoinPoint joinPoint, ConditionalValidate conditionalValidate) throws Throwable {
        //获取参数对象数组
        Object[] args = joinPoint.getArgs();
        Assert.notEmpty(args, "没有参数");
        Assert.isTrue(args.length <= 1, "只能有一个参数");

        //获取方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();


        //获取方法参数名
        String[] params = discoverer.getParameterNames(method);
        //将参数纳入Spring管理
        EvaluationContext context = new StandardEvaluationContext();
        for (int len = 0; len < params.length; len++) {
            context.setVariable(params[len], args[len]);
        }
        Object firstParams = args[0];
        if (!StringUtils.isEmpty(firstParams)) {
            List<Field> allFields = getAllFields(firstParams);

            // 把要校验的找到
            List<ConditionalValidateField> validateFieldList = new ArrayList<>();
            // 字段类型
            Map<String, Class> fieldClzMap = new HashMap<>();
            allFields.forEach(field -> {
                ConditionalValidateField conditionalValidateField = AnnotationUtils.findAnnotation(field, ConditionalValidateField.class);
                if (!StringUtils.isEmpty(conditionalValidateField)) {
                    validateFieldList.add(conditionalValidateField);
                }
                fieldClzMap.put(field.getName(), field.getType());
            });


            // 执行校验动作
            validateFieldList.forEach(validateField -> {
                if (!StringUtils.isEmpty(validateField)) {
                    if (ValidateFieldAction.NOT_NULL == validateField.action()) {
                        Expression expression = parser.parseExpression("#" + params[0] + "." + validateField.relationField());
                        String field = validateField.relationField();
                        Object value = expression.getValue(context, fieldClzMap.get(field));
                        Assert.isTrue(!StringUtils.isEmpty(value), validateField.message());
                    }
                }
            });

        }


    }

    public static List<Field> getAllFields(Object object) {
        Class clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

}
