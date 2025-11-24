package cn.iayousa.community.model;

import lombok.Data;

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
