package com.springboot.sample.aspectj;

import com.springboot.sample.annotation.ConditionalValidate;
import com.springboot.sample.annotation.ConditionalValidateField;
import com.springboot.sample.constant.ValidateFieldAction;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Aspect
//@Component
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
            List<ConditionalValidateFieldInfo> validateFieldList = new ArrayList<>();
            // 字段类型
            Map<String, Class> fieldClzMap = new HashMap<>();
            allFields.forEach(field -> {
                ConditionalValidateField conditionalValidateField = AnnotationUtils.findAnnotation(field, ConditionalValidateField.class);
                String fieldName = field.getName();
                if (!StringUtils.isEmpty(conditionalValidateField)) {
                    validateFieldList.add(new ConditionalValidateFieldInfo(fieldName, conditionalValidateField));
                }
                fieldClzMap.put(fieldName, field.getType());
            });


            // 执行校验动作，这块要分很多种情况处理
            validateFieldList.forEach(conditionalValidateFieldInfo -> {
                if (!StringUtils.isEmpty(conditionalValidateFieldInfo)) {
                    ConditionalValidateField conditionalValidateField = conditionalValidateFieldInfo.getConditionalValidateField();
                    //TODO 这个地方可以使用策略模式优化下，共性的地方用模板方法
                    // 如果是相等 执行校验
                    if (ValidateFieldAction.IF_EQ_NOT_NULL == conditionalValidateField.action()) {
                        // 判断该字段类型
                        Class originalClz = fieldClzMap.get(conditionalValidateFieldInfo.getFieldName());
                        //TODO 只写了Integer类型的
                        if (Integer.class.getSimpleName().equals(originalClz.getSimpleName())) {
                            Expression expression = parser.parseExpression("#" + params[0] + "." + conditionalValidateFieldInfo.getFieldName());
                            Integer originalValue = expression.getValue(context, Integer.class);
                            if (!StringUtils.isEmpty(conditionalValidateField.value())) {
                                // 如果是相等的
                                if (Integer.valueOf(conditionalValidateField.value()).equals(originalValue)) {
                                    Expression relationExpression = parser.parseExpression("#" + params[0] + "." + conditionalValidateField.relationField());
                                    String relationField = conditionalValidateField.relationField();
                                    Object value = relationExpression.getValue(context, fieldClzMap.get(relationField));
                                    Assert.isTrue(!StringUtils.isEmpty(value), conditionalValidateField.message());
                                }
                            } else {
                                // 为空的情况,有可能要求原字段为空，关联字段不能为空的情况；判断都是空就校验
                                if (StringUtils.isEmpty(conditionalValidateField.value()) && StringUtils.isEmpty(originalValue)) {
                                    Expression relationExpression = parser.parseExpression("#" + params[0] + "." + conditionalValidateField.relationField());
                                    String relationField = conditionalValidateField.relationField();
                                    Object value = relationExpression.getValue(context, fieldClzMap.get(relationField));
                                    Assert.isTrue(!StringUtils.isEmpty(value), conditionalValidateField.message());
                                }
                            }
                        }
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

    /***
     * 封装字段信息
     * */
    public class ConditionalValidateFieldInfo {
        private String fieldName;
        private ConditionalValidateField conditionalValidateField;

        public ConditionalValidateFieldInfo(String fieldName, ConditionalValidateField conditionalValidateField) {
            this.fieldName = fieldName;
            this.conditionalValidateField = conditionalValidateField;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public ConditionalValidateField getConditionalValidateField() {
            return conditionalValidateField;
        }

        public void setConditionalValidateField(ConditionalValidateField conditionalValidateField) {
            this.conditionalValidateField = conditionalValidateField;
        }
    }

}
