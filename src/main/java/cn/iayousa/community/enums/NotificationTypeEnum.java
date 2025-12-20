package cn.iayousa.community.enums;

public enum NotificationTypeEnum {
    // 核心通知类型
    REPLY_QUESTION(1, "回复了你的问题", "question", "bi-chat-dots", "text-primary"),
    REPLY_COMMENT(2, "回复了你的评论", "comment", "bi-chat-dots", "text-info"),
    LIKE_COMMENT(3, "赞了你的评论", "like", "bi-hand-thumbs-up", "text-success"),
    SYSTEM_NOTICE(4, "系统通知", "system", "bi-info-circle", "text-secondary")
    ;

    private final Integer type;
    private final String name;
    private final String category;
    private final String iconClass;
    private final String colorClass;

    NotificationTypeEnum(Integer type, String name, String category, String iconClass, String colorClass) {
        this.type = type;
        this.name = name;
        this.category = category;
        this.iconClass = iconClass;
        this.colorClass = colorClass;
    }

    public static NotificationTypeEnum getByType(Integer type) {
        for (NotificationTypeEnum notificationTypeEnum : NotificationTypeEnum.values()) {
            if (notificationTypeEnum.getType().equals(type)) {
                return notificationTypeEnum;
            }
        }
        return null;
    }

    public static String getNameByType(Integer type) {
        for (NotificationTypeEnum notificationTypeEnum : NotificationTypeEnum.values()) {
            if (notificationTypeEnum.getType().equals(type)) {
                return notificationTypeEnum.getName();
            }
        }
        return null;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getIconClass() {
        return iconClass;
    }

    public String getColorClass() {
        return colorClass;
    }
}
