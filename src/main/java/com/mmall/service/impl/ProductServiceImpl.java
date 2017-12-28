package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVO;
import com.mmall.vo.ProductListVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/25.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper mapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService categoryService;

    public ServerResponse saveOrUpdate(Product product){
        if(product != null){
            if(StringUtils.isNotBlank(product.getSubImages())){
                String[] images = product.getSubImages().split(",");
                product.setMainImage(images[0]);
            }
            if(product.getId() != null) {
                int result = mapper.updateByPrimaryKey(product);
                if (result > 0) {
                    return ServerResponse.createBySuccessMsg("产品更新成功");
                }
                return ServerResponse.createBySuccessMsg("产品更新失败");
            }else {
                int result = mapper.insert(product);
                if(result > 0){
                    return ServerResponse.createBySuccessMsg("新增产品成功");
                }
                return  ServerResponse.createBySuccessMsg("新增产品失败");
            }
        }
        return ServerResponse.createBySuccessMsg("参数传递有误");
    }

    public  ServerResponse setProductStatus(Integer productId,Integer status){
        if(productId == null || status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int result = mapper.updateByPrimaryKeySelective(product);
        if(result > 0){
            return ServerResponse.createBySuccessMsg("产品状态更新成功");
        }
        return  ServerResponse.createBySuccessMsg("产品状态更新失败");
    }

    public ServerResponse<ProductDetailVO> manageProductDetail(Integer productId){
        if(productId == null){
            ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = mapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createBySuccessMsg("产品已下架或不存在");
        }
        ProductDetailVO productDetailVO = assembleProductDetailVO(product);

        return ServerResponse.createBySuccess(productDetailVO);

    }

    private ProductDetailVO assembleProductDetailVO(Product product){
        ProductDetailVO productDetailVO = new ProductDetailVO();
        productDetailVO.setId(product.getId());
        productDetailVO.setCategory_id(product.getCategoryId());
        productDetailVO.setName(product.getName());
        productDetailVO.setMain_image(product.getMainImage());
        productDetailVO.setSub_image(product.getSubImages());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setStock(product.getStock());
        productDetailVO.setDetail(product.getDetail());

        productDetailVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"
                                    ,"http://www.happymmall.com/order/alipay_callback.do"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVO.setParentCategoryId(0);
        }else {
            productDetailVO.setParentCategoryId(category.getParentId());
        }

        productDetailVO.setCreate_time(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVO.setUpdate_time(DateTimeUtil.dateToStr(product.getUpdateTime()));


        return productDetailVO;
    }

    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize){

        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = mapper.getProductList();
        List<ProductListVO> productListVOList = Lists.newArrayList();
        for(Product product:products){
            ProductListVO productListVO = assembleProductListVO(product);
            productListVOList.add(productListVO);
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVOList);
        return ServerResponse.createBySuccess(pageInfo);

    }

    private ProductListVO assembleProductListVO(Product product){
        ProductListVO productListVO = new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setName(product.getName());

        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setSubtitle(product.getSubtitle());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setPrice(product.getPrice());
        productListVO.setStatus(product.getStatus());

        productListVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://www.happymmall.com/order/alipay_callback.do"));

        return productListVO;
    }

    public ServerResponse<PageInfo> searchProduct(String productName,Integer id,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName =new StringBuilder().append('%').append(productName).append('%').toString();
        }
        List<Product> products =  mapper.searchProductList(productName,id);
        List<ProductListVO> productListVOList = new ArrayList<>();
        for (Product product : products){
            ProductListVO productListVO = assembleProductListVO(product);
            productListVOList.add(productListVO);
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVOList);
        return  ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse<ProductDetailVO> productDetail(Integer productId){
        if(productId == null){
            ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = mapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createBySuccessMsg("产品已下架或不存在");
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("该商品已下线");
        }
        ProductDetailVO productDetailVO = assembleProductDetailVO(product);
        return ServerResponse.createBySuccess(productDetailVO);
    }

    public ServerResponse<PageInfo> list(String productName,Integer categoryId,String orderBy,int pageNum,int pageSize){
        if(categoryId == null && StringUtils.isBlank(productName)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(productName)){
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVO> productListVOList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVOList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = categoryService.getChildrenAndDeepParallelCategory(category.getId()).getData();
        }
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append('%').append(productName).append('%').toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] order = orderBy.split("_");
                PageHelper.orderBy(order[0] + " " + order[1]);
            }
        }
        List<Product> productList = mapper.selectProductNameAndCategoryId(StringUtils.isBlank(productName)?null:productName,
                categoryIdList.size() == 0?null:categoryIdList);

        List<ProductListVO> productListVOList = Lists.newArrayList();
        for (Product product :productList){
            ProductListVO productListVO = assembleProductListVO(product);
            productListVOList.add(productListVO);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVOList);

        return ServerResponse.createBySuccess(pageInfo);
    }

}
