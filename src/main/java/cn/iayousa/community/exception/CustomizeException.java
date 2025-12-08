package cn.iayousa.community.exception;

public class CustomizeException extends RuntimeException {
    private String message;

    public CustomizeException(CustomizeErrorCode errorCode) {
        this.message = errorCode.getMessage();
    }

    public String getMessage() {
        return message;
    }
}
