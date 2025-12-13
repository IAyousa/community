package cn.iayousa.community.dto;

import cn.iayousa.community.model.User;
import lombok.Data;
//数据传输用类，存储从question表和user表中查询得到的数据信息
@Data
public class QuestionDTO {
    private Long id;
    private String title;
    private String description;
    private Long gmtCreate;
    private Long gmtModified;
    private Long creatorId;
    private Long commentCount;
    private Long viewCount;
    private Long likeCount;
    private String tag;
    private User user;
}
