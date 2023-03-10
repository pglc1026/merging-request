# v1.0.5
- Servlet 3.0+ 异步
- 合并请求
- 参考 https://www.cnblogs.com/oyjg/p/13099998.html
- https://docs.spring.io/spring-framework/docs/5.2.19.RELEASE/spring-framework-reference/web.html#mvc-ann-async
- https://blog.csdn.net/lxhjh/article/details/70237473
- ForkJoin测试
## 批量插入数据，模拟千万数据
```bash
DELIMITER $$
USE `dwc-admin`$$
DROP PROCEDURE IF EXISTS `proc_user`$$

CREATE DEFINER=`root`@`%` PROCEDURE `proc_user`()
BEGIN
    DECLARE i INT DEFAULT 1;
   WHILE i <= 10000000 DO
    

     INSERT INTO  `t_users`(`name`) VALUES ( '张三');

     SET i=i+1;
     END WHILE;
END$$


CALL `proc_user`();
```

# v1.0.4 
- 支持同一个属性多次使用相同注解
- 测试事务
# v1.0.2 
- 条件注解，关联校验
- Redis zset消息队列、延迟队列
# spring-boot-kubernetes
spring-boot-kubernetes例子

## 构建Docker镜像
- docker build -t spring-boot-kubernetes:1.0.0 .

### 导出tar包
- docker save spring-boot-kubernetes:1.0.0 > spring-boot-kubernetes-1.0.0.tar

### 导入tar包 
- eval $(minikube docker-env)  切换到minikube daemon
- docker load < spring-boot-kubernetes-1.0.0.tar   导入

### kubernetes
- kubectl apply -f  deploy.yaml
- kubectl expose deployment spring-boot-kubernetes --type=NodePort
- minikube service spring-boot-kubernetes --url

## 遇到的异常
- 1、no matches for kind "Deployment" in version "apps/v1beta1"
- 这是因为API版本已正式发布，不再是beta了，将apps/v1beta1修改为apps/v1。
- 2、Container image "spring-boot-kubernetes" is not present with pull policy of Never Error: ErrImageNe
- 因为我的镜像版本是1.0.0，不写应该会默认使用latest版本，找不到，一直报这个错，强制声明spring-boot-kubernetes:1.0.0<image:tag>即可。

### 参考
- https://www.jianshu.com/p/592da53cdff0
- https://www.jianshu.com/p/a9aec78418df
