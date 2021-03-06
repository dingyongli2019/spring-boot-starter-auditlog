# spring-boot-starter-auditlog

<p align="center">
    <img src="https://img.shields.io/badge/JDK-1.8+-green.svg" />
    <img src="https://img.shields.io/maven-central/v/wiki.xsx/spring-boot-starter-slf4j.svg?label=Maven%20Central" />
    <img src="https://img.shields.io/:license-apache-blue.svg" />
</p>

#### 介绍
一个注解搞定日志的组件，减少到处编写日志的烦恼，还可定位代码哟

#### 软件架构
依赖spring-boot-starter-aop

#### 原理说明
AOP + Reflect

#### 作用范围
任意由**spring**调用的方法

#### 安装教程

```
<dependency>
    <groupId>com.tongxiaoya</groupId>
    <artifactId>spring-boot-starter-auditlog</artifactId>
    <version>1.0</version>
</dependency>
```

#### 使用说明

##### 一、准备工作
1. 添加依赖：

```
<dependency>
    <groupId>com.tongxiaoya</groupId>
    <artifactId>spring-boot-starter-auditlog</artifactId>
    <version>1.0</version>
</dependency>
```
2. 开启日志：

yml方式：

```
logging:
  level:
    auditlog: 对应级别
```
properties方式：

```
logging.level.auditlog: 对应级别
```

##### 二、开始使用

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
2019-06-20 00:53:32.390 DEBUG 7932 --- [           main] LogProcessor           : 调用方法：【com.example.demo.service.TestService.test1(TestService.java:19)】，业务名称：【test1】，接收参数：【{}】
2019-06-20 00:53:32.405 DEBUG 7932 --- [           main] LogProcessor           : 调用方法：【com.example.demo.service.TestService.test2(TestService.java:22)】，业务名称：【test2】，返回结果：【null】
2019-06-20 00:53:32.412 DEBUG 7932 --- [           main] LogProcessor           : 调用方法：【com.example.demo.service.TestService.test4(TestService.java:28)】，业务名称：【test4】，接收参数：【{name=xsx2, id=[1, 2, 3, 4]}】
2019-06-20 00:53:32.413 DEBUG 7932 --- [           main] LogProcessor           : 调用方法：【com.example.demo.service.TestService.test4(TestService.java:28)】，业务名称：【test4】，返回结果：【null】
```

#### 其他说明

##### 日志类型
1. @ParamLog：参数类型，仅打印参数
2. @ResultLog：结果类型，仅打印结果
3. @ThrowingLog：异常类型，仅打印异常
4. @Log：综合类型，打印参数+结果+异常

##### 日志参数
1. value：业务名称
2. level：日志级别，默认DEBUG
3. position：代码定位，默认DEFAULT

##### 日志级别
1. DEBUG(默认): 调试级别
2. INFO: 信息级别
3. WARN: 警告级别
4. ERROR: 错误级别

##### 特别说明
1. 日志级别为DEBUG时，默认开启代码定位，方便调试
2. 其他级别默认关闭代码定位，减少不必要的开支，如需要可手动开启(position=Position.ENABLED)

#### 性能测试(仅供参考)
##### 电脑配置

|  |  |
| :------: | :------: |
| CPU | AMD Athlon(tm) II X4 640 Processor(3000 Mhz) |
| 内存 | 8.00 GB (1333 MHz) |
| 硬盘 | Apacer A S510S 128GB SATA Disk Device |
| 测试工具 | Apache JMeter 5.1.1 |
| 测试方式 | http请求测试日志打印，循环5次取最后1次 |

##### 测试结果
加入代码定位功能：

| 日志类型 | 并发数 | 单次平均耗时(毫秒) | 吞吐量(请求次数/每秒)
| :------: | :------: | :------: | :------: |
| @ParamLog | 1000 | 136 | 484 |
| @ResultLog | 1000 | 86 | 417 |
| @Log | 1000 | 29 | 425 |

取消代码定位功能：

| 日志类型 | 并发数 | 单次平均耗时(毫秒) | 吞吐量(请求次数/每秒)
| :------: | :------: | :------: | :------: |
| @ParamLog | 1000 | 274 | 491 |
| @ResultLog | 1000 | 66 | 519 |
| @Log | 1000 | 108 | 483 |
