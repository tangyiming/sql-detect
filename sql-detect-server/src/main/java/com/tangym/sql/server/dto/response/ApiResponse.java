package com.tangym.sql.server.dto.response;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.io.Serializable;

public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 3452913614867544640L;

    private T data;

    private int result;

    private String errorMsg = "";

    public ApiResponse() {
    }

    private ApiResponse(int result, String errorMsg) {
        this.result = result;
        this.errorMsg = errorMsg;
    }

    public static <T> ApiResponse<T> succResponse(T data) {
        ApiResponse<T> response = new ApiResponse<>(1, "success");
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> succResponse() {
        return new ApiResponse<>(1, "success");
    }

    public static <T> ApiResponse<T> failResponse(int result, String errorMsg) {
        return new ApiResponse<>(result, errorMsg);
    }

    public static <T> ApiResponse<T> failResponse(T data) {
        ApiResponse<T> response = new ApiResponse<>(0, "请求失败！");
        response.setData(data);
        return response;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getResult() {
        return this.result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
