package cn.iayousa.community.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
public class CustomizeErrorController implements ErrorController {
    @RequestMapping({"${server.error.path:${error.path:/error}}"})
    public String handleError(HttpServletRequest request, Model model) {
        // 获取错误信息
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus status = HttpStatus.resolve(statusCode != null ? statusCode : 500);

        // 设置默认值
        status = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;

        // 填充模型
        model.addAttribute("timestamp", new Date());
        model.addAttribute("status", status.value());
        model.addAttribute("error", status.getReasonPhrase());
        model.addAttribute("path", request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));

        // 根据状态码设置友好消息
        String message;
        if (status == HttpStatus.NOT_FOUND) {
            message = "您访问的页面不存在";
        } else if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            message = "服务器运作过头了，待会再来吧";
        } else if (status == HttpStatus.FORBIDDEN) {
            message = "没有权限访问此页面";
        } else {
            message = "系统繁忙，请稍后再试";
        }
        model.addAttribute("message", message);

        return "error";
    }

}
