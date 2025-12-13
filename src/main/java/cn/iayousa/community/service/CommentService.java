package cn.iayousa.community.service;

import cn.iayousa.community.enums.CommentTypeEnum;
import cn.iayousa.community.exception.CustomizeErrorCode;
import cn.iayousa.community.exception.CustomizeException;
import cn.iayousa.community.mapper.CommentMapper;
import cn.iayousa.community.mapper.QuestionMapper;
import cn.iayousa.community.mapper.QuestionMapperExt;
import cn.iayousa.community.model.Comment;
import cn.iayousa.community.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapperExt questionMapperExt;

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
        }
        else{
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment == null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insertSelective(comment);
        }
    }
}
