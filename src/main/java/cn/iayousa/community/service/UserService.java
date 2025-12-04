package cn.iayousa.community.service;

import cn.iayousa.community.mapper.UserMapper;
import cn.iayousa.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {
        User dbUser = userMapper.findByAccountId(user.getAccountId());
        if (dbUser == null) {
            //数据库中没有这个用户时，插入新用户数据
            user.setGmtCreate(System.currentTimeMillis());
            userMapper.insert(user);
        }
        else{
            //数据库中有这个用户时，更新用户数据
            dbUser.setName(user.getName());
            dbUser.setAvatarUrl(user.getAvatarUrl());
            dbUser.setToken(user.getToken());
            dbUser.setGmtModified(System.currentTimeMillis());
            userMapper.update(dbUser);
        }
    }
}
