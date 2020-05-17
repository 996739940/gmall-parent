package com.atguigu.gmall.oms;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@MapperScan(basePackages = "com.atguigu.gmall.oms.mapper")
@SpringBootApplication
public class GmallOmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallOmsApplication.class, args);
    }

}
