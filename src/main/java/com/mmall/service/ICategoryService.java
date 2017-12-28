package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by Administrator on 2017/12/25.
 */
public interface ICategoryService {

    public ServerResponse addCategory(String cateName, Integer parentId);

    public  ServerResponse setCategoryName(Integer categoryId,String categoryName);

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    public ServerResponse<List<Integer>> getChildrenAndDeepParallelCategory(Integer categoryId);
}
