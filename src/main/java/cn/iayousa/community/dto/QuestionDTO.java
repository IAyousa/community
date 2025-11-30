package cn.iayousa.community.dto;

import cn.iayousa.community.model.User;
import lombok.Data;

@Data
public class QuestionDTO {
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
    private User user;
}
