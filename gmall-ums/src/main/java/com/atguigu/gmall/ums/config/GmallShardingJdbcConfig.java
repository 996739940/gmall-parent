package com.atguigu.gmall.ums.config;

import io.shardingjdbc.core.api.MasterSlaveDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @ClassName GmallShardingJdbcConfig
 * @Description 配置多数据源和读写分离
 * @Author 张燕廷
 * @Date 2020/4/11 14:54
 * @Version 1.0
 **/
@Configuration
public class GmallShardingJdbcConfig {
    @Bean
    public DataSource dataSource() throws IOException, SQLException {
        //使用sharding-jdbc创建出具有主从库的数据源
        DataSource dataSource = MasterSlaveDataSourceFactory
                .createDataSource(ResourceUtils.getFile("classpath:sharding-jdbc.yml"));
        return  dataSource;
    }
}

