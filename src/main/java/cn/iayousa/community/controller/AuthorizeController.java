package cn.iayousa.community.controller;

import cn.iayousa.community.dto.AccessTokenDTO;
import cn.iayousa.community.dto.GithubUser;
import cn.iayousa.community.mapper.UserMapper;
import cn.iayousa.community.model.User;
import cn.iayousa.community.provider.GithubProvide;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvide githubProvide;
    @Autowired
    private UserMapper userMapper;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name =  "state") String state,
                           HttpServletResponse response) throws IOException {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        String accessToken = githubProvide.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvide.getUser(accessToken);

        if(githubUser != null){
            //登陆成功
            User user = new User();
            String token = UUID.randomUUID().toString(); //通过随机生成的UUID作为token
            //把获取到的用户信息以及生成的token保存到数据库
            user.setName(githubUser.getName());
            user.setAccount_id(String.valueOf(githubUser.getId()));
            user.setToken(token);
            user.setGmt_create(System.currentTimeMillis());
            user.setGmt_modified(System.currentTimeMillis());
            userMapper.insert(user);
            //将token作为cookie保存到浏览器
            response.addCookie(new Cookie("token", token));
        }
        else {
            //登陆失败
        }
        return "redirect:/";
    }
}
