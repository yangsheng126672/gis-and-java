package com.jdrx.gis.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


/**
 * 数据源配置
 */
@Configuration
public class DataSourceConfig {

    @Autowired
    private PGConfigProperties pgConfigProperties;

    @Bean(name = "dataSource")
    @Qualifier("dataSource")
    @Primary
    public DataSource dataSource() {
        pgConfigProperties.setPoolName("postgresql-pool[" + pgConfigProperties.getMaximumPoolSize() + "]");
        return new HikariDataSource(pgConfigProperties);
    }

    @Bean(name = "transactionManager")
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "jdbcTemplate")
    @Primary
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}