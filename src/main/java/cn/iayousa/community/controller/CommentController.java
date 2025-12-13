package cn.iayousa.community.controller;

import cn.iayousa.community.dto.CommentDTO;
import cn.iayousa.community.dto.ResultDTO;
import cn.iayousa.community.exception.CustomizeErrorCode;
import cn.iayousa.community.model.Comment;
import cn.iayousa.community.model.User;
import cn.iayousa.community.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    @ResponseBody
    @RequestMapping(value =  "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentDTO commentDTO,
                       HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if(user == null){
            return ResultDTO.errorOf(CustomizeErrorCode.USER_NOT_LOGIN);
        }
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);
        System.out.println(commentDTO.getParentId() + " " + commentDTO.getContent() + " " + commentDTO.getType());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        comment.setCommentatorId(user.getId());
        commentService.insert(comment);
        return ResultDTO.successOf();
    }
}
