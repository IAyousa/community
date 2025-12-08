package cn.iayousa.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode {
    QUESTION_NOT_FOUND("你访问的问题不存在或已被删除"),
    UPDATE_FAIL("啊嚄，问题更新失败了，请联系管理员试试");

    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    CustomizeErrorCode(String message) {
        this.message = message;
    }
}
