package com.atguigu.gmall.cart.component;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.vo.UserCartKey;
import com.atguigu.gmall.constant.CartConstant;
import com.atguigu.gmall.constant.SysCacheConstant;
import com.atguigu.gmall.ums.entity.Member;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @ClassName MemberComponent
 * @Description 用户购物车辅助组件
 * @Author 张燕廷
 * @Date 2020/5/10 16:44
 * @Version 1.0
 **/
@Component
public class MemberComponent {

    @Autowired
    StringRedisTemplate redisTemplate;

    public UserCartKey getCartKey(String accessToken, String cartKey){
        UserCartKey userCartKey = new UserCartKey();
        Member member = null;
        if(!StringUtils.isEmpty(accessToken)){
            member = getMemberByAccessToken(accessToken);
        }

        if(member!=null){
            //获取到了在线用户；用户登录用这个
            userCartKey.setLogin(true);
            userCartKey.setUserId(member.getId());
            userCartKey.setFinalCartKey(CartConstant.USER_CART_KEY_PREFIX+member.getId());
            return userCartKey;
        }else if(!StringUtils.isEmpty(cartKey)){
            //用户有临时的用这个
            userCartKey.setLogin(false);
            userCartKey.setFinalCartKey(CartConstant.TEMP_CART_KEY_PREFIX+cartKey);
            return userCartKey;
        }else {
            //用户既没有登录也没有零时购物车
            String replace = UUID.randomUUID().toString().replace("-", "");
            userCartKey.setLogin(false);
            userCartKey.setFinalCartKey(CartConstant.TEMP_CART_KEY_PREFIX+replace);
            userCartKey.setTempCartKey(replace);

            return userCartKey;
        }

    }
    /**
     * 根据accessToken查询用户信息
     */
    public Member getMemberByAccessToken(String accessToken){
        String userJson = redisTemplate.opsForValue().get(SysCacheConstant.LOGIN_MEMBER + accessToken);
        return JSON.parseObject(userJson, Member.class);
    }
}
