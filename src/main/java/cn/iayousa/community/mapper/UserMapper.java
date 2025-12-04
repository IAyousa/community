package cn.iayousa.community.mapper;

import cn.iayousa.community.model.User;
import org.apache.ibatis.annotations.*;

//数据库交互用接口，用于管理user表的CRUD操作，通常使用 MyBatis 或 JPA 进行实现
@Mapper
public interface  UserMapper {
    @Insert("insert into user (name, account_id, token, gmt_create, gmt_modified, avatar_url) values (#{name}, #{accountId}, #{token}, #{gmtCreate}, #{gmtModified}, #{avatarUrl})")
    void insert(User user);

    @Select("select * from user where token = #{token}")
    User findByToken(@Param(value = "token") String token);

    @Select("select * from user where id = #{id}")
    User findById(@Param(value = "id") Integer creatorId);

    @Select("select * from user where account_id = #{accountId}")
    User findByAccountId(@Param(value = "accountId") String accountId);

    @Update("update user set name = #{name}, avatar_url = #{avatarUrl}, token = #{token}, gmt_modified = #{gmtModified} where id = #{id}")
    void update(User dbUser);
}
