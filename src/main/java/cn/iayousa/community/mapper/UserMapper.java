package cn.iayousa.community.mapper;

import cn.iayousa.community.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
//数据库交互用接口，用于管理user表的CRUD操作，通常使用 MyBatis 或 JPA 进行实现
@Mapper
public interface  UserMapper {
    @Select("insert into user (name, account_id, token, gmt_create, gmt_modified) values (#{name}, #{account_id}, #{token}, #{gmt_create}, #{gmt_modified})")
    void insert(User user);
}
