package cn.iayousa.community.controller;

import cn.iayousa.community.dto.AccessTokenDTO;
import cn.iayousa.community.dto.GithubUser;
import cn.iayousa.community.model.User;
import cn.iayousa.community.provider.GithubProvide;
import cn.iayousa.community.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvide githubProvide;
    @Autowired
    private UserService userService;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name =  "state") String state,
                           HttpServletResponse response,
                           RedirectAttributes redirectAttributes) throws IOException {
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
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setToken(token);
            user.setAvatarUrl(githubUser.getAvatar_url());
            userService.createOrUpdate(user);
            //将token作为cookie保存到浏览器
            response.addCookie(new Cookie("token", token));
        }
        else {
            //登陆失败
            // 添加错误信息到重定向属性
            redirectAttributes.addFlashAttribute("error", "GitHub登录失败，请重试");
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        //退出登录时
        //移除session
        request.getSession().removeAttribute("user");
        //移除cookie
        //方法为新创建一个cookie对象，并将其存储时间设置为0(即立即删除)
        Cookie cookie =  new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");  //设置与登录时相同的路径，确保能正确删除
        response.addCookie(cookie);
        return  "redirect:/";
    }
}
