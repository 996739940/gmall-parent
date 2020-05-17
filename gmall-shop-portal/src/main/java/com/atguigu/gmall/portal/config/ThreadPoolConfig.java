package com.atguigu.gmall.portal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ThreadPoolConfig
 * @Description 配置当前系统的线程池信息
 * @Author 张燕廷
 * @Date 2020/4/28 14:40
 * @Version 1.0
 **/
@Configuration
public class ThreadPoolConfig {

    @Value("${gmall.pool.coreSize}")
    private Integer coreSize;
    @Value("${gmall.pool.maximumPoolSize}")
    private Integer maximumPoolSize;
    @Value("${gmall.pool.queueSize}")
    private Integer queueSize;

    @Value("${gmall.pool.OthercoreSize}")
    private Integer OthercoreSize;
    @Value("${gmall.pool.OthermaximumPoolSize}")
    private Integer OthermaximumPoolSize;
    @Value("${gmall.pool.OtherqueueSize}")
    private Integer OtherqueueSize;


    @Bean("mainThreadPoolExecutor")
    public ThreadPoolExecutor mainThreadPoolExecutor(){
        /**
         *   public ThreadPoolExecutor(int corePoolSize,
         *                               int maximumPoolSize,
         *                               long keepAliveTime,
         *                               TimeUnit unit,
         *                               BlockingQueue<Runnable> workQueue,
         *                               RejectedExecutionHandler handler) {
         */
        LinkedBlockingDeque<Runnable> deque = new LinkedBlockingDeque<>(queueSize);

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(coreSize,
                maximumPoolSize, 10,
                TimeUnit.MINUTES, deque);

        return threadPoolExecutor;
    }

    @Bean("otherThreadPoolExecutor")
    public ThreadPoolExecutor otherThreadPoolExecutor(){
        LinkedBlockingDeque<Runnable> deque = new LinkedBlockingDeque<>(OtherqueueSize);

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(OthercoreSize,
                OthermaximumPoolSize, 10,
                TimeUnit.MINUTES, deque);

        return threadPoolExecutor;
    }


}

