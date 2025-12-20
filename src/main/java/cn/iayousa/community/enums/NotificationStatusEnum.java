package cn.iayousa.community.enums;

public enum NotificationStatusEnum {
    UNREAD(0),
    READ(1)
    ;
    private int value;
    NotificationStatusEnum(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }

}
