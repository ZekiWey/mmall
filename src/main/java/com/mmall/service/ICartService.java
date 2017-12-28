package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVO;

/**
 * Created by Administrator on 2017/12/27.
 */
public interface ICartService {
    public ServerResponse<CartVO> add(Integer productId, Integer userId, Integer count);

    public ServerResponse<CartVO> update(Integer productId,Integer userId,Integer count);

    public ServerResponse<CartVO> delete(String productIds,Integer userId);

    public ServerResponse<CartVO> list(Integer userId);

    public ServerResponse<CartVO> selectOrUnSelect(Integer userId,Integer productId,Integer checked);

    public ServerResponse<Integer> getCartProductCount(Integer userId);
}
