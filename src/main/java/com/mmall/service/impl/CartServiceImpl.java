package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVO;
import com.mmall.vo.CartVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jca.cci.connection.ConnectionFactoryUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/12/27.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<CartVO> add(Integer productId,Integer userId,Integer count){
        if(productId == null || count == null){
            ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId,productId);
        if(cart == null){
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(count);
            cart.setChecked(Const.Cart.CHECKED);

            cartMapper.insert(cart);
        }
        else{
            cart.setQuantity(cart.getQuantity()+count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return  list(userId);
    }

    public ServerResponse<CartVO> update(Integer productId,Integer userId,Integer count){
        if(productId == null || count == null){
            ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if(cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKey(cart);
        return  list(userId);
    }

    public ServerResponse<CartVO> delete(String productIds,Integer userId){
        List<String> productList = Splitter.on(',').splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)){
            ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int result = cartMapper.deleteCartProductByUserIdAndProductId(userId,productList);

        return  list(userId);
    }

    public ServerResponse<CartVO> list(Integer userId){
        CartVO cartVO = getCartVoLimit(userId);
        return  ServerResponse.createBySuccess(cartVO);
    }

    public ServerResponse<CartVO> selectOrUnSelect(Integer userId,Integer productId,Integer checked){
        cartMapper.checkedOrUncheckedProduct(userId,productId,checked);
        return list(userId);
    }

    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if(userId == null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.getCartProductCount(userId));
    }


    private CartVO getCartVoLimit(Integer userId){
        CartVO cartVO = new CartVO();

        List<Cart> cartList = cartMapper.selectCartByUserID(userId);
        if(cartList != null) {
            BigDecimal cartTotalPrice = new BigDecimal("0");
            List<CartProductVO> cartProductVOList = Lists.newArrayList();
            for (Cart cartItem : cartList) {
                CartProductVO cartProductVO = new CartProductVO();
                cartProductVO.setId(cartItem.getId());
                cartProductVO.setProductId(cartItem.getProductId());
                cartProductVO.setUserId(cartItem.getUserId());

                Product product = productMapper.selectByPrimaryKey(cartProductVO.getProductId());
                if (product != null) {
                    cartProductVO.setProductName(product.getName());
                    cartProductVO.setProductMainImg(product.getMainImage());
                    cartProductVO.setProductPrice(product.getPrice());
                    cartProductVO.setProductStatus(product.getStatus());
                    cartProductVO.setProductSubTitle(product.getSubtitle());
                    cartProductVO.setProductStock(product.getStock());
                    int buyLimit = 0;
                    if (cartItem.getQuantity() > cartProductVO.getProductStock()) {
                        buyLimit = cartProductVO.getProductStock();
                        Cart cart = new Cart();
                        cart.setId(cartItem.getId());
                        cart.setQuantity(buyLimit);
                        cartMapper.updateByPrimaryKeySelective(cart);
                        cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                    } else {
                        cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                        buyLimit = cartItem.getQuantity();
                    }
                    cartProductVO.setQuantity(buyLimit);

                    cartProductVO.setProductTotalPrice(BigDecimalUtil.mul(cartProductVO.getProductPrice().doubleValue(), cartProductVO.getQuantity()));

                    System.out.println(cartProductVO.getProductPrice());
                    cartProductVO.setProductCheck(cartItem.getChecked());
                }else{
                    return null;
                }
                if (cartItem.getChecked() == Const.Cart.CHECKED) {
                    cartTotalPrice = (BigDecimalUtil.add(cartTotalPrice.doubleValue()
                            , cartProductVO.getProductTotalPrice().doubleValue()));
                }
                cartProductVOList.add(cartProductVO);
            }
            cartVO.setCartTotalPrice(cartTotalPrice);
            cartVO.setProductVOList(cartProductVOList);
            cartVO.setAllCheck(isAllCheck(userId));
            cartVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        }
        else {
            return null;
        }
        return  cartVO;
    }

    private boolean isAllCheck(Integer userId){
        if(userId == null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}
