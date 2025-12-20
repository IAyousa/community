package cn.iayousa.community.controller;

import cn.iayousa.community.enums.NotificationTypeEnum;
import cn.iayousa.community.mapper.NotificationMapper;
import cn.iayousa.community.model.Notification;
import cn.iayousa.community.model.User;
import cn.iayousa.community.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationMapper notificationMapper;

    @GetMapping("/notification/{id}")
    public String notifications(@PathVariable(name = "id") Long id,
                                HttpServletRequest request,
                                Model model){
        //用户登陆检验
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            model.addAttribute("error", "用户未登录");
            return "redirect:/";
        }
        notificationService.read(id, user.getId());
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if(notification.getType() == NotificationTypeEnum.REPLY_QUESTION.getType()
        || notification.getType() == NotificationTypeEnum.REPLY_COMMENT.getType()){
            return "redirect:/question/"+notification.getOuterId();
        }
        else{
            return "redirect:/";
        }
    }
}
