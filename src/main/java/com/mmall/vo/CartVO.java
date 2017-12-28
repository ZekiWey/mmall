package com.mmall.vo;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/12/27.
 */
public class CartVO {
    private List<CartProductVO> productVOList = Lists.newArrayList();
    private BigDecimal cartTotalPrice;
    private boolean allCheck;
    private String imageHost;

    public List<CartProductVO> getProductVOList() {
        return productVOList;
    }

    public void setProductVOList(List<CartProductVO> productVOList) {
        this.productVOList = productVOList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public boolean isAllCheck() {
        return allCheck;
    }

    public void setAllCheck(boolean allCheck) {
        this.allCheck = allCheck;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
