package com.atguigu.gmall.ums.service;

import com.atguigu.gmall.ums.entity.Member;
import com.atguigu.gmall.ums.entity.MemberReceiveAddress;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Lfy
 * @since 2020-04-10
 */
public interface MemberService extends IService<Member> {

    Member login(String username, String password);

    List<MemberReceiveAddress> getMemberAddress(Long id);

    MemberReceiveAddress getMemberAddressByAddressId(Long addressId);
}
