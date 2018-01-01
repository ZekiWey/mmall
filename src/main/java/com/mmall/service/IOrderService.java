package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

/**
 * Created by Administrator on 2017/12/29.
 */
public interface IOrderService {

    public ServerResponse pay(Integer userId, Long orderNo, String path);

    public ServerResponse alipayCallback(Map<String,String> params);

    public ServerResponse<Boolean> queryOrderPayStatus(Integer userId,Long orderNo);

}
