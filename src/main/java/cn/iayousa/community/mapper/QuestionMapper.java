package cn.iayousa.community.mapper;

import cn.iayousa.community.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper {
    @Insert("insert into question (title, description, gmt_create, gmt_modified, creator_id, tag) values (#{title}, #{description}, #{gmt_create}, #{gmt_modified}, #{creator_id}, #{tag})")
    public void create(Question question);
}
