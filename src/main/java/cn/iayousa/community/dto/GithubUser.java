package cn.iayousa.community.dto;

import lombok.Data;

//数据传输用类，存储GitHub用户信息
@Data
public class GithubUser {
    private String name;
    private String bio;
    private Long id;
    private String avatar_url;
}
