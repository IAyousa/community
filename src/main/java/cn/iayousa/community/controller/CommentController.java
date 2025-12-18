package cn.iayousa.community.controller;

import cn.hutool.core.util.StrUtil;
import cn.iayousa.community.dto.CommentCreateDTO;
import cn.iayousa.community.dto.CommentDTO;
import cn.iayousa.community.dto.ResultDTO;
import cn.iayousa.community.enums.CommentTypeEnum;
import cn.iayousa.community.exception.CustomizeErrorCode;
import cn.iayousa.community.model.Comment;
import cn.iayousa.community.model.User;
import cn.iayousa.community.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    @ResponseBody
    @RequestMapping(value =  "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if(user == null){
            return ResultDTO.errorOf(CustomizeErrorCode.USER_NOT_LOGIN);
        }
        if(StrUtil.isBlank(commentCreateDTO.getContent())){
            return ResultDTO.errorOf(CustomizeErrorCode.COMMENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentCreateDTO, comment);
        System.out.println(commentCreateDTO.getParentId() + " " + commentCreateDTO.getContent() + " " + commentCreateDTO.getType());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        comment.setCommentatorId(user.getId());
        commentService.insert(comment);
        return ResultDTO.successOf();
    }

    @ResponseBody
    @RequestMapping(value =  "/comment/{id}", method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comments(@PathVariable(name = "id") Long id) {
        List<CommentDTO> commentDTOS = commentService.listById(id, CommentTypeEnum.COMMENT);
        return ResultDTO.successOf(commentDTOS);
    }
}
