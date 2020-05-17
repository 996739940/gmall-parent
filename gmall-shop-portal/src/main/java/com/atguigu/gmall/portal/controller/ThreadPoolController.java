package com.atguigu.gmall.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName ThreadPoolController
 * @Description 监控线程池状态
 * @Author 张燕廷
 * @Date 2020/4/28 14:35
 * @Version 1.0
 **/
@RestController
public class ThreadPoolController {

    @Qualifier("mainThreadPoolExecutor")
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;


    @GetMapping("/thread/status")
    public Map threadPoolStatue(){
        Map<String,Object> map = new HashMap<>();
        map.put("ActiveCount",threadPoolExecutor.getActiveCount());
        map.put("CorePoolSize",threadPoolExecutor.getCorePoolSize());


        return  map;
    }
}
