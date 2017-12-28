package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

/**
 * Created by Administrator on 2017/12/28.
 */
public interface IShippingService {
    public ServerResponse add(Integer userId, Shipping shipping);

    public ServerResponse<String> del(Integer userId, Integer shippingId);

    public ServerResponse<java.lang.String> update(Integer userId, Shipping shipping);

    public ServerResponse select(Integer userId,Integer shippingId);

    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
