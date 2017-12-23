package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by Administrator on 2017/12/23.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper mapper;

//    实现用户登陆
    @Override
    public ServerResponse<User> userLogin(String username,String password) {
        int count =  mapper.checkUserName(username);
        if(count == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        String md5password = MD5Util.MD5EncodeUtf8(password);
//        MD5加密

        User user = mapper.selectLogin(username,md5password);

        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccess(user);
    }

//    实现用户的注册功能
    public ServerResponse<User> userRegister(User user) {
        ServerResponse vaildResponse = this.checkVaild(user.getUsername(),Const.USER_NAME);
        if(!vaildResponse.isSuccess()){
            return vaildResponse;
        }

        vaildResponse = this.checkVaild(user.getEmail(),Const.EMAIL);
        if(!vaildResponse.isSuccess()){
            return vaildResponse;
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        user.setRole(Const.Role.ROLE_CUSTOMER);

        int resault = mapper.insert(user);
        if (resault > 0) {
            return ServerResponse.createBySuccessMsg("注册成功");
        }

        return ServerResponse.createByErrorMessage("注册失败");
    }

    public ServerResponse<String> checkVaild(String str,String type){
        if(StringUtils.isNotBlank(type)){
            if(type.equals(Const.USER_NAME)){
                int resault = mapper.checkUserName(str);
                if(resault > 0){
                    return  ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(type.equals(Const.EMAIL)){
                int resault = mapper.checkEmail(str);
                if(resault > 0){
                    return  ServerResponse.createByErrorMessage("邮箱已被注册");
                }
            }
        }
        else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMsg("校验成功");
    }

    public ServerResponse<String> forgetGetQuestion(String username){
        ServerResponse vaildResponse = this.checkVaild(username,Const.USER_NAME);
        if(vaildResponse.isSuccess()){
           return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = mapper.forgetGetQuestion(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("没有设置找回密码的问题");
    }
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resault = mapper.checkAnswer(username, question, answer);
        if(resault > 0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey("token_"+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题回答错误");
    }
    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgeToken){
        if(StringUtils.isBlank(forgeToken)){
            return ServerResponse.createByErrorMessage("参数错误，Toekn需要传递");
        }
        ServerResponse vaildResponse = this.checkVaild(username,Const.USER_NAME);
        if(vaildResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("Token不存在或无效");
        }
        if (StringUtils.equals(token,forgeToken)){
            String md5PasswordNew = MD5Util.MD5EncodeUtf8(passwordNew);
            int resault = mapper.updatePasswordByUserName(username,md5PasswordNew);
            if(resault > 0) {
                return ServerResponse.createBySuccessMsg("密码修改成功");
            }
        }else {
            return ServerResponse.createByErrorMessage("Token错误请重新回答问题");
        }

        return ServerResponse.createByErrorMessage("密码修改失败");
    }

    public ServerResponse<String>  resetPassword(String passwordOld,String passwordNew,User user){
        int result = mapper.checPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(result > 0){
            user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
            result = mapper.updateByPrimaryKeySelective(user);
            if(result > 0){
                return ServerResponse.createBySuccessMsg("密码更新成功");
            }
            return ServerResponse.createByErrorMessage("密码更新失败");
        }
        return  ServerResponse.createByErrorMessage("原密码输入错误");
    }

    public ServerResponse<User> updateUserInfo(User user){
        int result = mapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(result > 0){
            return  ServerResponse.createByErrorMessage("该邮箱已被占用，请尝试别的邮箱");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        result = mapper.updateByPrimaryKeySelective(updateUser);
        if(result > 0){
            return ServerResponse.createBySuccess("用户信息更新成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }
    public ServerResponse<User> getInformation(Integer id){
        User user = mapper.selectByPrimaryKey(id);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户信息");
        }
        return ServerResponse.createBySuccess(user);
    }
}
