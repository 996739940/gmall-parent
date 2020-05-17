package com.atguigu.gmall.oms.component;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.constant.SysCacheConstant;
import com.atguigu.gmall.ums.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @ClassName MemberComponent
 * @Description redis工具类
 * @Author 张燕廷
 * @Date 2020/5/15 14:33
 * @Version 1.0
 **/
@Component
public class MemberComponent {

    @Autowired
    StringRedisTemplate redisTemplate;

    public Member getMemberByAccessToken(String accessToken){
        String json = redisTemplate.opsForValue().get(SysCacheConstant.LOGIN_MEMBER + accessToken);
        if(!StringUtils.isEmpty(json)){
            return  JSON.parseObject(json,Member.class);
        }
        return null;
    }
}

