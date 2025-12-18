package cn.iayousa.community.mapper;

import cn.iayousa.community.model.Comment;

public interface CommentMapperExt {
    int incCommentCount(Comment record);
}
