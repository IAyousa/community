package cn.iayousa.community.dto;

import cn.iayousa.community.enums.NotificationTypeEnum;
import cn.iayousa.community.model.User;
import lombok.Data;

import java.util.Date;

@Data
public class NotificationDTO {
    private Long id;
    private Long notifierId;
    private Long receiverId;
    private Long outerId;
    private Integer type;
    private Integer status;
    private Long gmtCreate;
    private String content;
    // 关联字段（查询时关联）
    private User notifier;
    //被回复的具体内容
    private String repliedQuestionTitle;  // 被回复的问题标题
    private String repliedCommentContent; // 被回复的评论内容
    private String systemNoticeContent;   // 系统通知内容

    // 计算字段（通过方法动态生成）
    public NotificationTypeEnum getTypeEnum() {
        return NotificationTypeEnum.getByType(type);
    }

    public String getTypeName() {
        return NotificationTypeEnum.getNameByType(type);
    }

    public String getFormattedTime() {
        // 时间格式化逻辑
        return new Date(gmtCreate).toString();
    }

    public String getIconClass() {
        NotificationTypeEnum typeEnum = getTypeEnum();
        return typeEnum != null ? typeEnum.getIconClass() : "bi-bell";
    }

    public String getColorClass() {
        NotificationTypeEnum typeEnum = getTypeEnum();
        return typeEnum != null ? typeEnum.getColorClass() : "text-secondary";
    }
    //获取要显示的具体内容
    public String getDisplayContent() {
        if (repliedQuestionTitle != null && !repliedQuestionTitle.isEmpty()) {
            return repliedQuestionTitle;
        }
        if (repliedCommentContent != null && !repliedCommentContent.isEmpty()) {
            return repliedCommentContent;
        }
        if (systemNoticeContent != null && !systemNoticeContent.isEmpty()) {
            return systemNoticeContent;
        }
        return content != null ? content : "";
    }

    // 获取内容类型提示
    public String getContentTypeHint() {
        NotificationTypeEnum typeEnum = getTypeEnum();
        if (typeEnum == null) return "内容";

        switch (typeEnum) {
            case REPLY_QUESTION:
                return "问题";
            case REPLY_COMMENT:
            case LIKE_COMMENT:
                return "评论";
            case SYSTEM_NOTICE:
                return "通知";
            default:
                return "内容";
        }
    }
}
