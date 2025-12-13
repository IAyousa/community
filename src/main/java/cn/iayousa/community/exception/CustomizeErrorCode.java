package cn.iayousa.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode {
    QUESTION_NOT_FOUND(5001, "该问题不存在或已被删除"),
    UPDATE_FAIL(5002, "啊哦，问题更新失败了，请联系管理员试试"),
    TARGET_PARAM_NOT_FOUND(5003,"未选择任何问题或评论"),
    USER_NOT_LOGIN(5004, "当前用户未登录，请登录后再进行操作"),
    SYSTEM_ERROR(5005, "服务器运作过头了，待会再来吧"),
    TYPE_PARAM_ERROR(5006, "评论类型参数错误"),
    COMMENT_NOT_FOUND(5007, "该评论不存在或已被删除");
    private Integer code;
    private String message;

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }
}
