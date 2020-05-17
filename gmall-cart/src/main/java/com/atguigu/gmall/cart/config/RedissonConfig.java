package com.atguigu.gmall.cart.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @ClassName RedissonConfig
 * @Description 分布式rediss的配置类
 * @Author 张燕廷
 * @Date 2020/5/10 16:42
 * @Version 1.0
 **/
@Configuration
public class RedissonConfig {

    @Bean
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.1.200:6379");
        return Redisson.create(config);
    }

}
