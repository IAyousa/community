package cn.iayousa.community.mapper;

import cn.iayousa.community.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper {
    @Insert("insert into question (title, description, gmt_create, gmt_modified, creator_id, tag) values (#{title}, #{description}, #{gmtCreate}, #{gmtModified}, #{creatorId}, #{tag})")
    public void create(Question question);

    @Select("select * from question")
    List<Question> list();
}
