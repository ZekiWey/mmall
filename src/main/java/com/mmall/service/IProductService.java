package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVO;

/**
 * Created by Administrator on 2017/12/25.
 */
public interface IProductService {
    public ServerResponse saveOrUpdate(Product product);

    public  ServerResponse setProductStatus(Integer productId,Integer status);

    public ServerResponse<ProductDetailVO> manageProductDetail(Integer productId);

    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    public ServerResponse<PageInfo> searchProduct(String productName,Integer id,Integer pageNum,Integer pageSize);

    public ServerResponse<ProductDetailVO> productDetail(Integer productId);

    public ServerResponse<PageInfo> list(String productName,Integer categoryId,String orderBy,int pageNum,int pageSize);

}
