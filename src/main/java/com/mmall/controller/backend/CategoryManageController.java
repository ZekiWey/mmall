package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/12/25.
 */
@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ICategoryService categoryService;

    @RequestMapping(value = "add_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCatgory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") Integer parentId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("当前用户未登录");
        }
        if(userService.checkAdmin(user).isSuccess()){
            return categoryService.addCategory(categoryName,parentId);
        }

        return  ServerResponse.createByErrorMessage("当前用户不是管理员");
    }
    @RequestMapping(value = "set_category_name.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session,Integer categoryId,String categoryName){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("当前用户未登录");
        }
        if(userService.checkAdmin(user).isSuccess()){
            return categoryService.setCategoryName(categoryId,categoryName);
        }

        return  ServerResponse.createByErrorMessage("当前用户不是管理员");
    }

    @RequestMapping(value = "get_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session,Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("当前用户未登录");
        }
        if(userService.checkAdmin(user).isSuccess()){
            return categoryService.getChildrenParallelCategory(categoryId);
        }
        return  ServerResponse.createByErrorMessage("当前用户不是管理员");
    }


    @RequestMapping(value = "get_deep_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getChildrenAndDeepParallelCategory(HttpSession session,Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("当前用户未登录");
        }
        if(userService.checkAdmin(user).isSuccess()){
            return categoryService.getChildrenAndDeepParallelCategory(categoryId);
        }
        return  ServerResponse.createByErrorMessage("当前用户不是管理员");
    }

}
