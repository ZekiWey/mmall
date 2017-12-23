package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by Administrator on 2017/12/23.
 */
public interface IUserService {

    public ServerResponse<User> userLogin(String username,String password);

    public ServerResponse<User> userRegister(User user);

    public ServerResponse<String> checkVaild(String str,String type);

    public ServerResponse<String> forgetGetQuestion(String username);

    public ServerResponse<String> checkAnswer(String username,String question,String answer);

    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgeToken);

    public ServerResponse<String>  resetPassword(String passwordOld,String passwordNew,User user);

    public ServerResponse<User> updateUserInfo(User user);

    public ServerResponse<User> getInformation(Integer id);
}
