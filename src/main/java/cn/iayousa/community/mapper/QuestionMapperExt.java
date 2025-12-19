package cn.iayousa.community.mapper;

import cn.iayousa.community.model.Question;

import java.util.List;

public interface QuestionMapperExt {
    int incViewCount(Question record);

    int incCommentCount(Question record);

    List<Question> selectByRelatedTags(Question record);
}
