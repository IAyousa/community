package cn.iayousa.community.dto;

import cn.iayousa.community.model.User;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private Long parentId;
    private Integer type;
    private String content;
    private Long commentatorId;
    private Long gmtCreate;
    private Long gmtModified;
    private Long likeCount;
    private User user;
}
