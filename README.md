## JpIdem Job

JpIdem Job也叫《冥等数据任务管理平台》
主要是用来进行方法重试的。和Guava Retry、Spring Retry相比，JpIdem Job最大的特点是异步重试，数据操作冥等，支持持久化，系统重启之后可以继续重试。

## 背景

在微服务环境下，会根据不同的业务将拆分成不同的服务，比如会员服务、订单服务、商品服务等等，每个服务都会和其他服务进行交互，根据交互的结果来更新自己服务的数据。在交互的过程中，往往会因为网络原因、对方服务的中断或更新重启导致交互失败，在这种情况下，要保证各个服务数据的一致性就必须要进行重试。市面上开源的重试框架有很多，如Guava
Retry、Spring Retry等都支持重试，但是他们都不支持任务的持久化，系统更新重启，重试任务就会丢失，这无法保证数据的一致性。传统的做法是写一个定时任务，定时补偿。但这样会增加工作量，增加很多冗余的代码。JpIdem
Job在这种背景下应运而生，支持持久化的任务重试，保证数据的一致性

## 架构图

![架构图](https://s1.ax1x.com/2020/03/15/88RvNj.png)

## 实现原理

- 系统启动后，把所有com.github.jpidem.core.RetryHandler和带有@RetryFunction注解的方法注册为定时任务。
-
所有com.github.jpidem.core.RetryHandler和带有@RetryFunction注解的方法都会被Spring进行代理，执行的时候，会先把参数序列化，然后把执行任务插入到数据库。最后根据任务执行的成功与否，更新任务的相应状态。
- 定时任务定时从表里面获取未成功的任务，进行重试

## 项目特点

* 方法重试持久化，系统重启之后可以继续重试
* 异步重试（不支持同步重试）
* 支持接口实现和声明式方式
* 大量的扩展点
* 支持对接其他调度框架（如：Elastic-Job）
* 提供重试Job可视化管理

## 开发中特性

* 并发一致性：基于Redis分布式锁保证集群分布式调度的最终一致性, 一次任务调度只会触发一次执行；
* 故障转移：任务路由策略选择"故障转移"情况下，如果集群中某一台机器故障，将会自动切换到一台正常的执行器发送调度请求；
* 路由策略：系统集群部署时提供丰富的路由策略，包括：轮询、随机、故障转移、忙碌转移等常用策略；
* 阻塞策略：同一个调度过于密集执行器来不及处理时的处理策略，策略包括：单机串行（默认）、丢弃后续调度、覆盖之前调度；
* 超时控制：支持自定义任务超时时间，任务运行超时将会主动中断任务；默认超时时间15分钟
* 降级隔离：调度线程池进行隔离拆分，慢任务自动降级进入"Slow"线程池，避免耗尽调度线程阻塞线程，提高系统稳定性；
* 任务线程监控：可视化页面级别的任务运行线程信息；线程池，线程级别，可粗可细；

## 适用场景

* 方法重试需要持久化，系统重启、宕机恢复之后继续重试，直到重试成功
* 分布式事务最终一致性

## 不适用场景

* 延时低、实时性很高的重试
* 允许任务丢失（直接用Guava Retry就行了）
* 需要同步重试的

## Requirements:

* Java 8 or Above
* Apache Maven 3.x

## Maven dependency

* Spring 4.x
* Quartz 2.3.2
* aspectj 1.9.5
* lombok 1.18.12
* metainf-services 1.8
* apache commons-lang3 3.9

## Maven dependency provided

* MySQL
* PostgreSQL
* MSSQL Server
* Jackson 2.x
* Gson 2.8.6
* Fastjson 1.2.66
* Elastic-Job 2.1.5

## 模块说明

* [jpIdem-cpre](https://github.com/jiangeeq/jpIdem-job/tree/master/jpIdem-core) 重试模块的核心，定义了一系列的接口和扩展点
* [jpIdem-spring4](https://github.com/jiangeeq/jpIdem-job/tree/master/jpIdem-spring4) 基于spring4实现的重试模块
* [jpIdem-serializer-jackson2](https://github.com/jiangeeq/jpIdem-job/tree/master/jpIdem-serializer/jpIdem-serializer-jackson2) 使用jackson2来实现参数的序列化和反序列化
* [jpIdem-serializer-gson](https://github.com/jiangeeq/jpIdem-job/tree/master/jpIdem-serializer/jpIdem-serializer-gson) 使用gson来实现参数的序列化和反序列化
* [jpIdem-serializer-fastjson](https://github.com/jiangeeq/jpIdem-job/tree/master/jpIdem-serializer/jpIdem-serializer-fastjson) 使用fastjson来实现参数的序列化和反序列化
* [jpIdem-samples](https://github.com/jiangeeq/jpIdem-job/tree/master/jpIdem-samples) 配套的示例demo，可直接使用

## 如何使用

* 本工具只适合在Spring项目中使用，Spring依赖至少包含（spring-context-support、spring-aop、spring-jdbc）
* 在项目中引入maven依赖。最新版本已经deploy到maven的中央仓库了[查看最新版](https://search.maven.org/search?q=g:io.github.jiangeeq)

        <dependency>
            <groupId>io.github.jiangeeq</groupId>
            <artifactId>jpIdem-spring4</artifactId>
            <version>使用最新版本</version>
        </dependency>

* 创建数据库（或者直接使用已有的数据库），支持Microsoft SQL Server、PostgreSQL、MySQL数据库，其他数据库请自行扩展
*
初始化系统表，根据具体的数据库，执行jpIdem-spring4/resources/sql/sqlserver.sql、jpIdem-spring4/resources/sql/postgresql.sql、jpIdem-spring4/resources/sql/mysql.sql对应的建表SQL
* 配置数据源
* 编写业务逻辑

        @RetryFunction(identity = "order.payment")
        public void payOrderAndUpdateStatus(Order order) {
            boolean success = paymentBusiness.doPayment(order);
            if (success) {
                orderBusiness.updateOrderPayStatus(order);
            } else {
                orderBusiness.updateOrderPayFail(order);
            }
        }
  或者

        @Slf4j
        @Service("orderPaymentBusiness")
        public class OrderPaymentBusiness implements RetryHandler<Order, Void> {
        
            @Autowired
            private PaymentBusiness paymentBusiness;
        
            @Autowired
            private OrderBusiness orderBusiness;
        
            @Override
            public String identity() {
                return "order.payment";
            }
        
            @Override
            public Void handle(Order order) {
                boolean success = paymentBusiness.doPayment(order);
                if (success) {
                    orderBusiness.updateOrderPayStatus(order);
                } else {
                    orderBusiness.updateOrderPayFail(order);
                }
                return null;
            }
        }

* 最后在启动入口加上 @EnableRetrying 注解

## Job管理页面

系统内置了一个简易的Job管理页面（页面地址: /jpIdem/job.html），通过这个管理页面可以查看当前系统所有已注册的重试任务，对重试任务进行执行、停止、启动等操作
![Job管理页面](https://github.com/jiangeeq/jpIdem-job/tree/master/doc/img/dashboard.jpg)

## 其他文档

* [重试方法参数说明](/doc/TASK_PARAM.md)
* [系统参数说明](/doc/SYS_ARGS.md)
* [监听器](/doc/LISTENER.md)
* [整合Elastic-Job](/doc/Elastic_Job.md)
* [常见问题](/doc/QUESTION.md)

## 打包

mvn clean package