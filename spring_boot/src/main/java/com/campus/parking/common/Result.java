package com.campus.parking.common;

import lombok.Data;
import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private int code;       // 状态码：200成功，500失败等
    private String message; // 提示信息
    private T data;         // 携带的数据

    // 私有化构造器，强制使用静态方法构建
    private Result() {}
    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功返回（不带数据）
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    // 成功返回（带数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 成功返回（自定义消息和数据）
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    // 失败返回
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    // 失败返回（自定义状态码）
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }
}
