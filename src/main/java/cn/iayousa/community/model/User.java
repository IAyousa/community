package cn.iayousa.community.model;

import lombok.Data;

//数据库中的持久化对象，与user表对应。是与数据库交互的实体类
@Data
public class User {
    private Integer id;
    private String name;
    private String accountId;
    private String token;
    private Long gmtCreate;
    private Long gmtModified;
    private String avatarUrl;
}
