package com.github.jpidem.spring4;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 多数据源驱动枚举
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
@AllArgsConstructor
public enum DatabaseDriverEnum {
    SQLSERVER("Microsoft SQL Server", "sqlserver"),
    POSTGRESQL("PostgreSQL", "postgresql"),
    MYSQL("MySQL", "mysql");

    @Getter
    private final String productName;
    @Getter
    private final String driverClassName;


    public static DatabaseDriverEnum fromProductName(String productName) {
        return Stream.of(DatabaseDriverEnum.values())
                .filter(dbd -> dbd.getProductName().equalsIgnoreCase(productName))
                .findAny()
                .orElse(null);
    }
}