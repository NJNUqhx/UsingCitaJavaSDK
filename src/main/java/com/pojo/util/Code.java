package com.pojo.util;

public enum Code {

    FAILURE(100, "error"),

    // 成功类状态码
    SUCCESS(200, "succeed"),

    // 请求错误类状态码
    UPLOAD_FAIL(10001, "fail to upload to chain"),
    QUERY_FAIL(10002, "query fail"),
    UPDATE_FAIL(10003, "update fail"),
    METHOD_NOT_FOUND(10004, "contract method not found"),
    MARSHAL_FAIL(10005, "marshal fail"),
    UNMARSHAL_FAIL(10006, "unmarshal fail"),
    INVALID_PARAMETERS(10007, "invalid parameters");

    private final int code;
    private final String message;

    // 构造方法
    Code(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // 获取状态码
    public int getCode() {
        return code;
    }

    // 获取状态信息
    public String getMessage() {
        return message;
    }
}
