package cn.iayousa.community.service;

import cn.iayousa.community.mapper.UserMapper;
import cn.iayousa.community.model.User;
import cn.iayousa.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().
                andAccountIdEqualTo(user.getAccountId());
        List<User> dbUsers = userMapper.selectByExample(userExample);
        if (dbUsers.isEmpty()) {
            //数据库中没有这个用户时，插入新用户数据
            user.setGmtCreate(System.currentTimeMillis());
            userMapper.insert(user);
        }
        else{
            //数据库中有这个用户时，更新用户数据
            User dbUser = dbUsers.get(0);
            dbUser.setName(user.getName());
            dbUser.setAvatarUrl(user.getAvatarUrl());
            dbUser.setToken(user.getToken());
            dbUser.setGmtModified(System.currentTimeMillis());
            userMapper.updateByPrimaryKeySelective(dbUser);
        }
    }
}
