package com.poplar.utils;

/**
 * by poplar created on 2020/1/9
 */
public class ResultEnvelope<T> {
    //响应码
    private int code;

    private T data;

    private String message;

    public ResultEnvelope() {
    }

    public ResultEnvelope(int code) {
        this.code = code;
    }

    public ResultEnvelope(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResultEnvelope(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public ResultEnvelope(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultEnvelope<T> success() {
        return new ResultEnvelope<T>(0);
    }

    public ResultEnvelope<T> success(T t) {
        return new ResultEnvelope<>(0, t);
    }

    public ResultEnvelope<T> success(String message) {
        return new ResultEnvelope<>(0, message);
    }

    public ResultEnvelope<T> success(int code, String message) {
        return new ResultEnvelope<>(code, message);
    }

    public ResultEnvelope<T> failure(String message) {
        return new ResultEnvelope<>(-1, message);
    }

    public ResultEnvelope<T> failure(int code, String message) {
        return new ResultEnvelope<>(code, message);
    }

    public ResultEnvelope<T> failure() {
        return new ResultEnvelope<>(-1);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
