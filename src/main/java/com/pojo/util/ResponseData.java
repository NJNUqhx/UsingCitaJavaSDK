package com.pojo.util;

public class ResponseData<T> {

    public Integer code; // 返回的响应编码
    public String message; //返回的信息
    public T result; //返回的泛型类

    // 构造方法
    public ResponseData(Code code, T result) {
        this.code = code.getCode();
        this.message = code.getMessage();
        this.result = result;
    }

    // 工厂方法：创建成功响应
    public static <T> ResponseData<T> success(T result) {
        return new ResponseData<>(Code.SUCCESS, result);
    }

    // 工厂方法：创建错误响应
    public static <T> ResponseData<T> error(Code code) {
        return new ResponseData<>(code, null);
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}

