package cn.iayousa.community.provider;

import cn.iayousa.community.dto.AccessTokenDTO;
import cn.iayousa.community.dto.GithubUser;
import com.alibaba.fastjson2.JSON;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

//GitHub相关操作工具类，用于封装获取GitHub相关信息的操作函数
@Component
public class GithubProvide {
    public String getAccessToken(AccessTokenDTO accessTokenDTO) throws IOException {
        MediaType mediaType = MediaType.get("application/json");
        // 创建带超时配置的 OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)  // 连接超时 30秒
                .readTimeout(60, TimeUnit.SECONDS)     // 读取超时 30秒
                .writeTimeout(60, TimeUnit.SECONDS)    // 写入超时 30秒
                .build();

        RequestBody body = RequestBody.create(JSON.toJSONString(accessTokenDTO), mediaType);
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String token = string.split("&")[0].split("=")[1];
            return token;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public GithubUser getUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                //github新版请求方式
                .url("https://api.github.com/user")
                .header("Authorization","token "+accessToken) //通过get请求头携带令牌来获取授权
//               .url("https://api.github.com/user?access_token=" + accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
        }

        return null;
    }

}
