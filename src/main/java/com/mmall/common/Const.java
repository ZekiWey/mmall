package com.mmall.common;

/**
 * Created by Administrator on 2017/12/23.
 */
public class Const {

    public static final  String CURRENT_USER = "currentUser";
    public static final  String USER_NAME = "username";
    public static final  String EMAIL = "email";

    public interface Role{
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1; //管理员
    }

}
