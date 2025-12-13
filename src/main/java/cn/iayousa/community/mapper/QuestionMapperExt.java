package cn.iayousa.community.mapper;

import cn.iayousa.community.model.Question;

public interface QuestionMapperExt {
    int incViewCount(Question record);

    int incCommentCount(Question record);
}
