package cn.iayousa.community.service;

import cn.iayousa.community.dto.CommentDTO;
import cn.iayousa.community.enums.CommentTypeEnum;
import cn.iayousa.community.exception.CustomizeErrorCode;
import cn.iayousa.community.exception.CustomizeException;
import cn.iayousa.community.mapper.*;
import cn.iayousa.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapperExt questionMapperExt;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapperExt commentMapperExt;
    @Autowired
    private NotificationService notificationService;

    @Transactional //Spring 框架中声明式事务管理的核心注解，它允许你通过简单的注解来管理数据库事务
    public void insert(Comment comment) {
        if(comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }

        if(comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_ERROR);
        }

        if(comment.getType() == CommentTypeEnum.QUESTION.getType()){
            //回复问题
            Question dbQuestion = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(dbQuestion == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insertSelective(comment);
            dbQuestion.setCommentCount(1L);
            questionMapperExt.incCommentCount(dbQuestion);
            // 发送通知（如果问题创建者不是评论者自己）
            if (!dbQuestion.getCreatorId().equals(comment.getCommentatorId())) {
                notificationService.createReplyQuestionNotification(
                        comment.getCommentatorId(),
                        dbQuestion.getId()
                );
            }
        }
        else{
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment == null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insertSelective(comment);
            dbComment.setCommentCount(1L);
            commentMapperExt.incCommentCount(dbComment);
            // 发送通知（如果父评论作者不是评论者自己）
            if (!dbComment.getCommentatorId().equals(comment.getCommentatorId())) {
                notificationService.createReplyCommentNotification(
                        comment.getCommentatorId(),
                        dbComment.getId()
                );
            }
        }
    }

    public List<CommentDTO> listById(Long id, CommentTypeEnum commentTypeEnum) {
        CommentExample example = new CommentExample();
        Integer type = commentTypeEnum.getType();
        example.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type);
        example.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(example);

        if(comments == null || comments.isEmpty()){
            return new ArrayList<>();
        }

        //获取去重的评论用户id
        List<Long> commentators = new ArrayList<>(
                comments.stream().map(
                comment -> comment.getCommentatorId()
                ).collect(Collectors.toSet())
        );
        //利用用户id建立与该用户对象的映射
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(commentators);
        List<User> users = userMapper.selectByExample(userExample);

        Map<Long, User> userMap = users.stream().
                collect(
                        Collectors.toMap(User::getId, user -> user)
                );
        //利用评论列表和用户列表构造传输用的评论对象
        List<CommentDTO> commentDTOS = comments.stream().map(
                comment -> {
                    CommentDTO commentDTO = new CommentDTO();
                    BeanUtils.copyProperties(comment, commentDTO);
                    commentDTO.setUser(userMap.get(comment.getCommentatorId()));
                    return commentDTO;
                }).collect(Collectors.toList());
        return commentDTOS;

    }
}
