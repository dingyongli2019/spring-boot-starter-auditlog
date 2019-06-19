# spring-boot-starter-slf4j

#### 介绍
一个注解搞定日志，减少到处编写日志的烦恼

#### 软件架构
依赖spring-boot-starter-aop

#### 原理说明
AOP + Reflect

#### 作用范围
1. 由**spring管理**的类
2. 由**外部调用**的**public类型**方法

#### 安装教程

```
<dependency>
    <groupId>wiki.xsx</groupId>
    <artifactId>spring-boot-starter-slf4j</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### 使用说明

##### 准备工作
1. 添加依赖：

```
<dependency>
    <groupId>wiki.xsx</groupId>
    <artifactId>spring-boot-starter-slf4j</artifactId>
    <version>1.0.0</version>
</dependency>
```
2. 开启日志：

yml方式：

```
logging:
  level:
    wiki.xsx.log: 对应级别
```
properties方式：

```
logging.level.wiki.xsx.log: 对应级别
```

##### 开始使用

例如：
```
@Service
public class TestService {

    @ParamLog("test1")
    public void test1() {}

    @ResultLog("test2")
    public void test2(String name) {}

    @ThrowingLog("test3")
    public void test3(String name, int id) {}

    @Log("test4")
    public void test4(String name, int ...id) {}
}
```

方法调用：
```
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
        TestService bean = context.getBean(TestService.class);
        bean.test1();
        bean.test2("xsx");
        bean.test3("xsx`", 1);
        bean.test4("xsx2", 1, 2, 3, 4);
    }
}
```

打印效果：
```
2019-06-20 00:53:32.390 DEBUG 7932 --- [           main] wiki.xsx.core.log.LogProcessor           : 调用方法：【com.example.demo.service.TestService.test1(TestService.java:19)】，业务名称：【test1】，接收参数：【{}】
2019-06-20 00:53:32.405 DEBUG 7932 --- [           main] wiki.xsx.core.log.LogProcessor           : 调用方法：【com.example.demo.service.TestService.test2(TestService.java:22)】，业务名称：【test2】，返回结果：【null】
2019-06-20 00:53:32.412 DEBUG 7932 --- [           main] wiki.xsx.core.log.LogProcessor           : 调用方法：【com.example.demo.service.TestService.test4(TestService.java:28)】，业务名称：【test4】，接收参数：【{name=xsx2, id=[1, 2, 3, 4]}】
2019-06-20 00:53:32.413 DEBUG 7932 --- [           main] wiki.xsx.core.log.LogProcessor           : 调用方法：【com.example.demo.service.TestService.test4(TestService.java:28)】，业务名称：【test4】，返回结果：【null】
```

#### 其他说明

##### 日志级别
1. DEBUG(默认): 调试级别
2. INFO: 信息级别
3. WARN: 警告级别
4. ERROR: 错误级别

##### 日志类型
1. @ParamLog：参数类型，仅打印参数
2. @ResultLog：结果类型，仅打印结果
3. @ThrowingLog：异常类型，仅打印异常
4. @Log：综合类型，打印参数+结果+异常