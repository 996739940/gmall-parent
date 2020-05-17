package com.atguigu.gmall.oms.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import io.shardingjdbc.core.api.MasterSlaveDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @ClassName OmsDataSourceConfig
 * @Description 这是一个配置类     SpringBoot引入某个场景，这个场景的组件就会自动配置好。
 * @Author 张燕廷
 * @Date 2020/5/15 14:32
 * @Version 1.0
 **/
@Configuration
public class OmsDataSourceConfig {

    @Bean
    public DataSource dataSource() throws IOException, SQLException {

        File file = ResourceUtils.getFile("classpath:sharding-jdbc.yml");
        DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(file);
        return dataSource;
    }

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
