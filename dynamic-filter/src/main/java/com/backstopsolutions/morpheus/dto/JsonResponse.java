package com.backstopsolutions.morpheus.dto;

import com.backstopsolutions.morpheus.core.CodeEnum;

import java.util.ArrayList;

public class JsonResponse<T> {
    
    private Integer code;
    
    private String msg;
    
    private T data;

    protected JsonResponse() {}
    
    public JsonResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static JsonResponse<Object> success() {
        return new JsonResponse<>(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMsg(), new ArrayList<>());
    }

    public static <T> JsonResponse<T> success(T data) {
        return new JsonResponse<>(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMsg(), data);
    }

    public static JsonResponse<Object> success(Integer code, String msg) {
        return new JsonResponse<>(code, msg, new ArrayList<>());
    }

    public static <T> JsonResponse<T> success(String msg, T data) {
        return new JsonResponse<>(CodeEnum.SUCCESS.getCode(), msg, data);
    }

    public static <T> JsonResponse<T> success(Integer code, String msg, T data) {
        return new JsonResponse<>(code, msg, data);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
