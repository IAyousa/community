package cn.iayousa.community.dto;

import lombok.Data;

//数据传输用类，存储GitHub响应的信息
@Data
public class AccessTokenDTO {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String state;
}
