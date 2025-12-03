package cn.iayousa.community.model;

import lombok.Data;
//数据库中的持久化对象，与question表对应。是与数据库交互的实体类
@Data
public class Question {
    private Integer id;
    private String title;
    private String description;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer creatorId;
    private Long commentCount;
    private Long viewCount;
    private Long likeCount;
    private String tag;
}
