## 系统参数说明

|参数名称|参数类型|取值范围|默认值|参数说明|备注|
|:-----|:----|:----:|:----:|:----|:----|
| retry.enabled |boolean| true、false | true | 是否开启任务重试 |  |
| retry.web.enabled |boolean| true、false | true | 是否开启Job管理功能 |  |
| retry.beforeTask | boolean | true、false | true | 是否在执行任务之前插入数据库 |配置false则表示，只有任务执行报错才插入数据库|
| retry.sqlMapping.filepath | string |  |  | 配置自定义SQL文件 |当前系统只支持sqlserver、mysql、PostgreSQL，如果不是使用这些数据库，则需要自己扩展|

### 其他说明
JpIdem Job是一个持久化的任务重试框架，持久化内部使用的是spring-jdbc，所以目前只支持关系型数据库sqlserver、mysql、PostgreSQL。
如果要使用其他的关系型数据库，则需要自己写一个SQL映射文件（如 resources/config/h2.properties），参考 jpIdem-spring4/src/resources/META-INF/sqlprops/mysql.properties，根据数据库和表结构的实际情况修改如下4个配置：
    
    INSERT_SQL=
    UPDATE_SQL=
    QUERY_NEEDRETRYTASK_LIST_SQL=
    PRIMARY_KEY=
    
最后在配置文件中配置retry.sqlMapping.filepath=config/h2.properties

如果要使用其他非关系型数据库，如MongoDB，则可以直接实现一个com.github.jpidem.core.RetryTaskMapper接口，并托管到Spring容器中