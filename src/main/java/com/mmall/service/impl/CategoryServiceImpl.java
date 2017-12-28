package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/12/25.
 */
@Service("categoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper mapper;

    public ServerResponse addCategory(String cateName,Integer parentId){
        if(parentId == null || StringUtils.isBlank(cateName)){
            ServerResponse.createByErrorMessage("添加品类参数错误参数错误");
        }
        Category category = new Category();
        category.setName(cateName);
        category.setParentId(parentId);
        category.setStatus(true);
        int result = mapper.insert(category);
        if(result > 0){
            return ServerResponse.createBySuccessMsg("添加品类成功");
        }
        return  ServerResponse.createBySuccessMsg("添加品类失败");
    }

    public  ServerResponse setCategoryName(Integer categoryId,String categoryName){
        if(categoryId == null && StringUtils.isBlank(categoryName)){
            ServerResponse.createByErrorMessage("修改品类参数错误参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int result = mapper.updateByPrimaryKeySelective(category);
        if(result > 0){
            return  ServerResponse.createBySuccessMsg("修改品类名称成功");
        }
        return ServerResponse.createByErrorMessage("修改品类名称失败");
    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        if(categoryId == null){
            return ServerResponse.createByErrorMessage("查询参数有误");
        }
        List<Category> categories = mapper.selectCategoryChildrenByParentId(categoryId);

        if(CollectionUtils.isEmpty(categories)){
            logger.info("未找到子节点");
        }
        return ServerResponse.createBySuccess(categories);
    }


    public ServerResponse<List<Integer>> getChildrenAndDeepParallelCategory(Integer categoryId){
        Set<Category> categories = Sets.newHashSet();
        findChildCategory(categories,categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();

        if (categories != null){
            for (Category category_item : categories){
                categoryIdList.add(category_item.getId());
            }
            return ServerResponse.createBySuccess(categoryIdList);
        }
        return  ServerResponse.createByErrorMessage("没有找到子节点");
    }

    private Set<Category> findChildCategory(Set<Category> categories,Integer categoryId){
        Category category = mapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categories.add(category);
        }
        List<Category> list_categories = getChildrenParallelCategory(categoryId).getData();
        for (Category category_item : list_categories){
            findChildCategory(categories,category_item.getId());
        }
        return categories;
    }
}
